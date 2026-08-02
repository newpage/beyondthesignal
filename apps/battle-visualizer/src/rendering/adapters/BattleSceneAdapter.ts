import type { BattleFrame } from "../../types";
import { SceneGraph, type ShipSceneNode } from "../scene/SceneGraph";

export class BattleSceneAdapter {
  public adapt(frame: BattleFrame): SceneGraph {
    const scene = new SceneGraph();

    for (const ship of frame.ships) {
      const node: ShipSceneNode = {
        id: ship.id,
        type: "SHIP",
        visible: true,
        worldPosition: ship.position,
        name: ship.name,
        side: ship.side,
        velocity: ship.velocity,
        headingRadians: ship.headingRadians,
        hull: ship.hull,
        shields: ship.shields,
        selectedTargetId: ship.selectedTargetId,
      };
      scene.upsert(node);
    }

    return scene;
  }
}
