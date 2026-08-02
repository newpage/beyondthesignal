import type { ShipSceneNode } from "../scene/SceneGraph";

const lerp = (from: number, to: number, alpha: number) =>
  from + (to - from) * alpha;

export class SceneInterpolator {
  public interpolateShip(
    previous: ShipSceneNode,
    next: ShipSceneNode,
    alpha: number,
  ): ShipSceneNode {
    const clamped = Math.max(0, Math.min(1, alpha));
    return {
      ...next,
      worldPosition: {
        x: lerp(previous.worldPosition.x, next.worldPosition.x, clamped),
        y: lerp(previous.worldPosition.y, next.worldPosition.y, clamped),
        z: lerp(previous.worldPosition.z, next.worldPosition.z, clamped),
      },
      velocity: {
        x: lerp(previous.velocity.x, next.velocity.x, clamped),
        y: lerp(previous.velocity.y, next.velocity.y, clamped),
        z: lerp(previous.velocity.z, next.velocity.z, clamped),
      },
      headingRadians: lerp(previous.headingRadians, next.headingRadians, clamped),
      hull: lerp(previous.hull, next.hull, clamped),
      shields: lerp(previous.shields, next.shields, clamped),
    };
  }
}
