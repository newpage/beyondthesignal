import { Application, extend } from "@pixi/react";
import { Container, Graphics, Text } from "pixi.js";
import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { TacticalHud } from "../components/TacticalHud";
import { useCameraController } from "./camera/useCameraController";
import { SceneLayer } from "./SceneLayer";
import { Starfield } from "./Starfield";
import { tacticalTheme } from "./theme/TacticalTheme";

extend({ Container, Graphics, Text });

type Props = Readonly<{
  frame: BattleFrame;
  connectionState: import("../types").ConnectionState;
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
  const { handlers, state, reset, fitBattle } = useCameraController(frame);

  return (
    <>
      <div className="camera-input" {...handlers}>
        <Application
          resizeTo={resizeTo}
          backgroundColor={tacticalTheme.background}
          antialias
          autoDensity
          resolution={window.devicePixelRatio}
        >
          <pixiContainer
            x={window.innerWidth / 2 - state.position.x * state.zoom}
            y={window.innerHeight / 2 - state.position.y * state.zoom}
            scale={state.zoom}
          >
            <Starfield />
            <SceneLayer
              frame={frame}
              selectedShipId={selectedShipId}
              onSelectShip={onSelectShip}
            />
          </pixiContainer>
        </Application>
      </div>
      <TacticalHud
        frame={frame}
        connectionState={connectionState}
        zoom={state.zoom}
        selectedShipId={selectedShipId}
        onResetCamera={reset}
        onFitBattle={fitBattle}
      />
    </>
  );
};
