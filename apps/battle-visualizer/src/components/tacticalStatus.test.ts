import { describe, expect, it } from "vitest";
import type { BattleFrame } from "../types";
import { deriveTacticalStatus } from "./tacticalStatus";

const frame: BattleFrame = {
  battleId: "battle",
  tick: 1,
  simulationTimeSeconds: 0.05,
  ships: [],
  projectiles: [],
  wrecks: [],
  events: [],
};

describe("deriveTacticalStatus", () => {
  it("reports nominal state for an empty tactical picture", () => {
    expect(deriveTacticalStatus(frame)).toMatchObject({
      activeProjectiles: 0,
      wrecks: 0,
      alertLevel: "NOMINAL",
    });
  });
});
