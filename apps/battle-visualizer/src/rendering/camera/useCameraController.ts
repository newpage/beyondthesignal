import { useMemo, useRef, useState } from "react";
import type { BattleFrame } from "../../types";
import { Camera2D, type CameraState } from "./Camera2D";

export const useCameraController = (frame: BattleFrame) => {
  const camera = useMemo(() => new Camera2D(), []);
  const [state, setState] = useState<CameraState>(camera.snapshot());
  const pointerStart = useRef<{ x: number; y: number } | null>(null);

  const reset = () => setState(camera.reset());
  const fitBattle = () => setState(camera.fit(
    frame.ships.map((ship) => ({ x: ship.position.x, y: ship.position.y })),
    { x: window.innerWidth, y: window.innerHeight },
  ));

  return {
    state,
    reset,
    fitBattle,
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
        pointerStart.current = { x: event.clientX, y: event.clientY };
        event.currentTarget.setPointerCapture(event.pointerId);
      },
      onPointerMove: (event: React.PointerEvent<HTMLDivElement>) => {
        const start = pointerStart.current;
        if (!start) return;
        const current = camera.snapshot();
        setState(camera.panBy({
          x: -(event.clientX - start.x) / current.zoom,
          y: -(event.clientY - start.y) / current.zoom,
        }));
        pointerStart.current = { x: event.clientX, y: event.clientY };
      },
      onPointerUp: (event: React.PointerEvent<HTMLDivElement>) => {
        pointerStart.current = null;
        if (event.currentTarget.hasPointerCapture(event.pointerId)) {
          event.currentTarget.releasePointerCapture(event.pointerId);
        }
      },
    },
  };
};
