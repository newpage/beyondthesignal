import { useCallback, useState } from "react";
import type { FederatedPointerEvent, Graphics } from "pixi.js";
import type { ShipSceneNode } from "./scene/SceneGraph";
import { statusColor, tacticalTheme } from "./theme/TacticalTheme";

type Props = Readonly<{
  ships: readonly ShipSceneNode[];
  selectedShipId?: string;
  onSelectShip: (shipId: string) => void;
}>;

const WORLD_SCALE = 0.75;
const GLYPH_RADIUS = 15;

const drawArc = (
  graphics: Graphics,
  radius: number,
  value: number,
  color: number,
  width: number,
) => {
  const bounded = Math.max(0, Math.min(1, value));
  graphics
    .arc(0, 0, radius, -Math.PI / 2, -Math.PI / 2 + Math.PI * 2 * bounded)
    .stroke({ color, alpha: 0.95, width, cap: "round" });
};

const ShipGlyph = ({
  ship,
  selected,
  onSelect,
}: {
  ship: ShipSceneNode;
  selected: boolean;
  onSelect: () => void;
}) => {
  const [hovered, setHovered] = useState(false);
  const draw = useCallback(
    (graphics: Graphics) => {
      const factionColor = ship.side === "ALLIANCE"
        ? tacticalTheme.friendly
        : tacticalTheme.hostile;
      const shieldColor = statusColor(
        ship.shields,
        tacticalTheme.shieldHigh,
        tacticalTheme.shieldMedium,
        tacticalTheme.shieldLow,
        tacticalTheme.shieldCritical,
      );
      const hullColor = statusColor(
        ship.hull,
        tacticalTheme.hullHigh,
        tacticalTheme.hullMedium,
        tacticalTheme.hullLow,
        tacticalTheme.hullCritical,
      );

      graphics.clear();

      if (selected || hovered) {
        graphics
          .circle(0, 0, selected ? 27 : 24)
          .stroke({
            color: selected ? tacticalTheme.selection : tacticalTheme.hover,
            alpha: selected ? 0.9 : 0.65,
            width: selected ? 2.5 : 1.5,
          });
      }

      graphics.circle(0, 0, 22).stroke({ color: 0x18314f, alpha: 0.5, width: 2 });
      drawArc(graphics, 22, ship.shields, shieldColor, 2.4);
      graphics.circle(0, 0, 18.5).stroke({ color: 0x1d2c3e, alpha: 0.55, width: 2 });
      drawArc(graphics, 18.5, ship.hull, hullColor, 2.2);

      graphics
        .poly([
          { x: GLYPH_RADIUS, y: 0 },
          { x: -8, y: -10 },
          { x: -4, y: -4 },
          { x: -12, y: 0 },
          { x: -4, y: 4 },
          { x: -8, y: 10 },
        ])
        .fill({ color: factionColor, alpha: hovered ? 1 : 0.92 })
        .stroke({ color: hovered ? tacticalTheme.hover : 0xffffff, alpha: 0.7, width: 1 });

      const thrust = Math.min(14, Math.hypot(ship.velocity.x, ship.velocity.y) * 2.5);
      if (thrust > 0.5) {
        graphics
          .moveTo(-10, 0)
          .lineTo(-10 - thrust, 0)
          .stroke({ color: factionColor, alpha: 0.7, width: 2.5 });
      }
    },
    [hovered, selected, ship],
  );

  const select = (event: FederatedPointerEvent) => {
    event.stopPropagation();
    onSelect();
  };

  return (
    <pixiContainer
      x={ship.worldPosition.x * WORLD_SCALE}
      y={ship.worldPosition.y * WORLD_SCALE}
      rotation={ship.headingRadians}
      visible={ship.visible}
      eventMode="static"
      cursor="pointer"
      onPointerTap={select}
      onPointerOver={() => setHovered(true)}
      onPointerOut={() => setHovered(false)}
    >
      <pixiGraphics draw={draw} />
      <pixiText
        text={ship.name}
        x={0}
        y={32}
        rotation={-ship.headingRadians}
        anchor={0.5}
        style={{
          fill: tacticalTheme.label,
          fontFamily: "Inter, sans-serif",
          fontSize: 12,
          fontWeight: "600",
          stroke: { color: 0x02050d, width: 3 },
        }}
      />
      <pixiText
        text={`H ${Math.round(ship.hull * 100)}  S ${Math.round(ship.shields * 100)}`}
        x={0}
        y={47}
        rotation={-ship.headingRadians}
        anchor={0.5}
        style={{
          fill: tacticalTheme.labelMuted,
          fontFamily: "SFMono-Regular, monospace",
          fontSize: 9,
          stroke: { color: 0x02050d, width: 2 },
        }}
      />
    </pixiContainer>
  );
};

export const ShipLayer = ({ ships, selectedShipId, onSelectShip }: Props) => (
  <pixiContainer>
    {ships.map((ship) => (
      <ShipGlyph
        key={ship.id}
        ship={ship}
        selected={ship.id === selectedShipId}
        onSelect={() => onSelectShip(ship.id)}
      />
    ))}
  </pixiContainer>
);
