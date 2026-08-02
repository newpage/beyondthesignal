import { useMemo, useRef, useState } from "react";
import { Camera2D, type CameraState } from "./Camera2D";

export const useCameraController = () => {
  const camera = useMemo(() => new Camera2D(), []);
  const [state, setState] = useState<CameraState>(camera.snapshot());
  const pointerStart = useRef<{ x: number; y: number } | null>(null);

  return {
    state,
    handlers: {
      onWheel: (event: React.WheelEvent<HTMLDivElement>) => {
        event.preventDefault();
        setState(camera.zoomBy(event.deltaY > 0 ? 0.9 : 1.1));
      },
      onPointerDown: (event: React.PointerEvent<HTMLDivElement>) => {
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
        event.currentTarget.releasePointerCapture(event.pointerId);
      },
    },
  };
};
