import { describe, expect, it } from "vitest";
import type { BattleFrame } from "../../types";
import { BattleSceneAdapter } from "./BattleSceneAdapter";
import type { EngagementSceneNode } from "../scene/SceneGraph";

describe("BattleSceneAdapter combat events", () => {
  it("creates an event-driven beam with shield impact", () => {
    const frame: BattleFrame = {
      battleId: "battle",
      tick: 7,
      simulationTimeSeconds: 0.35,
      ships: [
        {
          id: "a",
          name: "A",
          side: "ALLIANCE",
          position: { x: 0, y: 0, z: 0 },
          velocity: { x: 0, y: 0, z: 0 },
          headingRadians: 0,
          hull: 1,
          shields: 1,
          selectedTargetId: "b",
        },
        {
          id: "b",
          name: "B",
          side: "HOSTILE",
          position: { x: 100, y: 0, z: 0 },
          velocity: { x: 0, y: 0, z: 0 },
          headingRadians: Math.PI,
          hull: 1,
          shields: 0.8,
        },
      ],
      projectiles: [],
      events: [
        {
          tick: 7,
          type: "BEAM_FIRED",
          message: "beam",
          sourceId: "a",
          targetId: "b",
          attributes: { sequence: "4" },
        },
        {
          tick: 7,
          type: "SHIELD_IMPACT",
          message: "impact",
          sourceId: "a",
          targetId: "b",
          attributes: { sequence: "6" },
        },
      ],
    };

    const engagements = new BattleSceneAdapter()
      .adapt(frame)
      .byType<EngagementSceneNode>("ENGAGEMENT");

    const beam = engagements.find((value) => value.id.startsWith("beam:"));
    expect(beam?.firing).toBe(true);
    expect(beam?.impactType).toBe("SHIELD");
  });
});
