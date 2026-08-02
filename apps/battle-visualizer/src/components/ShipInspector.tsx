import type { ShipTelemetry } from "../types";

type Props = Readonly<{
  ship?: ShipTelemetry;
  onClose: () => void;
}>;

const percent = (value: number): string => `${Math.round(value * 100)}%`;

export const ShipInspector = ({ ship, onClose }: Props) => {
  if (!ship) return null;

  const speed = Math.hypot(ship.velocity.x, ship.velocity.y, ship.velocity.z);

  return (
    <aside className="ship-inspector" aria-label="Selected ship details">
      <button className="inspector-close" type="button" onClick={onClose}>×</button>
      <span className="eyebrow">SELECTED VESSEL</span>
      <h2>{ship.name}</h2>
      <span className={`side-badge ${ship.side.toLowerCase()}`}>{ship.side}</span>
      <dl>
        <div><dt>Hull</dt><dd>{percent(ship.hull)}</dd></div>
        <div><dt>Shields</dt><dd>{percent(ship.shields)}</dd></div>
        <div><dt>Speed</dt><dd>{speed.toFixed(2)}</dd></div>
        <div><dt>Heading</dt><dd>{(ship.headingRadians * 180 / Math.PI).toFixed(1)}°</dd></div>
        <div><dt>Target</dt><dd>{ship.selectedTargetId?.slice(0, 8) ?? "None"}</dd></div>
      </dl>
      <div className="status-bars">
        <label>Hull <progress max={1} value={ship.hull} /></label>
        <label>Shield <progress max={1} value={ship.shields} /></label>
      </div>
    </aside>
  );
};
