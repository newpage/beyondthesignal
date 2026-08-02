import { Application, extend } from "@pixi/react";
import { Container, Graphics } from "pixi.js";
import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { useCameraController } from "./camera/useCameraController";
import { SceneLayer } from "./SceneLayer";
import { Starfield } from "./Starfield";

extend({ Container, Graphics });

type Props = Readonly<{ frame: BattleFrame }>;

export const BattleViewport = ({ frame }: Props) => {
  const resizeTo = useMemo(() => window, []);
  const { handlers, state } = useCameraController();

  return (
    <div className="camera-input" {...handlers}>
      <Application
        resizeTo={resizeTo}
        backgroundColor={0x030713}
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
          <SceneLayer frame={frame} />
        </pixiContainer>
      </Application>
    </div>
  );
};
