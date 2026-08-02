import type { BattleFrame, Vector3 } from "../../types";
import {
  SceneGraph,
  type EngagementSceneNode,
  type ShipSceneNode,
} from "../scene/SceneGraph";

const ZERO: Vector3 = { x: 0, y: 0, z: 0 };
const FIRE_CYCLE_TICKS = 20;
const FIRE_WINDOW_TICKS = 5;

export class BattleSceneAdapter {
  public adapt(frame: BattleFrame): SceneGraph {
    const scene = new SceneGraph();
    const ships = new Map(frame.ships.map((ship) => [ship.id, ship]));

    for (const ship of frame.ships) {
      const node: ShipSceneNode = {
        id: ship.id,
        type: "SHIP",
        visible: true,
        worldPosition: ship.position,
        name: ship.name,
        side: ship.side,
        velocity: ship.velocity,
        headingRadians: ship.headingRadians,
        hull: ship.hull,
        shields: ship.shields,
        selectedTargetId: ship.selectedTargetId,
        shipClass: ship.shipClass ?? "UNKNOWN",
        maneuver: ship.maneuver ?? "NONE",
        executionState: ship.executionState ?? "IDLE",
        confidence: ship.confidence ?? 0,
      };
      scene.upsert(node);
    }

    for (const ship of frame.ships) {
      if (!ship.selectedTargetId) continue;
      const target = ships.get(ship.selectedTargetId);
      if (!target) continue;

      const cycleTick = frame.tick % FIRE_CYCLE_TICKS;
      const engagement: EngagementSceneNode = {
        id: `engagement:${ship.id}:${target.id}`,
        type: "ENGAGEMENT",
        visible: true,
        worldPosition: ZERO,
        sourceShipId: ship.id,
        targetShipId: target.id,
        sourcePosition: ship.position,
        targetPosition: target.position,
        side: ship.side,
        pulsePhase: cycleTick / FIRE_CYCLE_TICKS,
        firing: cycleTick < FIRE_WINDOW_TICKS,
      };
      scene.upsert(engagement);
    }

    return scene;
  }
}
