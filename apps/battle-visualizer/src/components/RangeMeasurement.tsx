import type { Point2D } from "../rendering/camera/Camera2D";

export type RangeMeasurementState = Readonly<{
  startScreen: Point2D;
  endScreen: Point2D;
  startWorld: Point2D;
  endWorld: Point2D;
}>;

type Props = Readonly<{
  measurement?: RangeMeasurementState;
}>;

export const RangeMeasurement = ({ measurement }: Props) => {
  if (!measurement) return null;

  const dx = measurement.endScreen.x - measurement.startScreen.x;
  const dy = measurement.endScreen.y - measurement.startScreen.y;
  const distance = Math.hypot(
    measurement.endWorld.x - measurement.startWorld.x,
    measurement.endWorld.y - measurement.startWorld.y,
  );
  const length = Math.hypot(dx, dy);
  const angle = Math.atan2(dy, dx) * 180 / Math.PI;
  const midpoint = {
    x: (measurement.startScreen.x + measurement.endScreen.x) / 2,
    y: (measurement.startScreen.y + measurement.endScreen.y) / 2,
  };

  return (
    <div className="range-measurement" aria-label={`Measured range ${distance.toFixed(1)}`}>
      <div
        className="range-line"
        style={{
          left: measurement.startScreen.x,
          top: measurement.startScreen.y,
          width: length,
          transform: `rotate(${angle}deg)`,
        }}
      />
      <span style={{ left: midpoint.x, top: midpoint.y }}>
        {distance.toFixed(1)} units
      </span>
    </div>
  );
};
