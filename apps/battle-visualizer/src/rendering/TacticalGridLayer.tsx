import { useCallback } from "react";
import type { Graphics } from "pixi.js";
import { tacticalTheme } from "./theme/TacticalTheme";

const WORLD_SCALE = 0.75;
const GRID_EXTENT = 1600;
const GRID_STEP = 100;

export const TacticalGridLayer = () => {
  const draw = useCallback((graphics: Graphics) => {
    graphics.clear();
    for (let value = -GRID_EXTENT; value <= GRID_EXTENT; value += GRID_STEP) {
      const coordinate = value * WORLD_SCALE;
      const major = value % 500 === 0;
      graphics
        .moveTo(coordinate, -GRID_EXTENT * WORLD_SCALE)
        .lineTo(coordinate, GRID_EXTENT * WORLD_SCALE)
        .stroke({
          color: major ? tacticalTheme.gridMajor : tacticalTheme.gridMinor,
          alpha: major ? 0.2 : 0.07,
          width: major ? 1.2 : 0.7,
        });
      graphics
        .moveTo(-GRID_EXTENT * WORLD_SCALE, coordinate)
        .lineTo(GRID_EXTENT * WORLD_SCALE, coordinate)
        .stroke({
          color: major ? tacticalTheme.gridMajor : tacticalTheme.gridMinor,
          alpha: major ? 0.2 : 0.07,
          width: major ? 1.2 : 0.7,
        });
    }

    [150, 300, 450, 600].forEach((radius, index) => {
      graphics.circle(0, 0, radius * WORLD_SCALE).stroke({
        color: tacticalTheme.sensorRing,
        alpha: 0.18 - index * 0.025,
        width: index === 0 ? 1.4 : 1,
      });
    });
  }, []);

  return <pixiGraphics draw={draw} />;
};
