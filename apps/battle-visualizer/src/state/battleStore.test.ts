import { describe, expect, it } from "vitest";
import { BattleStore } from "./battleStore";
import type { BattleFrame } from "../types";

const frame = (tick: number): BattleFrame => ({
  battleId: "battle",
  tick,
  simulationTimeSeconds: tick * 0.05,
  ships: [],
});

describe("BattleStore playback", () => {
  it("buffers live frames while paused and steps in order", () => {
    const store = new BattleStore(frame(1));
    store.setPlayback({ playing: false, speed: 1 });
    store.setFrame(frame(2));
    store.setFrame(frame(3));

    expect(store.getSnapshot().frame.tick).toBe(1);
    expect(store.getSnapshot().queuedFrames).toBe(2);

    expect(store.stepFrame()).toBe(true);
    expect(store.getSnapshot().frame.tick).toBe(2);
    expect(store.getSnapshot().queuedFrames).toBe(1);
  });

  it("catches up to the latest frame when playback resumes", () => {
    const store = new BattleStore(frame(1));
    store.setPlayback({ playing: false, speed: 1 });
    store.setFrame(frame(2));
    store.setFrame(frame(3));

    store.setPlayback({ playing: true, speed: 1 });

    expect(store.getSnapshot().frame.tick).toBe(3);
    expect(store.getSnapshot().queuedFrames).toBe(0);
  });
});
