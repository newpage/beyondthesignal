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
}>;

type Listener = () => void;

export class BattleStore {
  private listeners = new Set<Listener>();
  private snapshot: Snapshot;

  public constructor(initialFrame: BattleFrame) {
    this.snapshot = {
      frame: initialFrame,
      connectionState: "CONNECTING",
      playback: {
        playing: true,
        speed: 1,
      },
    };
  }

  public getSnapshot = (): Snapshot => this.snapshot;

  public subscribe = (listener: Listener): (() => void) => {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  };

  public setFrame(frame: BattleFrame): void {
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
    this.snapshot = {
      ...this.snapshot,
      playback,
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
