import type { BattleStore } from "../state/battleStore";
import { advanceDemoFrame } from "./demoBattle";

export class DemoTelemetrySource {
  private timer?: number;
  private lastTimestamp = 0;

  public constructor(private readonly store: BattleStore) {}

  public start(): void {
    this.store.setConnectionState("DEMO");
    this.lastTimestamp = performance.now();

    this.timer = window.setInterval(() => {
      const now = performance.now();
      const elapsed = Math.min(
        0.1,
        (now - this.lastTimestamp) / 1000,
      );
      this.lastTimestamp = now;

      const snapshot = this.store.getSnapshot();

      if (!snapshot.playback.playing) {
        return;
      }

      this.store.setFrame(
        advanceDemoFrame(
          snapshot.frame,
          elapsed * snapshot.playback.speed,
        ),
      );
    }, 50);
  }

  public stop(): void {
    if (this.timer !== undefined) {
      window.clearInterval(this.timer);
    }
  }
}
