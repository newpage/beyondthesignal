import type { BattleFrame, ConnectionState } from "../types";
import { deriveTacticalStatus } from "./tacticalStatus";
import { FleetCommandPanel } from "./FleetCommandPanel";
import { VISUAL_QUALITIES, type VisualQuality } from "../rendering/cinematic/VisualQuality";

type Props = Readonly<{
  frame: BattleFrame;
  connectionState: ConnectionState;
  zoom: number;
  selectedShipId?: string;
  followingSelection: boolean;
  hasMeasurement: boolean;
  onResetCamera: () => void;
  onFitBattle: () => void;
  onToggleFollow: () => void;
  onClearMeasurement: () => void;
  visualQuality: VisualQuality;
  onVisualQualityChange: (quality: VisualQuality) => void;
}>;

export const TacticalHud = ({
  frame,
  connectionState,
  zoom,
  selectedShipId,
  followingSelection,
  hasMeasurement,
  onResetCamera,
  onFitBattle,
  onToggleFollow,
  onClearMeasurement,
  visualQuality,
  onVisualQualityChange,
}: Props) => {
  const status = deriveTacticalStatus(frame);

  return (
    <aside className={`tactical-hud alert-${status.alertLevel.toLowerCase()}`} aria-label="Battle status">
      <div className="hud-section">
        <span className="hud-label">Battle Command</span>
        <strong>{frame.battleId.slice(0, 12)}</strong>
        <span className={`alert-badge ${status.alertLevel.toLowerCase()}`}>
          {status.alertLevel}
        </span>
      </div>
      <dl>
        <div><dt>Tick</dt><dd>{frame.tick.toLocaleString()}</dd></div>
        <div><dt>Ships</dt><dd>{frame.ships.length}</dd></div>
        <div><dt>Alliance</dt><dd>{status.allianceShips}</dd></div>
        <div><dt>Hostile</dt><dd>{status.hostileShips}</dd></div>
        <div><dt>Torpedoes</dt><dd>{status.activeProjectiles}</dd></div>
        <div><dt>Wrecks</dt><dd>{status.wrecks}</dd></div>
        <div><dt>Zoom</dt><dd>{Math.round(zoom * 100)}%</dd></div>
        <div><dt>Telemetry</dt><dd data-state={connectionState}>{connectionState}</dd></div>
      </dl>
      <FleetCommandPanel frame={frame} />
      <label className="quality-control">
        <span>Visual Quality</span>
        <select
          value={visualQuality}
          onChange={(event) =>
            onVisualQualityChange(event.target.value as VisualQuality)
          }
        >
          {VISUAL_QUALITIES.map((quality) => (
            <option key={quality} value={quality}>{quality}</option>
          ))}
        </select>
      </label>
      <div className="hud-actions">
        <button type="button" onClick={onResetCamera}>Reset</button>
        <button type="button" onClick={onFitBattle}>Fit</button>
        <button
          type="button"
          disabled={!selectedShipId}
          className={followingSelection ? "active" : undefined}
          onClick={onToggleFollow}
        >
          Follow
        </button>
        <button
          type="button"
          disabled={!hasMeasurement}
          onClick={onClearMeasurement}
        >
          Clear Range
        </button>
      </div>
      <small>
        {selectedShipId ? `Selected: ${selectedShipId.slice(0, 8)} · ` : ""}
        Shift-drag to measure range
      </small>
    </aside>
  );
};
