import { useSyncExternalStore } from "react";
import type {
  BattleFrame,
  ConnectionState,
  PlaybackState,
} from "../types";

type Snapshot = Readonly<{
  frame: BattleFrame;
  connectionState: ConnectionState;
  playback: PlaybackState;
  queuedFrames: number;
}>;

type Listener = () => void;

const MAX_BUFFERED_FRAMES = 500;

export class BattleStore {
  private listeners = new Set<Listener>();
  private frameQueue: BattleFrame[] = [];
  private snapshot: Snapshot;

  public constructor(initialFrame: BattleFrame) {
    this.snapshot = {
      frame: initialFrame,
      connectionState: "CONNECTING",
      playback: {
        playing: true,
        speed: 1,
      },
      queuedFrames: 0,
    };
  }

  public getSnapshot = (): Snapshot => this.snapshot;

  public subscribe = (listener: Listener): (() => void) => {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  };

  public setFrame(frame: BattleFrame): void {
    if (!this.snapshot.playback.playing) {
      this.frameQueue.push(frame);
      if (this.frameQueue.length > MAX_BUFFERED_FRAMES) {
        this.frameQueue.splice(0, this.frameQueue.length - MAX_BUFFERED_FRAMES);
      }
      this.updateQueuedFrames();
      return;
    }

    this.snapshot = {
      ...this.snapshot,
      frame,
    };
    this.emit();
  }

  public setConnectionState(connectionState: ConnectionState): void {
    this.snapshot = {
      ...this.snapshot,
      connectionState,
    };
    this.emit();
  }

  public setPlayback(playback: PlaybackState): void {
    const wasPaused = !this.snapshot.playback.playing;
    this.snapshot = {
      ...this.snapshot,
      playback,
    };

    if (wasPaused && playback.playing && this.frameQueue.length > 0) {
      const latest = this.frameQueue.at(-1);
      this.frameQueue = [];
      this.snapshot = {
        ...this.snapshot,
        frame: latest ?? this.snapshot.frame,
        queuedFrames: 0,
      };
    }

    this.emit();
  }

  public stepFrame(): boolean {
    if (this.snapshot.playback.playing || this.frameQueue.length === 0) {
      return false;
    }

    const next = this.frameQueue.shift();
    if (!next) return false;

    this.snapshot = {
      ...this.snapshot,
      frame: next,
      queuedFrames: this.frameQueue.length,
    };
    this.emit();
    return true;
  }

  private updateQueuedFrames(): void {
    this.snapshot = {
      ...this.snapshot,
      queuedFrames: this.frameQueue.length,
    };
    this.emit();
  }

  private emit(): void {
    for (const listener of this.listeners) {
      listener();
    }
  }
}

export const useBattleStore = (store: BattleStore): Snapshot =>
  useSyncExternalStore(
    store.subscribe,
    store.getSnapshot,
    store.getSnapshot,
  );
