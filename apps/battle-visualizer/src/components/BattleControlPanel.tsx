import { useCallback, useEffect, useState } from "react";

type BattleStatus = Readonly<{
  battleId: string;
  scenario: string;
  state: "RUNNING" | "PAUSED" | "COMPLETED";
  tick: number;
  outcome: string;
  allianceOperational: number;
  hostileOperational: number;
}>;

type Scenario = Readonly<{
  id: string;
  name: string;
  description: string;
}>;

const API_URL = import.meta.env.VITE_GAME_API_URL ?? "http://localhost:8080";

export const BattleControlPanel = () => {
  const [status, setStatus] = useState<BattleStatus>();
  const [scenarios, setScenarios] = useState<readonly Scenario[]>([]);
  const [scenario, setScenario] = useState("FLEET_SKIRMISH");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string>();

  const refresh = useCallback(async () => {
    try {
      const response = await fetch(`${API_URL}/api/battle`);
      if (!response.ok) throw new Error(`Battle API returned ${response.status}`);
      const next = await response.json() as BattleStatus;
      setStatus(next);
      setScenario(next.scenario);
      setError(undefined);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : "Battle API unavailable");
    }
  }, []);

  useEffect(() => {
    void refresh();
    void fetch(`${API_URL}/api/battle/scenarios`)
      .then((response) => response.json())
      .then((body: { scenarios: Scenario[] }) => setScenarios(body.scenarios))
      .catch(() => setError("Unable to load battle scenarios"));
    const timer = window.setInterval(() => void refresh(), 1_000);
    return () => window.clearInterval(timer);
  }, [refresh]);

  const command = async (action: "pause" | "resume" | "reset") => {
    setBusy(true);
    try {
      const response = await fetch(`${API_URL}/api/battle/${action}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: action === "reset" ? JSON.stringify({ scenario }) : undefined,
      });
      if (!response.ok) throw new Error(`Battle command failed (${response.status})`);
      setStatus(await response.json() as BattleStatus);
      setError(undefined);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : "Battle command failed");
    } finally {
      setBusy(false);
    }
  };

  const selectedScenario = scenarios.find((candidate) => candidate.id === scenario);

  return (
    <section className="battle-control" aria-label="Battle control">
      <div className="battle-control-heading">
        <span className="eyebrow">BATTLE CONTROL</span>
        <strong data-state={status?.state}>{status?.state ?? "CONNECTING"}</strong>
      </div>
      <select value={scenario} disabled={busy} onChange={(event) => setScenario(event.target.value)}>
        {scenarios.map((candidate) => (
          <option key={candidate.id} value={candidate.id}>{candidate.name}</option>
        ))}
      </select>
      <div className="battle-control-actions">
        <button type="button" disabled={busy || status?.state !== "RUNNING"} onClick={() => void command("pause")}>Pause Battle</button>
        <button type="button" disabled={busy || status?.state !== "PAUSED"} onClick={() => void command("resume")}>Resume</button>
        <button type="button" disabled={busy} onClick={() => void command("reset")}>Start / Reset</button>
      </div>
      <dl>
        <div><dt>Alliance</dt><dd>{status?.allianceOperational ?? "—"}</dd></div>
        <div><dt>Hostile</dt><dd>{status?.hostileOperational ?? "—"}</dd></div>
        <div><dt>Outcome</dt><dd>{status?.outcome.replaceAll("_", " ") ?? "—"}</dd></div>
      </dl>
      <small>{error ?? selectedScenario?.description ?? "Loading scenario…"}</small>
    </section>
  );
};
