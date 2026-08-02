import { useEffect, useMemo, useRef, useState } from "react";
import type { BattleFrame } from "../../types";
import { Camera2D, type CameraState, type Point2D } from "./Camera2D";
import type { RangeMeasurementState } from "../../components/RangeMeasurement";

export const useCameraController = (
  frame: BattleFrame,
  selectedShipId?: string,
) => {
  const camera = useMemo(() => new Camera2D(), []);
  const [state, setState] = useState<CameraState>(camera.snapshot());
  const [followingSelection, setFollowingSelection] = useState(false);
  const [measurement, setMeasurement] = useState<RangeMeasurementState>();
  const pointerStart = useRef<Point2D | null>(null);
  const measuring = useRef(false);

  const viewport = (): Point2D => ({
    x: window.innerWidth,
    y: window.innerHeight,
  });

  const reset = () => {
    setFollowingSelection(false);
    setState(camera.reset());
  };

  const fitBattle = () => {
    setFollowingSelection(false);
    setState(camera.fit(
      frame.ships.map((ship) => ({ x: ship.position.x, y: ship.position.y })),
      viewport(),
    ));
  };

  const centerAt = (position: Point2D) => {
    setFollowingSelection(false);
    setState(camera.setPosition(position));
  };

  useEffect(() => {
    if (!followingSelection || !selectedShipId) return;
    const selected = frame.ships.find((ship) => ship.id === selectedShipId);
    if (!selected) return;
    setState(camera.setPosition({
      x: selected.position.x,
      y: selected.position.y,
    }));
  }, [camera, followingSelection, frame, selectedShipId]);

  const toLocalScreen = (
    event: React.PointerEvent<HTMLDivElement>,
  ): Point2D => {
    const bounds = event.currentTarget.getBoundingClientRect();
    return {
      x: event.clientX - bounds.left,
      y: event.clientY - bounds.top,
    };
  };

  return {
    state,
    reset,
    fitBattle,
    centerAt,
    followingSelection,
    setFollowingSelection,
    measurement,
    clearMeasurement: () => setMeasurement(undefined),
    handlers: {
      onWheel: (event: React.WheelEvent<HTMLDivElement>) => {
        event.preventDefault();
        setState(camera.zoomBy(event.deltaY > 0 ? 0.9 : 1.1));
      },
      onDoubleClick: (event: React.MouseEvent<HTMLDivElement>) => {
        event.preventDefault();
        reset();
      },
      onPointerDown: (event: React.PointerEvent<HTMLDivElement>) => {
        if (event.button !== 0 && event.button !== 1) return;
        const screen = toLocalScreen(event);
        pointerStart.current = screen;
        measuring.current = event.shiftKey && event.button === 0;
        if (measuring.current) {
          const world = camera.screenToWorld(screen, viewport());
          setMeasurement({
            startScreen: screen,
            endScreen: screen,
            startWorld: world,
            endWorld: world,
          });
        } else {
          setFollowingSelection(false);
        }
        event.currentTarget.setPointerCapture(event.pointerId);
      },
      onPointerMove: (event: React.PointerEvent<HTMLDivElement>) => {
        const start = pointerStart.current;
        if (!start) return;
        const screen = toLocalScreen(event);

        if (measuring.current) {
          setMeasurement((current) => current ? {
            ...current,
            endScreen: screen,
            endWorld: camera.screenToWorld(screen, viewport()),
          } : current);
          return;
        }

        const current = camera.snapshot();
        setState(camera.panBy({
          x: -(screen.x - start.x) / current.zoom,
          y: -(screen.y - start.y) / current.zoom,
        }));
        pointerStart.current = screen;
      },
      onPointerUp: (event: React.PointerEvent<HTMLDivElement>) => {
        pointerStart.current = null;
        measuring.current = false;
        if (event.currentTarget.hasPointerCapture(event.pointerId)) {
          event.currentTarget.releasePointerCapture(event.pointerId);
        }
      },
    },
  };
};
