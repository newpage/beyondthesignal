import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { AIIntelligenceLayer } from "./AIIntelligenceLayer";
import { BattleSceneAdapter } from "./adapters/BattleSceneAdapter";
import { ShipLayer } from "./ShipLayer";
import type {
  EngagementSceneNode,
  ShipSceneNode,
  ProjectileSceneNode,
  WreckSceneNode,
} from "./scene/SceneGraph";
import { WeaponEffectLayer } from "./WeaponEffectLayer";
import { ProjectileLayer } from "./ProjectileLayer";
import { WreckLayer } from "./WreckLayer";
import { TargetingReticleLayer } from "./TargetingReticleLayer";
import { FleetDecisionOverlay } from "./FleetDecisionOverlay";
import { CinematicEffectsLayer } from "./cinematic/CinematicEffectsLayer";
import type { VisualQuality } from "./cinematic/VisualQuality";

type Props = Readonly<{
  frame: BattleFrame;
  selectedShipId?: string;
  onSelectShip: (shipId: string) => void;
  visualQuality: VisualQuality;
}>;

export const SceneLayer = ({ frame, selectedShipId, onSelectShip, visualQuality }: Props) => {
  const adapter = useMemo(() => new BattleSceneAdapter(), []);
  const scene = useMemo(() => adapter.adapt(frame), [adapter, frame]);
  const ships = scene.byType<ShipSceneNode>("SHIP");
  const engagements = scene.byType<EngagementSceneNode>("ENGAGEMENT");
  const projectiles = scene.byType<ProjectileSceneNode>("PROJECTILE");
  const wrecks = scene.byType<WreckSceneNode>("WRECK");
  const selectedShip = selectedShipId
    ? ships.find((ship) => ship.id === selectedShipId)
    : undefined;
  const targetShip = selectedShip?.selectedTargetId
    ? ships.find((ship) => ship.id === selectedShip.selectedTargetId)
    : undefined;

  return (
    <>
      <CinematicEffectsLayer frame={frame} ships={ships} quality={visualQuality} />
      <FleetDecisionOverlay frame={frame} />
      <AIIntelligenceLayer selectedShip={selectedShip} targetShip={targetShip} />
      <WeaponEffectLayer engagements={engagements} />
      <ProjectileLayer projectiles={projectiles} />
      <WreckLayer wrecks={wrecks} currentTick={frame.tick} />
      <TargetingReticleLayer
        selectedShip={selectedShip}
        targetShip={targetShip}
      />
      <ShipLayer
        ships={ships}
        selectedShipId={selectedShipId}
        onSelectShip={onSelectShip}
      />
    </>
  );
};
