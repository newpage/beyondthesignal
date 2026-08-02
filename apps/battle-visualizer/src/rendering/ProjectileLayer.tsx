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
      const color = projectile.side === "ALLIANCE"
        ? tacticalTheme.projectileFriendly
        : tacticalTheme.projectileHostile;

      graphics.clear();

      [44, 30, 18].forEach((trail, index) => {
        graphics
          .moveTo(x - ux * trail, y - uy * trail)
          .lineTo(x, y)
          .stroke({
            color,
            alpha: 0.18 + index * 0.18,
            width: 1 + index,
          });
      });

      graphics.circle(x, y, 10).fill({ color, alpha: 0.12 });
      graphics.circle(x, y, 5).fill({ color, alpha: 0.98 });
      graphics.circle(x, y, 12).stroke({
        color,
        alpha: 0.36,
        width: 1,
      });

      const targetX = projectile.targetPosition.x * WORLD_SCALE;
      const targetY = projectile.targetPosition.y * WORLD_SCALE;
      graphics.circle(targetX, targetY, 16).stroke({
        color,
        alpha: 0.2,
        width: 1,
      });
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
