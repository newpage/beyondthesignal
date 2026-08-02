import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { WreckSceneNode } from "./scene/SceneGraph";

const WORLD_SCALE = 0.75;

const WreckGlyph = ({ wreck }: { wreck: WreckSceneNode }) => {
  const draw = useCallback((graphics: Graphics) => {
    const x = wreck.worldPosition.x * WORLD_SCALE;
    const y = wreck.worldPosition.y * WORLD_SCALE;
    graphics.clear();
    graphics
      .moveTo(x - 10, y - 8)
      .lineTo(x + 8, y + 10)
      .stroke({ color: 0x6f7785, alpha: 0.75, width: 3 });
    graphics
      .moveTo(x + 9, y - 9)
      .lineTo(x - 7, y + 8)
      .stroke({ color: 0x4b515c, alpha: 0.85, width: 2 });
    graphics.circle(x, y, 15).stroke({
      color: 0x8b5e3c,
      alpha: 0.25,
      width: 1,
    });
  }, [wreck]);

  return <pixiGraphics draw={draw} visible={wreck.visible} />;
};

export const WreckLayer = ({
  wrecks,
}: {
  wrecks: readonly WreckSceneNode[];
}) => (
  <pixiContainer>
    {wrecks.map((wreck) => (
      <WreckGlyph key={wreck.id} wreck={wreck} />
    ))}
  </pixiContainer>
);
