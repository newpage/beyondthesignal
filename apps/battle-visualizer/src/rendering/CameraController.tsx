import {
  useCallback,
  useMemo,
  useRef,
  useState,
} from "react";

type CameraTransform = Readonly<{
  x: number;
  y: number;
  scale: number;
}>;

export const useCameraController = () => {
  const [scale, setScale] = useState(1);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const pointerStart = useRef<{ x: number; y: number } | null>(null);

  const onWheel = useCallback((event: React.WheelEvent) => {
    event.preventDefault();
    setScale((current) =>
      Math.max(
        0.25,
        Math.min(4, current * (event.deltaY > 0 ? 0.9 : 1.1)),
      ),
    );
  }, []);

  const handlers = useMemo(
    () => ({
      onWheel,
      onPointerDown: (event: React.PointerEvent<HTMLDivElement>) => {
        pointerStart.current = {
          x: event.clientX - offset.x,
          y: event.clientY - offset.y,
        };
        event.currentTarget.setPointerCapture(event.pointerId);
      },
      onPointerMove: (event: React.PointerEvent<HTMLDivElement>) => {
        if (!pointerStart.current) {
          return;
        }

        setOffset({
          x: event.clientX - pointerStart.current.x,
          y: event.clientY - pointerStart.current.y,
        });
      },
      onPointerUp: (event: React.PointerEvent<HTMLDivElement>) => {
        pointerStart.current = null;
        event.currentTarget.releasePointerCapture(event.pointerId);
      },
    }),
    [offset.x, offset.y, onWheel],
  );

  const transform: CameraTransform = {
    x: offset.x,
    y: offset.y,
    scale,
  };

  return {
    handlers,
    transform,
  };
};
