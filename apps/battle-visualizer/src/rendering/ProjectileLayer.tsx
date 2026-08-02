import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import type { ProjectileSceneNode } from "./scene/SceneGraph";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;

const ProjectileGlyph = ({
  projectile,
}: {
  projectile: ProjectileSceneNode;
}) => {
  const draw = useCallback(
    (graphics: Graphics) => {
      const x = projectile.worldPosition.x * WORLD_SCALE;
      const y = projectile.worldPosition.y * WORLD_SCALE;
      const dx = projectile.targetPosition.x - projectile.sourcePosition.x;
      const dy = projectile.targetPosition.y - projectile.sourcePosition.y;
      const length = Math.max(0.001, Math.hypot(dx, dy));
      const ux = dx / length;
      const uy = dy / length;
      const trail = 28;
      const color = projectile.side === "ALLIANCE"
        ? tacticalTheme.projectileFriendly
        : tacticalTheme.projectileHostile;

      graphics.clear();
      graphics
        .moveTo(x - ux * trail, y - uy * trail)
        .lineTo(x, y)
        .stroke({ color, alpha: 0.55, width: 2 });
      graphics.circle(x, y, 4).fill({ color, alpha: 0.95 });
      graphics.circle(x, y, 8).stroke({ color, alpha: 0.35, width: 1 });
    },
    [projectile],
  );

  return <pixiGraphics draw={draw} visible={projectile.visible} />;
};

export const ProjectileLayer = ({
  projectiles,
}: {
  projectiles: readonly ProjectileSceneNode[];
}) => (
  <pixiContainer>
    {projectiles.map((projectile) => (
      <ProjectileGlyph key={projectile.id} projectile={projectile} />
    ))}
  </pixiContainer>
);
