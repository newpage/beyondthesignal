import { useCallback } from "react";
import type { Graphics } from "pixi.js";

const stars = Array.from({ length: 180 }, (_, index) => ({
  x: ((index * 67) % 1200) - 600,
  y: ((index * 131) % 800) - 400,
  size: 0.6 + (index % 4) * 0.35,
  alpha: 0.2 + (index % 6) * 0.11,
}));

export const Starfield = () => {
  const draw = useCallback((graphics: Graphics) => {
    graphics.clear();

    for (const star of stars) {
      graphics
        .circle(star.x, star.y, star.size)
        .fill({ color: 0xc8dcff, alpha: star.alpha });
    }
  }, []);

  return <pixiGraphics draw={draw} />;
};
