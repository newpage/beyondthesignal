import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { ShipSceneNode } from "./scene/SceneGraph";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;

const Reticle = ({ ship }: { ship: ShipSceneNode }) => {
  const draw = useCallback((graphics: Graphics) => {
    const x = ship.worldPosition.x * WORLD_SCALE;
    const y = ship.worldPosition.y * WORLD_SCALE;
    graphics.clear();
    graphics.circle(x, y, 33).stroke({
      color: tacticalTheme.targetReticle,
      alpha: 0.55,
      width: 1.5,
    });
    graphics
      .moveTo(x - 8, y)
      .lineTo(x + 8, y)
      .moveTo(x, y - 8)
      .lineTo(x, y + 8)
      .stroke({
        color: tacticalTheme.targetReticle,
        alpha: 0.85,
        width: 1,
      });
  }, [ship]);

  return <pixiGraphics draw={draw} visible={ship.visible} />;
};

export const TargetingReticleLayer = ({
  selectedShip,
  targetShip,
}: {
  selectedShip?: ShipSceneNode;
  targetShip?: ShipSceneNode;
}) => (
  <pixiContainer>
    {selectedShip ? <Reticle ship={selectedShip} /> : null}
    {targetShip ? <Reticle ship={targetShip} /> : null}
  </pixiContainer>
);
