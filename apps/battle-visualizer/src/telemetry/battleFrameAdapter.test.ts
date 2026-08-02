import { describe, expect, it } from "vitest";
import { adaptBattleFrame } from "./battleFrameAdapter";
import type { BattleFrameV1 } from "../types";

const frame: BattleFrameV1 = {
  metadata: {
    battleId: "battle-1",
    tick: 12,
    simulationTimeSeconds: 0.6,
    seed: 9,
    frameVersion: "1.0",
    generatedAt: "1970-01-01T00:00:00.600Z",
    checksum: "abc",
  },
  capabilities: { enabled: ["SHIPS", "MOVEMENT"] },
  ships: [{
    id: "ship-1",
    name: "Ship One",
    shipClass: "UNKNOWN",
    faction: "FRIENDLY",
    position: { x: 0, y: 0, z: 0 },
    velocity: { x: 0, y: 0, z: 0 },
    headingRadians: 0,
    targetId: null,
    hullPercent: 0.75,
    shieldPercent: 0.5,
    maneuver: "NONE",
    executionState: "ACTIVE",
    confidence: 0,
    renderFlags: ["SELECTABLE"],
  }],
  formations: [],
  movement: [],
  events: [],
  debug: {},
};

describe("adaptBattleFrame", () => {
  it("maps the versioned server contract to renderer telemetry", () => {
    const adapted = adaptBattleFrame(frame);
    expect(adapted.battleId).toBe("battle-1");
    expect(adapted.ships[0]?.side).toBe("ALLIANCE");
    expect(adapted.ships[0]?.hull).toBe(0.75);
    expect(adapted.ships[0]?.position.x).toBeLessThan(0);
  });
});
