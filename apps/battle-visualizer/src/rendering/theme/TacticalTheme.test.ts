import { describe, expect, it } from "vitest";
import { statusColor } from "./TacticalTheme";

describe("statusColor", () => {
  it("selects tactical thresholds deterministically", () => {
    expect(statusColor(1, 1, 2, 3, 4)).toBe(1);
    expect(statusColor(0.6, 1, 2, 3, 4)).toBe(2);
    expect(statusColor(0.3, 1, 2, 3, 4)).toBe(3);
    expect(statusColor(0.1, 1, 2, 3, 4)).toBe(4);
  });
});
