import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { ShipSceneNode } from "./scene/SceneGraph";
import { tacticalTheme } from "./theme/TacticalTheme";

type Props = Readonly<{
  selectedShip?: ShipSceneNode;
  targetShip?: ShipSceneNode;
}>;

export const AIIntelligenceLayer = ({ selectedShip, targetShip }: Props) => {
  const draw = useCallback((graphics: Graphics) => {
    graphics.clear();
    if (!selectedShip) return;

    const speed = Math.hypot(selectedShip.velocity.x, selectedShip.velocity.y);
    const scale = speed > 0.001 ? Math.min(12, 110 / speed) : 0;
    const projectedX = selectedShip.worldPosition.x + selectedShip.velocity.x * scale;
    const projectedY = selectedShip.worldPosition.y + selectedShip.velocity.y * scale;

    graphics
      .moveTo(selectedShip.worldPosition.x, selectedShip.worldPosition.y)
      .lineTo(projectedX, projectedY)
      .stroke({ color: tacticalTheme.intelligenceProjection, alpha: 0.8, width: 1.4 });

    graphics
      .circle(projectedX, projectedY, 7)
      .stroke({ color: tacticalTheme.intelligenceProjection, alpha: 0.9, width: 1.2 });

    const confidenceArc = Math.PI * 2 * Math.max(0, Math.min(100, selectedShip.confidence ?? 0)) / 100;
    graphics
      .arc(
        selectedShip.worldPosition.x,
        selectedShip.worldPosition.y,
        31,
        -Math.PI / 2,
        -Math.PI / 2 + confidenceArc,
      )
      .stroke({ color: tacticalTheme.intelligenceConfidence, alpha: 0.95, width: 2.2 });

    if (targetShip) {
      graphics
        .circle(targetShip.worldPosition.x, targetShip.worldPosition.y, 24)
        .stroke({ color: tacticalTheme.intelligenceTarget, alpha: 0.85, width: 1.5 });
    }
  }, [selectedShip, targetShip]);

  return <pixiGraphics draw={draw} />;
};
