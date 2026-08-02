import { describe, expect, it } from "vitest";
import { SceneGraph, type ShipSceneNode } from "./SceneGraph";

describe("SceneGraph", () => {
  it("upserts, queries and removes nodes", () => {
    const graph = new SceneGraph();
    const ship: ShipSceneNode = {
      id: "ship-1", type: "SHIP", visible: true,
      worldPosition: { x: 0, y: 0, z: 0 },
      name: "Horizon", side: "ALLIANCE",
      velocity: { x: 1, y: 0, z: 0 }, headingRadians: 0,
      hull: 1, shields: 1,
    };
    graph.upsert(ship);
    expect(graph.get("ship-1")).toEqual(ship);
    expect(graph.byType("SHIP")).toHaveLength(1);
    expect(graph.remove("ship-1")).toBe(true);
  });
});
