import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { ShipSceneNode } from "./scene/SceneGraph";

type Props = Readonly<{
  ships: readonly ShipSceneNode[];
}>;

const WORLD_SCALE = 0.75;

const ShipGlyph = ({ ship }: { ship: ShipSceneNode }) => {
  const draw = useCallback(
    (graphics: Graphics) => {
      const color = ship.side === "ALLIANCE" ? 0x66d9ff : 0xff5d78;
      graphics.clear();
      graphics
        .poly([
          { x: 14, y: 0 },
          { x: -10, y: -8 },
          { x: -5, y: 0 },
          { x: -10, y: 8 },
        ])
        .fill({ color, alpha: 0.95 })
        .stroke({ color: 0xffffff, alpha: 0.55, width: 1 });

      const velocityLength = Math.min(80, Math.hypot(ship.velocity.x, ship.velocity.y) * 3);
      graphics
        .moveTo(0, 0)
        .lineTo(-velocityLength, 0)
        .stroke({ color, alpha: 0.45, width: 1.5 });
    },
    [ship],
  );

  return (
    <pixiContainer
      x={ship.worldPosition.x * WORLD_SCALE}
      y={ship.worldPosition.y * WORLD_SCALE}
      rotation={ship.headingRadians}
      visible={ship.visible}
    >
      <pixiGraphics draw={draw} />
    </pixiContainer>
  );
};

export const ShipLayer = ({ ships }: Props) => (
  <pixiContainer>
    {ships.map((ship) => <ShipGlyph key={ship.id} ship={ship} />)}
  </pixiContainer>
);
