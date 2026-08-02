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
  projectiles: [],
  events: [],
});

describe("BattleSceneAdapter", () => {
  it("maps ships into deterministic scene nodes", () => {
    const scene = new BattleSceneAdapter().adapt(frame(9));
    const ships = scene.byType<ShipSceneNode>("SHIP");

    expect(ships).toHaveLength(2);
    expect(ships[0]?.name).toBe("Horizon");
    expect(ships[0]?.selectedTargetId).toBe("hostile");
  });

  it("creates non-firing target links for valid selected targets", () => {
    const scene = new BattleSceneAdapter().adapt(frame(2));
    const engagements = scene.byType<EngagementSceneNode>("ENGAGEMENT");

    expect(engagements).toHaveLength(1);
    expect(engagements[0]).toMatchObject({
      sourceShipId: "friendly",
      targetShipId: "hostile",
      side: "ALLIANCE",
      firing: false,
      impactType: "NONE",
    });
  });

  it("creates active weapon pulses only from beam events", () => {
    const eventFrame: BattleFrame = {
      ...frame(4),
      events: [
        {
          tick: 4,
          type: "BEAM_FIRED",
          message: "Beam fired",
          sourceId: "friendly",
          targetId: "hostile",
          attributes: { sequence: "3" },
        },
        {
          tick: 4,
          type: "SHIELD_IMPACT",
          message: "Shield impacted",
          sourceId: "friendly",
          targetId: "hostile",
          attributes: { sequence: "4" },
        },
      ],
    };

    const engagements = new BattleSceneAdapter()
      .adapt(eventFrame)
      .byType<EngagementSceneNode>("ENGAGEMENT");

    expect(engagements).toHaveLength(2);

    const targetLink = engagements.find((value) =>
      value.id.startsWith("engagement:")
    );
    const beam = engagements.find((value) =>
      value.id.startsWith("beam:")
    );

    expect(targetLink?.firing).toBe(false);
    expect(beam).toMatchObject({
      firing: true,
      pulsePhase: 0,
      impactType: "SHIELD",
    });
  });
});
