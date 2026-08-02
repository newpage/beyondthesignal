import { describe, expect, it } from "vitest";
import { Camera2D } from "./Camera2D";

describe("Camera2D", () => {
  it("round trips world and screen coordinates", () => {
    const camera = new Camera2D({ position: { x: 10, y: -5 }, zoom: 2 });
    const viewport = { x: 1000, y: 800 };
    const world = { x: 25, y: 15 };
    expect(camera.screenToWorld(camera.worldToScreen(world, viewport), viewport)).toEqual(world);
  });

  it("clamps zoom", () => {
    const camera = new Camera2D();
    expect(camera.zoomBy(100, 0.25, 4).zoom).toBe(4);
    expect(camera.zoomBy(0.001, 0.25, 4).zoom).toBe(0.25);
  });
});
