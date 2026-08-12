import type { BattleFrame, FleetDecisionTelemetry } from "../types";

const targetName = (
  frame: BattleFrame,
  decision: FleetDecisionTelemetry,
): string => {
  if (!decision.primaryTargetId) return "No active target";
  return frame.ships.find(
    (ship) => ship.id === decision.primaryTargetId,
  )?.name ?? decision.primaryTargetId.slice(0, 8);
};

export const FleetCommandPanel = ({ frame }: { frame: BattleFrame }) => {
  const decisions = frame.fleetDecisions ?? [];
  if (decisions.length === 0) return null;

  return (
    <section className="fleet-command-panel" aria-label="Fleet command">
      <header>
        <span>Fleet Command</span>
        <strong>{decisions.length} Active</strong>
      </header>
      {decisions.map((decision) => {
        const fleet = frame.fleets?.find(
          (candidate) => candidate.id === decision.fleetId,
        );
        const topThreat = decision.threats[0];
        return (
          <article key={decision.fleetId}>
            <div className="fleet-command-title">
              <strong>{fleet?.name ?? decision.fleetId.slice(0, 8)}</strong>
              <span>{decision.commanderStatus}</span>
            </div>
            <dl>
              <div><dt>Doctrine</dt><dd>{decision.doctrine}</dd></div>
              <div><dt>Objective</dt><dd>{decision.objective}</dd></div>
              <div><dt>Primary</dt><dd>{targetName(frame, decision)}</dd></div>
              <div><dt>Threat</dt><dd>{topThreat ? topThreat.score.toFixed(1) : "—"}</dd></div>
            </dl>
            {decision.retreat ? (
              <div className="fleet-retreat-warning">WITHDRAWAL ORDER ACTIVE</div>
            ) : null}
          </article>
        );
      })}
    </section>
  );
};
