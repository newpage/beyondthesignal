import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { WreckSceneNode } from "./scene/SceneGraph";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;

const WreckGlyph = ({
  wreck,
  currentTick,
}: {
  wreck: WreckSceneNode;
  currentTick: number;
}) => {
  const draw = useCallback((graphics: Graphics) => {
    const x = wreck.worldPosition.x * WORLD_SCALE;
    const y = wreck.worldPosition.y * WORLD_SCALE;
    const age = Math.max(0, currentTick - wreck.destroyedTick);
    const fresh = age <= 18;
    const shockwaveRadius = 18 + Math.min(age, 18) * 2.2;

    graphics.clear();

    if (fresh) {
      graphics.circle(x, y, shockwaveRadius).stroke({
        color: tacticalTheme.explosion,
        alpha: Math.max(0.08, 0.7 - age / 24),
        width: 2.4,
      });
      graphics.circle(x, y, 9 + age * 0.35).fill({
        color: tacticalTheme.explosionCore,
        alpha: Math.max(0.05, 0.55 - age / 32),
      });
    }

    graphics
      .moveTo(x - 12, y - 9)
      .lineTo(x + 9, y + 11)
      .stroke({ color: 0x7c8595, alpha: 0.82, width: 3 });
    graphics
      .moveTo(x + 10, y - 10)
      .lineTo(x - 8, y + 9)
      .stroke({ color: 0x4e5663, alpha: 0.92, width: 2 });

    [0, 1, 2].forEach((index) => {
      const angle = (wreck.destroyedTick * 0.17) + index * 2.1;
      const distance = 16 + index * 6;
      graphics.circle(
        x + Math.cos(angle) * distance,
        y + Math.sin(angle) * distance,
        1.5 + index * 0.5,
      ).fill({
        color: index === 0
          ? tacticalTheme.explosionCore
          : tacticalTheme.debris,
        alpha: 0.45,
      });
    });
  }, [currentTick, wreck]);

  return <pixiGraphics draw={draw} visible={wreck.visible} />;
};

export const WreckLayer = ({
  wrecks,
  currentTick,
}: {
  wrecks: readonly WreckSceneNode[];
  currentTick: number;
}) => (
  <pixiContainer>
    {wrecks.map((wreck) => (
      <WreckGlyph
        key={wreck.id}
        wreck={wreck}
        currentTick={currentTick}
      />
    ))}
  </pixiContainer>
);
