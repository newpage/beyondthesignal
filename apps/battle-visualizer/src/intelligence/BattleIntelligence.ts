import type { ShipTelemetry, Vector3 } from "../types";

export type ConfidenceBand = "LOW" | "MODERATE" | "HIGH";
export type ThreatLevel = "LOW" | "ELEVATED" | "HIGH" | "CRITICAL";

export type ShipIntelligence = Readonly<{
  confidenceBand: ConfidenceBand;
  threatLevel: ThreatLevel;
  decisionSummary: string;
  reasoning: readonly string[];
  projectedPosition: Vector3;
}>;

export const confidenceBand = (confidence: number): ConfidenceBand => {
  if (confidence >= 75) return "HIGH";
  if (confidence >= 40) return "MODERATE";
  return "LOW";
};

export const threatLevel = (ship: ShipTelemetry): ThreatLevel => {
  const durability = ship.hull * 0.65 + ship.shields * 0.35;
  if (durability <= 0.2) return "CRITICAL";
  if (durability <= 0.45) return "HIGH";
  if (ship.selectedTargetId || (ship.confidence ?? 0) >= 75) return "ELEVATED";
  return "LOW";
};

export const projectPosition = (ship: ShipTelemetry, seconds = 6): Vector3 => ({
  x: ship.position.x + ship.velocity.x * seconds,
  y: ship.position.y + ship.velocity.y * seconds,
  z: ship.position.z + ship.velocity.z * seconds,
});

const titleCase = (value: string): string =>
  value
    .toLowerCase()
    .split("_")
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");

export const analyzeShip = (ship: ShipTelemetry): ShipIntelligence => {
  const reasons: string[] = [];
  if (ship.selectedTargetId) reasons.push("An active target is assigned.");
  if (ship.shields < 0.35) reasons.push("Shield reserves are below 35%.");
  if (ship.hull < 0.5) reasons.push("Hull integrity is below 50%.");
  if (Math.hypot(ship.velocity.x, ship.velocity.y, ship.velocity.z) > 0.01) {
    reasons.push("A projected movement vector is available.");
  }
  if (reasons.length === 0) reasons.push("Maintaining current tactical posture.");

  return {
    confidenceBand: confidenceBand(ship.confidence ?? 0),
    threatLevel: threatLevel(ship),
    decisionSummary: `${titleCase(ship.maneuver ?? "NONE")} · ${titleCase(ship.executionState ?? "IDLE")}`,
    reasoning: reasons,
    projectedPosition: projectPosition(ship),
  };
};
