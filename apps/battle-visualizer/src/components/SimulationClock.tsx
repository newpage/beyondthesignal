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
  atLiveEdge: boolean;
  canStepBackward: boolean;
  canStepForward: boolean;
  onPlaybackChange: (playback: PlaybackState) => void;
  onStepBackward: () => void;
  onStepForward: () => void;
  onJumpBackward: () => void;
  onJumpForward: () => void;
  onJumpToLive: () => void;
}>;

export const SimulationClock = ({
  battleId,
  tick,
  simulationTimeSeconds,
  connectionState,
  playback,
  queuedFrames,
  atLiveEdge,
  canStepBackward,
  canStepForward,
  onPlaybackChange,
  onStepBackward,
  onStepForward,
  onJumpBackward,
  onJumpForward,
  onJumpToLive,
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
      <div>
        <dt>Position</dt>
        <dd data-state={atLiveEdge ? "CONNECTED" : "DEMO"}>
          {atLiveEdge ? "LIVE" : `REPLAY +${queuedFrames}`}
        </dd>
      </div>
    </dl>

    <div className="playback-controls replay-controls">
      <button type="button" disabled={!canStepBackward} onClick={onJumpBackward} title="Rewind 10 frames">
        −10
      </button>
      <button type="button" disabled={!canStepBackward} onClick={onStepBackward} title="Previous frame">
        ◀
      </button>
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
      <button type="button" disabled={!canStepForward} onClick={onStepForward} title="Next frame">
        ▶
      </button>
      <button type="button" disabled={!canStepForward} onClick={onJumpForward} title="Advance 10 frames">
        +10
      </button>
      <button
        type="button"
        disabled={atLiveEdge}
        className={atLiveEdge ? "active" : undefined}
        onClick={onJumpToLive}
      >
        Live
      </button>

      {[0.25, 0.5, 1, 2, 5].map((speed) => (
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
