import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { BattleSceneAdapter } from "./adapters/BattleSceneAdapter";
import { ShipLayer } from "./ShipLayer";
import type {
  EngagementSceneNode,
  ShipSceneNode,
} from "./scene/SceneGraph";
import { WeaponEffectLayer } from "./WeaponEffectLayer";

type Props = Readonly<{
  frame: BattleFrame;
  selectedShipId?: string;
  onSelectShip: (shipId: string) => void;
}>;

export const SceneLayer = ({ frame, selectedShipId, onSelectShip }: Props) => {
  const adapter = useMemo(() => new BattleSceneAdapter(), []);
  const scene = useMemo(() => adapter.adapt(frame), [adapter, frame]);
  const ships = scene.byType<ShipSceneNode>("SHIP");
  const engagements = scene.byType<EngagementSceneNode>("ENGAGEMENT");

  return (
    <>
      <WeaponEffectLayer engagements={engagements} />
      <ShipLayer
        ships={ships}
        selectedShipId={selectedShipId}
        onSelectShip={onSelectShip}
      />
    </>
  );
};
