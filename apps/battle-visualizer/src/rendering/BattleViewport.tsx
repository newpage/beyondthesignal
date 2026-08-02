import { Application, extend } from "@pixi/react";
import { Container, Graphics } from "pixi.js";
import { useMemo } from "react";
import type { BattleFrame } from "../types";
import { useCameraController } from "./CameraController";
import { ShipLayer } from "./ShipLayer";
import { Starfield } from "./Starfield";

extend({ Container, Graphics });

type Props = Readonly<{
  frame: BattleFrame;
}>;

export const BattleViewport = ({ frame }: Props) => {
  const resizeTo = useMemo(() => window, []);
  const { handlers, transform } = useCameraController();

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
          x={window.innerWidth / 2 + transform.x}
          y={window.innerHeight / 2 + transform.y}
          scale={transform.scale}
        >
          <Starfield />
          <ShipLayer ships={frame.ships} />
        </pixiContainer>
      </Application>
    </div>
  );
};
