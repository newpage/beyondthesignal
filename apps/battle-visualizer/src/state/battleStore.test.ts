import { describe, expect, it } from "vitest";
import { BattleStore } from "./battleStore";
import type { BattleFrame } from "../types";

const frame = (
  tick: number,
  options: Partial<BattleFrame> = {},
): BattleFrame => ({
  battleId: "battle",
  tick,
  simulationTimeSeconds: tick * 0.05,
  ships: [],
  ...options,
});

describe("BattleStore time machine", () => {
  it("retains live frames and allows backward and forward stepping", () => {
    const store = new BattleStore(frame(1));
    store.setFrame(frame(2));
    store.setFrame(frame(3));

    expect(store.getSnapshot().frame.tick).toBe(3);
    expect(store.stepBackward()).toBe(true);
    expect(store.getSnapshot().frame.tick).toBe(2);
    expect(store.getSnapshot().atLiveEdge).toBe(false);
    expect(store.stepForward()).toBe(true);
    expect(store.getSnapshot().frame.tick).toBe(3);
  });

  it("continues retaining live frames while replaying history", () => {
    const store = new BattleStore(frame(1));
    store.setFrame(frame(2));
    store.stepBackward();
    store.setFrame(frame(3));

    expect(store.getSnapshot().frame.tick).toBe(1);
    expect(store.getSnapshot().queuedFrames).toBe(2);
    store.jumpToLive();
    expect(store.getSnapshot().frame.tick).toBe(3);
  });

  it("seeks by history index and nearest tick", () => {
    const store = new BattleStore(frame(10));
    store.setFrame(frame(20));
    store.setFrame(frame(30));

    store.seek(0);
    expect(store.getSnapshot().frame.tick).toBe(10);
    store.seekTick(24);
    expect(store.getSnapshot().frame.tick).toBe(20);
  });

  it("adds and removes bookmarks at the current tick", () => {
    const store = new BattleStore(frame(7));
    store.toggleBookmark();
    expect(store.getSnapshot().bookmarks).toEqual([7]);
    store.toggleBookmark();
    expect(store.getSnapshot().bookmarks).toEqual([]);
  });

  it("derives target acquisition markers from observable frame changes", () => {
    const ship = {
      id: "ship-1",
      name: "Horizon",
      side: "ALLIANCE" as const,
      position: { x: 0, y: 0, z: 0 },
      velocity: { x: 0, y: 0, z: 0 },
      headingRadians: 0,
      hull: 1,
      shields: 1,
    };
    const store = new BattleStore(frame(1, { ships: [ship] }));
    store.setFrame(frame(2, {
      wrecks: [],
    projectiles: [],
      events: [],
      ships: [{ ...ship, selectedTargetId: "enemy-1" }],
    }));

    expect(store.getSnapshot().replayMarkers.some(
      (marker) => marker.type === "TARGET_ACQUIRED" && marker.tick === 2,
    )).toBe(true);
  });
});
