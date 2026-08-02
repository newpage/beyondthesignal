import type {
  ConnectionState,
  PlaybackState,
} from "../types";

type Props = Readonly<{
  battleId: string;
  tick: number;
  simulationTimeSeconds: number;
  connectionState: ConnectionState;
  playback: PlaybackState;
  queuedFrames: number;
  onPlaybackChange: (playback: PlaybackState) => void;
  onStepFrame: () => void;
}>;

export const SimulationClock = ({
  battleId,
  tick,
  simulationTimeSeconds,
  connectionState,
  playback,
  queuedFrames,
  onPlaybackChange,
  onStepFrame,
}: Props) => (
  <header className="simulation-clock">
    <div>
      <span className="eyebrow">TACTICAL BATTLE VISUALIZER</span>
      <strong>{battleId}</strong>
    </div>

    <dl>
      <div>
        <dt>Tick</dt>
        <dd>{tick.toLocaleString()}</dd>
      </div>
      <div>
        <dt>Simulation</dt>
        <dd>{simulationTimeSeconds.toFixed(2)}s</dd>
      </div>
      <div>
        <dt>Telemetry</dt>
        <dd data-state={connectionState}>{connectionState}</dd>
      </div>
    </dl>

    <div className="playback-controls">
      <button
        type="button"
        onClick={() =>
          onPlaybackChange({
            ...playback,
            playing: !playback.playing,
          })
        }
      >
        {playback.playing ? "Pause" : "Play"}
      </button>

      <button
        type="button"
        disabled={playback.playing || queuedFrames === 0}
        onClick={onStepFrame}
        title="Advance one buffered telemetry frame"
      >
        Step{queuedFrames > 0 ? ` (${queuedFrames})` : ""}
      </button>

      {[1, 2, 5, 10].map((speed) => (
        <button
          key={speed}
          type="button"
          className={playback.speed === speed ? "active" : undefined}
          onClick={() =>
            onPlaybackChange({
              ...playback,
              speed,
            })
          }
        >
          {speed}×
        </button>
      ))}
    </div>
  </header>
);
