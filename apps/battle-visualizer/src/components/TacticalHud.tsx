import type { BattleFrame, ConnectionState } from "../types";

type Props = Readonly<{
  frame: BattleFrame;
  connectionState: ConnectionState;
  zoom: number;
  selectedShipId?: string;
  onResetCamera: () => void;
  onFitBattle: () => void;
}>;

export const TacticalHud = ({
  frame,
  connectionState,
  zoom,
  selectedShipId,
  onResetCamera,
  onFitBattle,
}: Props) => {
  const alliance = frame.ships.filter((ship) => ship.side === "ALLIANCE").length;
  const hostile = frame.ships.length - alliance;

  return (
    <aside className="tactical-hud" aria-label="Battle status">
      <div className="hud-section">
        <span className="hud-label">Battle</span>
        <strong>{frame.battleId.slice(0, 12)}</strong>
      </div>
      <dl>
        <div><dt>Tick</dt><dd>{frame.tick.toLocaleString()}</dd></div>
        <div><dt>Ships</dt><dd>{frame.ships.length}</dd></div>
        <div><dt>Alliance</dt><dd>{alliance}</dd></div>
        <div><dt>Hostile</dt><dd>{hostile}</dd></div>
        <div><dt>Zoom</dt><dd>{Math.round(zoom * 100)}%</dd></div>
        <div><dt>Telemetry</dt><dd data-state={connectionState}>{connectionState}</dd></div>
      </dl>
      <div className="hud-actions">
        <button type="button" onClick={onResetCamera}>Reset View</button>
        <button type="button" onClick={onFitBattle}>Fit Battle</button>
      </div>
      {selectedShipId ? <small>Selected: {selectedShipId.slice(0, 8)}</small> : null}
    </aside>
  );
};
