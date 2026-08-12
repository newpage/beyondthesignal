import type {
  BattleFrame,
  BattleFrameV1,
  FleetSide,
  ShipTelemetry,
} from "../types";

const side = (faction: string): FleetSide =>
  faction === "FRIENDLY" || faction === "ALLIANCE"
    ? "ALLIANCE"
    : "HOSTILE";

const tacticalPosition = (
  ship: BattleFrameV1["ships"][number],
  index: number,
  tick: number,
) => {
  const provided = ship.position;
  if (provided.x !== 0 || provided.y !== 0 || provided.z !== 0) {
    return provided;
  }

  const alliance = side(ship.faction) === "ALLIANCE";
  const sideIndex = index % 3;
  return {
    x: (alliance ? -1 : 1) * (300 + sideIndex * 65),
    y: (sideIndex - 1) * 110 + Math.sin(tick / 25 + index) * 18,
    z: 0,
  };
};

export const adaptBattleFrame = (frame: BattleFrameV1): BattleFrame => ({
  battleId: frame.metadata.battleId,
  tick: frame.metadata.tick,
  simulationTimeSeconds: frame.metadata.simulationTimeSeconds,
  ships: frame.ships.map((ship, index): ShipTelemetry => ({
    id: ship.id,
    name: ship.name,
    side: side(ship.faction),
    position: tacticalPosition(ship, index, frame.metadata.tick),
    velocity: ship.velocity,
    headingRadians:
      ship.headingRadians || (side(ship.faction) === "ALLIANCE" ? 0 : Math.PI),
    hull: ship.hullPercent,
    shields: ship.shieldPercent,
    selectedTargetId: ship.targetId ?? undefined,
    shipClass: ship.shipClass,
    maneuver: ship.maneuver,
    executionState: ship.executionState,
    confidence: ship.confidence,
  })),
  projectiles: (frame.projectiles ?? []).map((projectile) => ({
    id: projectile.id,
    sourceId: projectile.sourceId,
    targetId: projectile.targetId,
    weaponId: projectile.weaponId,
    status: projectile.status,
    progress: projectile.progress,
  })),
  fleets: (frame.fleets ?? []).map((fleet) => ({ ...fleet })),
  squadrons: (frame.squadrons ?? []).map((squadron) => ({ id: squadron.id, name: squadron.name, leaderId: squadron.leaderId, memberIds: squadron.memberIds, formation: squadron.formation, morale: squadron.morale, status: squadron.status })),
  fleetDecisions: (frame.fleetDecisions ?? []).map((decision) => ({ fleetId: decision.fleetId, doctrine: decision.doctrine, objective: decision.objective, primaryTargetId: decision.primaryTargetId ?? undefined, threats: decision.threats, commanderStatus: decision.commanderStatus, retreat: decision.retreat, generatedTick: decision.generatedTick })),
  events: (frame.events ?? []).map((event) => ({
    tick: event.tick,
    type: event.type,
    message: event.message,
    sourceId: event.attributes.sourceId,
    targetId: event.attributes.targetId,
    attributes: event.attributes,
  })),
});
