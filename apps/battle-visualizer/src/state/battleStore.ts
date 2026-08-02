import { useSyncExternalStore } from "react";
import type {
  BattleFrame,
  ConnectionState,
  PlaybackState,
  ReplayMarker,
} from "../types";

type Snapshot = Readonly<{
  frame: BattleFrame;
  connectionState: ConnectionState;
  playback: PlaybackState;
  queuedFrames: number;
  historySize: number;
  historyIndex: number;
  firstHistoryTick: number;
  lastHistoryTick: number;
  atLiveEdge: boolean;
  replayMarkers: readonly ReplayMarker[];
  bookmarks: readonly number[];
}>;

type Listener = () => void;

const MAX_REPLAY_FRAMES = 5_000;
const MAX_REPLAY_MARKERS = 1_000;

const uniqueMarkers = (markers: readonly ReplayMarker[]): ReplayMarker[] => {
  const seen = new Set<string>();
  return markers.filter((marker) => {
    const key = `${marker.tick}:${marker.type}:${marker.shipId ?? ""}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
};

const deriveMarkers = (
  previous: BattleFrame | undefined,
  current: BattleFrame,
): ReplayMarker[] => {
  if (!previous) {
    return [{
      tick: current.tick,
      type: "BATTLE_START",
      label: "Battle telemetry started",
    }];
  }

  const previousShips = new Map(previous.ships.map((ship) => [ship.id, ship]));
  const markers: ReplayMarker[] = [];

  for (const ship of current.ships) {
    const prior = previousShips.get(ship.id);
    if (!prior) continue;

    if (!prior.selectedTargetId && ship.selectedTargetId) {
      markers.push({
        tick: current.tick,
        type: "TARGET_ACQUIRED",
        label: `${ship.name} acquired a target`,
        shipId: ship.id,
      });
    } else if (
      prior.selectedTargetId
      && ship.selectedTargetId
      && prior.selectedTargetId !== ship.selectedTargetId
    ) {
      markers.push({
        tick: current.tick,
        type: "TARGET_CHANGED",
        label: `${ship.name} changed targets`,
        shipId: ship.id,
      });
    }

    if (prior.hull > 0.25 && ship.hull <= 0.25 && ship.hull > 0) {
      markers.push({
        tick: current.tick,
        type: "SHIP_CRITICAL",
        label: `${ship.name} entered critical hull condition`,
        shipId: ship.id,
      });
    }

    if (prior.hull > 0 && ship.hull <= 0) {
      markers.push({
        tick: current.tick,
        type: "SHIP_DESTROYED",
        label: `${ship.name} was destroyed`,
        shipId: ship.id,
      });
    }
  }

  return markers;
};

export class BattleStore {
  private listeners = new Set<Listener>();
  private history: BattleFrame[];
  private markers: ReplayMarker[];
  private bookmarks = new Set<number>();
  private playhead = 0;
  private snapshot: Snapshot;

  public constructor(initialFrame: BattleFrame) {
    this.history = [initialFrame];
    this.markers = deriveMarkers(undefined, initialFrame);
    this.snapshot = this.buildSnapshot({
      connectionState: "CONNECTING",
      playback: { playing: true, speed: 1 },
    });
  }

  public getSnapshot = (): Snapshot => this.snapshot;

  public subscribe = (listener: Listener): (() => void) => {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  };

  public setFrame(frame: BattleFrame): void {
    const previous = this.history.at(-1);
    const wasAtLiveEdge = this.playhead === this.history.length - 1;

    if (previous?.battleId !== frame.battleId) {
      this.history = [frame];
      this.markers = deriveMarkers(undefined, frame);
      this.bookmarks.clear();
      this.playhead = 0;
      this.refresh();
      return;
    }

    if (previous?.tick === frame.tick) {
      this.history[this.history.length - 1] = frame;
    } else {
      this.history.push(frame);
      this.markers.push(...deriveMarkers(previous, frame));
      this.markers = uniqueMarkers(this.markers).slice(-MAX_REPLAY_MARKERS);
    }

    if (this.history.length > MAX_REPLAY_FRAMES) {
      const removed = this.history.length - MAX_REPLAY_FRAMES;
      this.history.splice(0, removed);
      this.playhead = Math.max(0, this.playhead - removed);
      const firstTick = this.history[0]?.tick ?? frame.tick;
      this.markers = this.markers.filter((marker) => marker.tick >= firstTick);
      this.bookmarks = new Set(
        [...this.bookmarks].filter((tick) => tick >= firstTick),
      );
    }

    if (this.snapshot.playback.playing && wasAtLiveEdge) {
      this.playhead = this.history.length - 1;
    }

    this.refresh();
  }

  public setConnectionState(connectionState: ConnectionState): void {
    this.snapshot = {
      ...this.snapshot,
      connectionState,
    };
    this.emit();
  }

  public setPlayback(playback: PlaybackState): void {
    this.snapshot = {
      ...this.snapshot,
      playback,
    };
    this.emit();
  }

  public stepFrame(): boolean {
    return this.stepForward();
  }

  public stepForward(): boolean {
    if (this.playhead >= this.history.length - 1) return false;
    this.playhead += 1;
    this.refresh();
    return true;
  }

  public stepBackward(): boolean {
    if (this.playhead <= 0) return false;
    this.playhead -= 1;
    this.refresh();
    return true;
  }

  public advancePlayback(): boolean {
    if (!this.snapshot.playback.playing) return false;
    return this.stepForward();
  }

  public seek(index: number): void {
    if (!Number.isFinite(index)) return;
    this.playhead = Math.max(
      0,
      Math.min(Math.round(index), this.history.length - 1),
    );
    this.refresh();
  }

  public seekTick(tick: number): void {
    if (this.history.length === 0) return;
    let closestIndex = 0;
    let closestDistance = Math.abs(this.history[0].tick - tick);
    for (let index = 1; index < this.history.length; index++) {
      const distance = Math.abs(this.history[index].tick - tick);
      if (distance < closestDistance) {
        closestIndex = index;
        closestDistance = distance;
      }
    }
    this.seek(closestIndex);
  }

  public jumpToLive(): void {
    this.playhead = this.history.length - 1;
    this.refresh();
  }

  public jumpByFrames(offset: number): void {
    this.seek(this.playhead + offset);
  }

  public toggleBookmark(): void {
    const tick = this.history[this.playhead]?.tick;
    if (tick === undefined) return;
    if (this.bookmarks.has(tick)) {
      this.bookmarks.delete(tick);
    } else {
      this.bookmarks.add(tick);
    }
    this.refresh();
  }

  private buildSnapshot(
    retained?: Pick<Snapshot, "connectionState" | "playback">,
  ): Snapshot {
    const frame = this.history[this.playhead];
    const connectionState = retained?.connectionState
      ?? this.snapshot?.connectionState
      ?? "CONNECTING";
    const playback = retained?.playback
      ?? this.snapshot?.playback
      ?? { playing: true, speed: 1 };

    return {
      frame,
      connectionState,
      playback,
      queuedFrames: Math.max(0, this.history.length - 1 - this.playhead),
      historySize: this.history.length,
      historyIndex: this.playhead,
      firstHistoryTick: this.history[0]?.tick ?? frame.tick,
      lastHistoryTick: this.history.at(-1)?.tick ?? frame.tick,
      atLiveEdge: this.playhead === this.history.length - 1,
      replayMarkers: [...this.markers],
      bookmarks: [...this.bookmarks].sort((left, right) => left - right),
    };
  }

  private refresh(): void {
    this.snapshot = this.buildSnapshot();
    this.emit();
  }

  private emit(): void {
    for (const listener of this.listeners) listener();
  }
}

export const useBattleStore = (store: BattleStore): Snapshot =>
  useSyncExternalStore(
    store.subscribe,
    store.getSnapshot,
    store.getSnapshot,
  );
