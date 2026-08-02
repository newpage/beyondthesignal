import type { CombatVisualEvent } from "../../types";

export type AudioCue =
  | "BEAM"
  | "SHIELD_IMPACT"
  | "HULL_IMPACT"
  | "EXPLOSION"
  | "PROJECTILE";

export type AudioCueListener = (cue: AudioCue, event: CombatVisualEvent) => void;

export class AudioEventBus {
  private readonly listeners = new Set<AudioCueListener>();
  private readonly consumed = new Set<string>();

  public subscribe(listener: AudioCueListener): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  public consume(events: readonly CombatVisualEvent[]): void {
    events.forEach((event, index) => {
      const key = `${event.tick}:${event.type}:${event.attributes.sequence ?? index}`;
      if (this.consumed.has(key)) return;
      this.consumed.add(key);

      const cue = cueFor(event.type);
      if (!cue) return;
      this.listeners.forEach((listener) => listener(cue, event));
    });
  }
}

const cueFor = (type: string): AudioCue | undefined => {
  switch (type) {
    case "BEAM_FIRED": return "BEAM";
    case "SHIELD_IMPACT": return "SHIELD_IMPACT";
    case "HULL_DAMAGE": return "HULL_IMPACT";
    case "SHIP_DESTROYED": return "EXPLOSION";
    case "PROJECTILE_CREATED": return "PROJECTILE";
    default: return undefined;
  }
};
