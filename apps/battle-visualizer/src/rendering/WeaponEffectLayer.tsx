import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { EngagementSceneNode } from "./scene/SceneGraph";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;
const SEGMENT_LENGTH = 12;
const SEGMENT_GAP = 8;

const drawSegmentedLine = (
  graphics: Graphics,
  startX: number,
  startY: number,
  endX: number,
  endY: number,
  color: number,
) => {
  const dx = endX - startX;
  const dy = endY - startY;
  const distance = Math.hypot(dx, dy);
  if (distance <= 0.001) return;

  const unitX = dx / distance;
  const unitY = dy / distance;
  for (let offset = 0; offset < distance; offset += SEGMENT_LENGTH + SEGMENT_GAP) {
    const segmentEnd = Math.min(distance, offset + SEGMENT_LENGTH);
    graphics
      .moveTo(startX + unitX * offset, startY + unitY * offset)
      .lineTo(startX + unitX * segmentEnd, startY + unitY * segmentEnd)
      .stroke({ color, alpha: 0.28, width: 1 });
  }
};

const EngagementEffect = ({ engagement }: { engagement: EngagementSceneNode }) => {
  const draw = useCallback(
    (graphics: Graphics) => {
      const sourceX = engagement.sourcePosition.x * WORLD_SCALE;
      const sourceY = engagement.sourcePosition.y * WORLD_SCALE;
      const targetX = engagement.targetPosition.x * WORLD_SCALE;
      const targetY = engagement.targetPosition.y * WORLD_SCALE;
      const beamColor = engagement.side === "ALLIANCE"
        ? tacticalTheme.beamFriendly
        : tacticalTheme.beamHostile;

      graphics.clear();
      drawSegmentedLine(
        graphics,
        sourceX,
        sourceY,
        targetX,
        targetY,
        tacticalTheme.targetLine,
      );

      if (!engagement.firing) return;

      const pulseAlpha = 0.65 + (1 - engagement.pulsePhase * 4) * 0.35;
      graphics
        .moveTo(sourceX, sourceY)
        .lineTo(targetX, targetY)
        .stroke({ color: beamColor, alpha: pulseAlpha, width: 3 });
      graphics
        .moveTo(sourceX, sourceY)
        .lineTo(targetX, targetY)
        .stroke({ color: 0xffffff, alpha: 0.75, width: 1 });

      const impactRadius = 20 + engagement.pulsePhase * 18;
      graphics
        .circle(targetX, targetY, impactRadius)
        .stroke({
          color: tacticalTheme.shieldImpact,
          alpha: Math.max(0.15, 0.85 - engagement.pulsePhase * 3),
          width: 2.5,
        });
      graphics
        .circle(targetX, targetY, 6)
        .fill({ color: tacticalTheme.shieldImpact, alpha: 0.65 });
    },
    [engagement],
  );

  return <pixiGraphics draw={draw} visible={engagement.visible} />;
};

export const WeaponEffectLayer = ({ engagements }: {
  engagements: readonly EngagementSceneNode[];
}) => (
  <pixiContainer>
    {engagements.map((engagement) => (
      <EngagementEffect key={engagement.id} engagement={engagement} />
    ))}
  </pixiContainer>
);
