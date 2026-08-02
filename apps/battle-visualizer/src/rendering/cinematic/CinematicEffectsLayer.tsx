import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { BattleFrame } from "../../types";
import type { ShipSceneNode } from "../scene/SceneGraph";
import { tacticalTheme } from "../theme/TacticalTheme";
import { visualQualityProfile, type VisualQuality } from "./VisualQuality";

const WORLD_SCALE = 0.75;

export const CinematicEffectsLayer = ({
  frame,
  ships,
  quality,
}: {
  frame: BattleFrame;
  ships: readonly ShipSceneNode[];
  quality: VisualQuality;
}) => {
  const profile = visualQualityProfile(quality);

  const draw = useCallback((graphics: Graphics) => {
    graphics.clear();

    if (profile.sensorSweep) {
      const angle = (frame.tick % 180) / 180 * Math.PI * 2;
      const radius = 650 * WORLD_SCALE;
      graphics
        .moveTo(0, 0)
        .lineTo(Math.cos(angle) * radius, Math.sin(angle) * radius)
        .stroke({
          color: tacticalTheme.sensorRing,
          alpha: 0.24,
          width: 2,
        });
    }

    for (const event of frame.events ?? []) {
      const target = event.targetId
        ? ships.find((ship) => ship.id === event.targetId)
        : undefined;
      if (!target) continue;
      const x = target.worldPosition.x * WORLD_SCALE;
      const y = target.worldPosition.y * WORLD_SCALE;

      if (event.type === "SHIELD_IMPACT" && profile.shieldRipples) {
        [24, 34, 44].forEach((radius, index) => {
          graphics.circle(x, y, radius).stroke({
            color: tacticalTheme.shieldImpact,
            alpha: 0.52 - index * 0.14,
            width: 2,
          });
        });
      }

      if (event.type === "SHIP_DESTROYED") {
        graphics.circle(x, y, 24).fill({
          color: tacticalTheme.explosionCore,
          alpha: 0.45,
        });
        for (let index = 0; index < profile.particleCount; index += 1) {
          const angle = index / Math.max(1, profile.particleCount) * Math.PI * 2;
          const distance = 18 + ((frame.tick + index * 7) % 22);
          graphics.circle(
            x + Math.cos(angle) * distance,
            y + Math.sin(angle) * distance,
            2 + index % 3,
          ).fill({
            color: index % 2 === 0
              ? tacticalTheme.explosion
              : tacticalTheme.explosionCore,
            alpha: 0.72,
          });
        }
      }
    }
  }, [frame.events, frame.tick, profile, ships]);

  return <pixiGraphics draw={draw} />;
};
