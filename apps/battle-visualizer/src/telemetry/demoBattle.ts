import type { BattleFrame, ShipTelemetry } from "../types";

const ships: readonly ShipTelemetry[] = [
  {
    id: "alliance-command",
    name: "BTS Horizon",
    side: "ALLIANCE",
    position: { x: -360, y: -40, z: 0 },
    velocity: { x: 12, y: 2, z: 0 },
    headingRadians: 0.08,
    hull: 1,
    shields: 0.92,
    selectedTargetId: "hostile-command",
  },
  {
    id: "alliance-escort-1",
    name: "BTS Resolute",
    side: "ALLIANCE",
    position: { x: -430, y: -120, z: 0 },
    velocity: { x: 13, y: 3, z: 0 },
    headingRadians: 0.12,
    hull: 0.94,
    shields: 0.86,
    selectedTargetId: "hostile-command",
  },
  {
    id: "alliance-escort-2",
    name: "BTS Venture",
    side: "ALLIANCE",
    position: { x: -425, y: 65, z: 0 },
    velocity: { x: 12, y: -1, z: 0 },
    headingRadians: -0.04,
    hull: 0.97,
    shields: 0.89,
    selectedTargetId: "hostile-raider",
  },
  {
    id: "hostile-command",
    name: "Raider Command",
    side: "HOSTILE",
    position: { x: 330, y: 5, z: 0 },
    velocity: { x: -9, y: 1, z: 0 },
    headingRadians: Math.PI,
    hull: 0.88,
    shields: 0.71,
    selectedTargetId: "alliance-command",
  },
  {
    id: "hostile-raider",
    name: "Raider Spear",
    side: "HOSTILE",
    position: { x: 410, y: 130, z: 0 },
    velocity: { x: -14, y: -4, z: 0 },
    headingRadians: Math.PI + 0.15,
    hull: 0.76,
    shields: 0.58,
    selectedTargetId: "alliance-escort-2",
  },
];

export const initialDemoFrame: BattleFrame = {
  battleId: "demo-engagement-01",
  tick: 0,
  simulationTimeSeconds: 0,
  ships,
};

export const advanceDemoFrame = (
  previous: BattleFrame,
  deltaSeconds: number,
): BattleFrame => ({
  ...previous,
  tick: previous.tick + 1,
  simulationTimeSeconds:
    previous.simulationTimeSeconds + deltaSeconds,
  ships: previous.ships.map((ship) => {
    const orbit = ship.side === "ALLIANCE" ? 1 : -1;
    const nextX = ship.position.x + ship.velocity.x * deltaSeconds;
    const nextY =
      ship.position.y +
      ship.velocity.y * deltaSeconds +
      Math.sin(previous.tick / 30 + nextX / 250) * orbit * 0.8;

    return {
      ...ship,
      position: {
        ...ship.position,
        x: nextX,
        y: nextY,
      },
      headingRadians:
        Math.atan2(ship.velocity.y, ship.velocity.x) +
        (ship.side === "HOSTILE" ? Math.PI : 0),
    };
  }),
});
