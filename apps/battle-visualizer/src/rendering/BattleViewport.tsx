import { Application, extend } from "@pixi/react";
import { Container, Graphics, Text } from "pixi.js";
import { useMemo } from "react";
import type { BattleFrame, ConnectionState } from "../types";
import { RangeMeasurement } from "../components/RangeMeasurement";
import { TacticalHud } from "../components/TacticalHud";
import { TacticalMinimap } from "../components/TacticalMinimap";
import { useCameraController } from "./camera/useCameraController";
import { SceneLayer } from "./SceneLayer";
import { Starfield } from "./Starfield";
import { tacticalTheme } from "./theme/TacticalTheme";

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
            x={window.innerWidth / 2 - camera.state.position.x * camera.state.zoom}
            y={window.innerHeight / 2 - camera.state.position.y * camera.state.zoom}
            scale={camera.state.zoom}
          >
            <Starfield />
            <SceneLayer
              frame={frame}
              selectedShipId={selectedShipId}
              onSelectShip={onSelectShip}
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
      />

      <TacticalMinimap
        frame={frame}
        camera={camera.state}
        onCenterAt={camera.centerAt}
      />
    </>
  );
};
