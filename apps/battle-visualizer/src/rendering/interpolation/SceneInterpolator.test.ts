import { describe, expect, it } from "vitest";
import { SceneInterpolator } from "./SceneInterpolator";
import type { ShipSceneNode } from "../scene/SceneGraph";

const ship = (x: number): ShipSceneNode => ({
  id: "ship-1", type: "SHIP", visible: true,
  worldPosition: { x, y: 0, z: 0 }, name: "Horizon", side: "ALLIANCE",
  velocity: { x: 0, y: 0, z: 0 }, headingRadians: 0,
  hull: 1, shields: 1,
});

describe("SceneInterpolator", () => {
  it("interpolates ship positions", () => {
    const result = new SceneInterpolator().interpolateShip(ship(0), ship(100), 0.25);
    expect(result.worldPosition.x).toBe(25);
  });
});
