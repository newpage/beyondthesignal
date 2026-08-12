import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { BattleFrame, ShipTelemetry } from "../types";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;

const FocusTarget = ({ ship, score, tick }: { ship: ShipTelemetry; score: number; tick: number }) => {
  const draw = useCallback((graphics: Graphics) => {
    const x = ship.position.x * WORLD_SCALE;
    const y = ship.position.y * WORLD_SCALE;
    const pulse = 1 + Math.sin(tick * 0.28) * 0.12;
    const radius = (38 + Math.min(24, score / 8)) * pulse;
    graphics.clear();
    graphics.circle(x, y, radius).stroke({
      color: tacticalTheme.targetReticle,
      alpha: 0.82,
      width: 2.2,
    });
    graphics.circle(x, y, radius + 8).stroke({
      color: tacticalTheme.targetReticle,
      alpha: 0.22,
      width: 1,
    });
    for (let index = 0; index < 4; index += 1) {
      const angle = index * Math.PI / 2 + tick * 0.015;
      graphics
        .moveTo(x + Math.cos(angle) * (radius - 7), y + Math.sin(angle) * (radius - 7))
        .lineTo(x + Math.cos(angle) * (radius + 11), y + Math.sin(angle) * (radius + 11))
        .stroke({ color: tacticalTheme.targetReticle, alpha: 0.9, width: 2 });
    }
  }, [score, ship, tick]);
  return <pixiGraphics draw={draw} />;
};

export const FleetDecisionOverlay = ({ frame }: { frame: BattleFrame }) => (
  <pixiContainer>
    {(frame.fleetDecisions ?? []).map((decision) => {
      if (!decision.primaryTargetId || decision.retreat) return null;
      const ship = frame.ships.find(
        (candidate) => candidate.id === decision.primaryTargetId,
      );
      if (!ship) return null;
      return (
        <FocusTarget
          key={decision.fleetId}
          ship={ship}
          score={decision.threats[0]?.score ?? 0}
          tick={frame.tick}
        />
      );
    })}
  </pixiContainer>
);
