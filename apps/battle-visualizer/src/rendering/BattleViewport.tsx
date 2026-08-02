import { Application, extend } from "@pixi/react";
import { Container, Graphics, Text } from "pixi.js";
import { useEffect, useMemo, useState } from "react";
import type { BattleFrame, ConnectionState } from "../types";
import { RangeMeasurement } from "../components/RangeMeasurement";
import { TacticalHud } from "../components/TacticalHud";
import { TacticalMinimap } from "../components/TacticalMinimap";
import { useCameraController } from "./camera/useCameraController";
import { SceneLayer } from "./SceneLayer";
import { Starfield } from "./Starfield";
import { tacticalTheme } from "./theme/TacticalTheme";
import { TacticalGridLayer } from "./TacticalGridLayer";
import { AudioEventBus } from "./cinematic/AudioEventBus";
import type { VisualQuality } from "./cinematic/VisualQuality";

extend({ Container, Graphics, Text });

type Props = Readonly<{
  frame: BattleFrame;
  connectionState: ConnectionState;
  selectedShipId?: string;
  onSelectShip: (shipId: string) => void;
}>;

export const BattleViewport = ({
  frame,
  connectionState,
  selectedShipId,
  onSelectShip,
}: Props) => {
  const resizeTo = useMemo(() => window, []);
  const camera = useCameraController(frame, selectedShipId);
  const [visualQuality, setVisualQuality] =
    useState<VisualQuality>("HIGH");
  const audioBus = useMemo(() => new AudioEventBus(), []);

  useEffect(() => {
    audioBus.consume(frame.events ?? []);
  }, [audioBus, frame.events]);

  const shake = useMemo(() => {
    const heavyImpact = (frame.events ?? []).some((event) =>
      event.type === "SHIP_DESTROYED" || event.type === "HULL_DAMAGE"
    );
    if (!heavyImpact || visualQuality === "LOW") return { x: 0, y: 0 };
    const magnitude = visualQuality === "ULTRA" ? 5 : 3;
    return {
      x: Math.sin(frame.tick * 2.17) * magnitude,
      y: Math.cos(frame.tick * 1.73) * magnitude,
    };
  }, [frame.events, frame.tick, visualQuality]);

  return (
    <>
      <div className="camera-input" {...camera.handlers}>
        <Application
          resizeTo={resizeTo}
          backgroundColor={tacticalTheme.background}
          antialias
          autoDensity
          resolution={window.devicePixelRatio}
        >
          <pixiContainer
            x={window.innerWidth / 2 - camera.state.position.x * camera.state.zoom + shake.x}
            y={window.innerHeight / 2 - camera.state.position.y * camera.state.zoom + shake.y}
            scale={camera.state.zoom}
          >
            <Starfield />
            <TacticalGridLayer />
            <SceneLayer
              frame={frame}
              selectedShipId={selectedShipId}
              onSelectShip={onSelectShip}
              visualQuality={visualQuality}
            />
          </pixiContainer>
        </Application>
        <RangeMeasurement measurement={camera.measurement} />
      </div>

      <TacticalHud
        frame={frame}
        connectionState={connectionState}
        zoom={camera.state.zoom}
        selectedShipId={selectedShipId}
        followingSelection={camera.followingSelection}
        hasMeasurement={Boolean(camera.measurement)}
        onResetCamera={camera.reset}
        onFitBattle={camera.fitBattle}
        onToggleFollow={() => camera.setFollowingSelection(!camera.followingSelection)}
        onClearMeasurement={camera.clearMeasurement}
        visualQuality={visualQuality}
        onVisualQualityChange={setVisualQuality}
      />

      <TacticalMinimap
        frame={frame}
        camera={camera.state}
        onCenterAt={camera.centerAt}
      />
    </>
  );
};
