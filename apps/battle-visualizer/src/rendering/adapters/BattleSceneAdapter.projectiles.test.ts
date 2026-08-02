import { describe, expect, it } from "vitest";
import type { BattleFrame } from "../../types";
import { BattleSceneAdapter } from "./BattleSceneAdapter";
import type { ProjectileSceneNode } from "../scene/SceneGraph";

describe("BattleSceneAdapter projectiles", () => {
  it("interpolates an authoritative projectile between source and target", () => {
    const frame: BattleFrame = {
      battleId: "battle",
      tick: 5,
      simulationTimeSeconds: 0.25,
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
        },
        {
          id: "b",
          name: "B",
          side: "HOSTILE",
          position: { x: 100, y: 40, z: 0 },
          velocity: { x: 0, y: 0, z: 0 },
          headingRadians: Math.PI,
          hull: 1,
          shields: 1,
        },
      ],
      wrecks: [],
    projectiles: [
        {
          id: "p",
          sourceId: "a",
          targetId: "b",
          weaponId: "torpedo",
          status: "IN_FLIGHT",
          progress: 0.25,
        },
      ],
      events: [],
    };

    const projectile = new BattleSceneAdapter()
      .adapt(frame)
      .byType<ProjectileSceneNode>("PROJECTILE")[0];

    expect(projectile.worldPosition.x).toBe(25);
    expect(projectile.worldPosition.y).toBe(10);
  });
});
