import { describe, expect, it } from "vitest";
import { RenderLayerRegistry } from "./RenderLayerRegistry";

describe("RenderLayerRegistry", () => {
  it("returns deterministic layer order", () => {
    const layers = RenderLayerRegistry.defaults().ordered();
    expect(layers[0]?.id).toBe("background");
    expect(layers.at(-1)?.id).toBe("debug");
  });
});
