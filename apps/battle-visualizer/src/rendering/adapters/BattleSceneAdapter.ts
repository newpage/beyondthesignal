import type { BattleFrame, CombatVisualEvent, Vector3 } from "../../types";
import {
  SceneGraph,
  type EngagementSceneNode,
  type ProjectileSceneNode,
  type ShipSceneNode,
  type WreckSceneNode,
} from "../scene/SceneGraph";

const ZERO: Vector3 = { x: 0, y: 0, z: 0 };

const eventKey = (event: CombatVisualEvent, index: number) =>
  `${event.type}:${event.attributes.sequence ?? index}`;

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
        pulsePhase: 1,
        firing: false,
        impactType: "NONE",
      };
      scene.upsert(engagement);
    }

    for (const projectile of frame.projectiles ?? []) {
      const source = ships.get(projectile.sourceId);
      const target = ships.get(projectile.targetId);
      if (!source || !target) continue;

      const node: ProjectileSceneNode = {
        id: `projectile:${projectile.id}`,
        type: "PROJECTILE",
        visible: projectile.status === "IN_FLIGHT",
        worldPosition: {
          x: source.position.x
            + (target.position.x - source.position.x) * projectile.progress,
          y: source.position.y
            + (target.position.y - source.position.y) * projectile.progress,
          z: source.position.z
            + (target.position.z - source.position.z) * projectile.progress,
        },
        sourceShipId: source.id,
        targetShipId: target.id,
        weaponId: projectile.weaponId,
        progress: projectile.progress,
        sourcePosition: source.position,
        targetPosition: target.position,
        side: source.side,
      };
      scene.upsert(node);
    }

    for (const wreck of frame.wrecks ?? []) {
      const formerShip = ships.get(wreck.formerShipId);
      const fallback = {
        x: ((wreck.destroyedTick * 37) % 600) - 300,
        y: ((wreck.destroyedTick * 53) % 400) - 200,
        z: 0,
      };
      const node: WreckSceneNode = {
        id: `wreck:${wreck.id}`,
        type: "WRECK",
        visible: true,
        worldPosition: formerShip?.position ?? fallback,
        formerShipId: wreck.formerShipId,
        faction: wreck.faction,
        destroyedTick: wreck.destroyedTick,
        cause: wreck.cause,
      };
      scene.upsert(node);
    }

    (frame.events ?? []).forEach((event, index) => {
      if (event.type !== "BEAM_FIRED" || !event.sourceId || !event.targetId) {
        return;
      }
      const source = ships.get(event.sourceId);
      const target = ships.get(event.targetId);
      if (!source || !target) return;

      const impact = (frame.events ?? []).find(
        (candidate) =>
          candidate.sourceId === event.sourceId
          && candidate.targetId === event.targetId
          && candidate.tick === event.tick
          && (candidate.type === "SHIELD_IMPACT"
            || candidate.type === "HULL_DAMAGE"
            || candidate.type === "SHIP_DESTROYED"),
      );

      const beam: EngagementSceneNode = {
        id: `beam:${eventKey(event, index)}`,
        type: "ENGAGEMENT",
        visible: true,
        worldPosition: ZERO,
        sourceShipId: source.id,
        targetShipId: target.id,
        sourcePosition: source.position,
        targetPosition: target.position,
        side: source.side,
        pulsePhase: 0,
        firing: true,
        impactType:
          impact?.type === "SHIP_DESTROYED"
            ? "DESTROYED"
            : impact?.type === "HULL_DAMAGE"
              ? "HULL"
              : impact?.type === "SHIELD_IMPACT"
                ? "SHIELD"
                : "MISS",
      };
      scene.upsert(beam);
    });

    return scene;
  }
}
