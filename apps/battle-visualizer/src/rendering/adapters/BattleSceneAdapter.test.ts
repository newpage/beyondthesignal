import { describe, expect, it } from "vitest";
import type { BattleFrame } from "../../types";
import { BattleSceneAdapter } from "./BattleSceneAdapter";
import type {
  EngagementSceneNode,
  ShipSceneNode,
} from "../scene/SceneGraph";

const frame = (tick: number): BattleFrame => ({
  battleId: "battle-1",
  tick,
  simulationTimeSeconds: tick * 0.05,
  ships: [
    {
      id: "friendly",
      name: "Horizon",
      side: "ALLIANCE",
      position: { x: -100, y: 0, z: 0 },
      velocity: { x: 1, y: 0, z: 0 },
      headingRadians: 0,
      hull: 1,
      shields: 0.8,
      selectedTargetId: "hostile",
    },
    {
      id: "hostile",
      name: "Raider",
      side: "HOSTILE",
      position: { x: 100, y: 0, z: 0 },
      velocity: { x: -1, y: 0, z: 0 },
      headingRadians: Math.PI,
      hull: 0.7,
      shields: 0.4,
    },
  ],
});

describe("BattleSceneAdapter", () => {
  it("maps ships into deterministic scene nodes", () => {
    const scene = new BattleSceneAdapter().adapt(frame(9));
    const ships = scene.byType<ShipSceneNode>("SHIP");

    expect(ships).toHaveLength(2);
    expect(ships[0]?.name).toBe("Horizon");
    expect(ships[0]?.selectedTargetId).toBe("hostile");
  });

  it("creates engagement nodes for valid selected targets", () => {
    const scene = new BattleSceneAdapter().adapt(frame(2));
    const engagements = scene.byType<EngagementSceneNode>("ENGAGEMENT");

    expect(engagements).toHaveLength(1);
    expect(engagements[0]).toMatchObject({
      sourceShipId: "friendly",
      targetShipId: "hostile",
      side: "ALLIANCE",
      firing: true,
    });
  });

  it("derives weapon pulse state from simulation tick", () => {
    const firing = new BattleSceneAdapter()
      .adapt(frame(4))
      .byType<EngagementSceneNode>("ENGAGEMENT")[0];
    const cooling = new BattleSceneAdapter()
      .adapt(frame(8))
      .byType<EngagementSceneNode>("ENGAGEMENT")[0];

    expect(firing?.firing).toBe(true);
    expect(cooling?.firing).toBe(false);
    expect(cooling?.pulsePhase).toBeCloseTo(0.4);
  });
});
