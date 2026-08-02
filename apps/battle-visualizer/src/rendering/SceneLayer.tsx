import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { BattleSceneAdapter } from "./adapters/BattleSceneAdapter";
import { ShipLayer } from "./ShipLayer";
import type { ShipSceneNode } from "./scene/SceneGraph";

type Props = Readonly<{
  frame: BattleFrame;
  selectedShipId?: string;
  onSelectShip: (shipId: string) => void;
}>;

export const SceneLayer = ({ frame, selectedShipId, onSelectShip }: Props) => {
  const adapter = useMemo(() => new BattleSceneAdapter(), []);
  const scene = useMemo(() => adapter.adapt(frame), [adapter, frame]);
  const ships = scene.byType<ShipSceneNode>("SHIP");

  return (
    <ShipLayer
      ships={ships}
      selectedShipId={selectedShipId}
      onSelectShip={onSelectShip}
    />
  );
};
