import { describe, expect, it, vi } from "vitest";
import { AudioEventBus } from "./AudioEventBus";

describe("AudioEventBus", () => {
  it("emits each observable combat event once", () => {
    const bus = new AudioEventBus();
    const listener = vi.fn();
    bus.subscribe(listener);
    const event = {
      tick: 4,
      type: "BEAM_FIRED",
      message: "beam",
      attributes: { sequence: "2" },
    };
    bus.consume([event]);
    bus.consume([event]);
    expect(listener).toHaveBeenCalledTimes(1);
    expect(listener).toHaveBeenCalledWith("BEAM", event);
  });
});
