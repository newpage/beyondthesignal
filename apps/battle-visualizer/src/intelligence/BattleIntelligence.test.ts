import { describe, expect, it } from "vitest";
import { analyzeShip, confidenceBand, projectPosition, threatLevel } from "./BattleIntelligence";
import type { ShipTelemetry } from "../types";

const ship = (overrides: Partial<ShipTelemetry> = {}): ShipTelemetry => ({
  id: "ship-1",
  name: "Horizon",
  side: "ALLIANCE",
  position: { x: 10, y: 20, z: 0 },
  velocity: { x: 2, y: -1, z: 0 },
  headingRadians: 0,
  hull: 0.9,
  shields: 0.8,
  shipClass: "CRUISER",
  maneuver: "INTERCEPT",
  executionState: "TURNING",
  confidence: 82,
  ...overrides,
});

describe("BattleIntelligence", () => {
  it("classifies confidence bands", () => {
    expect(confidenceBand(80)).toBe("HIGH");
    expect(confidenceBand(50)).toBe("MODERATE");
    expect(confidenceBand(10)).toBe("LOW");
  });

  it("projects the current velocity vector", () => {
    expect(projectPosition(ship(), 5)).toEqual({ x: 20, y: 15, z: 0 });
  });

  it("identifies critical durability", () => {
    expect(threatLevel(ship({ hull: 0.1, shields: 0.1 }))).toBe("CRITICAL");
  });

  it("summarizes real presentation fields", () => {
    const result = analyzeShip(ship({ selectedTargetId: "enemy" }));
    expect(result.decisionSummary).toBe("Intercept · Turning");
    expect(result.reasoning).toContain("An active target is assigned.");
  });
});
