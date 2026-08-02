import { describe, expect, it } from "vitest";
import { BattleSceneAdapter } from "./BattleSceneAdapter";

const frame = {
  battleId: "battle-1", tick: 1, simulationTimeSeconds: 0.05,
  ships: [{
    id: "ship-1", name: "Horizon", side: "ALLIANCE" as const,
    position: { x: 1, y: 2, z: 0 }, velocity: { x: 3, y: 4, z: 0 },
    headingRadians: 0.5, hull: 0.9, shields: 0.8,
  }],
};

describe("BattleSceneAdapter", () => {
  it("creates deterministic ship scene nodes", () => {
    const scene = new BattleSceneAdapter().adapt(frame);
    const ship = scene.get("ship-1");
    expect(ship?.worldPosition).toEqual({ x: 1, y: 2, z: 0 });
    expect(ship?.type).toBe("SHIP");
  });
});
