import type { BattleFrame } from "../types";
import type { CameraState, Point2D } from "../rendering/camera/Camera2D";

type Props = Readonly<{
  frame: BattleFrame;
  camera: CameraState;
  onCenterAt: (position: Point2D) => void;
}>;

const WIDTH = 220;
const HEIGHT = 145;
const PADDING = 14;

export const TacticalMinimap = ({ frame, camera, onCenterAt }: Props) => {
  const points = frame.ships.map((ship) => ship.position);
  const xs = points.map((point) => point.x);
  const ys = points.map((point) => point.y);
  const minX = Math.min(...xs, -500);
  const maxX = Math.max(...xs, 500);
  const minY = Math.min(...ys, -350);
  const maxY = Math.max(...ys, 350);
  const spanX = Math.max(1, maxX - minX);
  const spanY = Math.max(1, maxY - minY);

  const toMap = (point: Point2D): Point2D => ({
    x: PADDING + ((point.x - minX) / spanX) * (WIDTH - PADDING * 2),
    y: PADDING + ((point.y - minY) / spanY) * (HEIGHT - PADDING * 2),
  });

  const toWorld = (point: Point2D): Point2D => ({
    x: minX + ((point.x - PADDING) / (WIDTH - PADDING * 2)) * spanX,
    y: minY + ((point.y - PADDING) / (HEIGHT - PADDING * 2)) * spanY,
  });

  const cameraPoint = toMap(camera.position);

  return (
    <aside className="tactical-minimap" aria-label="Tactical minimap">
      <svg
        viewBox={`0 0 ${WIDTH} ${HEIGHT}`}
        role="img"
        onClick={(event) => {
          const bounds = event.currentTarget.getBoundingClientRect();
          const x = ((event.clientX - bounds.left) / bounds.width) * WIDTH;
          const y = ((event.clientY - bounds.top) / bounds.height) * HEIGHT;
          onCenterAt(toWorld({ x, y }));
        }}
      >
        <rect className="minimap-background" x="1" y="1" width={WIDTH - 2} height={HEIGHT - 2} rx="8" />
        <line className="minimap-axis" x1={WIDTH / 2} y1={8} x2={WIDTH / 2} y2={HEIGHT - 8} />
        <line className="minimap-axis" x1={8} y1={HEIGHT / 2} x2={WIDTH - 8} y2={HEIGHT / 2} />
        {frame.ships.map((ship) => {
          const point = toMap(ship.position);
          return (
            <circle
              key={ship.id}
              className={`minimap-ship ${ship.side.toLowerCase()}`}
              cx={point.x}
              cy={point.y}
              r="3.5"
            />
          );
        })}
        <circle className="minimap-camera" cx={cameraPoint.x} cy={cameraPoint.y} r="7" />
      </svg>
      <span>TACTICAL MAP · CLICK TO CENTER</span>
    </aside>
  );
};
