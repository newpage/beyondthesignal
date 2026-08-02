import { describe, expect, it } from "vitest";
import type { BattleFrame } from "../../types";
import { BattleSceneAdapter } from "./BattleSceneAdapter";
import type { WreckSceneNode } from "../scene/SceneGraph";

describe("BattleSceneAdapter wrecks", () => {
  it("keeps wrecks as persistent scene nodes", () => {
    const frame: BattleFrame = {
      battleId: "battle",
      tick: 20,
      simulationTimeSeconds: 1,
      ships: [],
      projectiles: [],
      wrecks: [{
        id: "w1",
        formerShipId: "s1",
        faction: "HOSTILE",
        destroyedTick: 12,
        cause: "torpedo",
      }],
      events: [],
    };

    const wrecks = new BattleSceneAdapter()
      .adapt(frame)
      .byType<WreckSceneNode>("WRECK");

    expect(wrecks).toHaveLength(1);
    expect(wrecks[0]?.formerShipId).toBe("s1");
  });
});
