import type { BattleFrame } from "../types";

export type TacticalAlertLevel = "NOMINAL" | "ENGAGED" | "CRITICAL";

export type TacticalStatus = Readonly<{
  allianceShips: number;
  hostileShips: number;
  activeProjectiles: number;
  wrecks: number;
  alertLevel: TacticalAlertLevel;
}>;

export const deriveTacticalStatus = (frame: BattleFrame): TacticalStatus => {
  const allianceShips = frame.ships.filter(
    (ship) => ship.side === "ALLIANCE",
  ).length;
  const hostileShips = frame.ships.length - allianceShips;
  const activeProjectiles = frame.projectiles?.filter(
    (projectile) => projectile.status === "IN_FLIGHT",
  ).length ?? 0;
  const wrecks = frame.wrecks?.length ?? 0;
  const lowestHull = frame.ships.reduce(
    (lowest, ship) => Math.min(lowest, ship.hull),
    1,
  );

  const alertLevel: TacticalAlertLevel = lowestHull < 0.3 || wrecks > 0
    ? "CRITICAL"
    : activeProjectiles > 0 || (frame.events?.length ?? 0) > 0
      ? "ENGAGED"
      : "NOMINAL";

  return { allianceShips, hostileShips, activeProjectiles, wrecks, alertLevel };
};
