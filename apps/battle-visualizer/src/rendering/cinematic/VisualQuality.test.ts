import { describe, expect, it } from "vitest";
import { visualQualityProfile } from "./VisualQuality";

describe("visualQualityProfile", () => {
  it("scales effects without changing simulation data", () => {
    expect(visualQualityProfile("LOW").particleCount).toBe(0);
    expect(visualQualityProfile("ULTRA").particleCount).toBe(10);
    expect(visualQualityProfile("HIGH").cameraShake).toBe(true);
  });
});
