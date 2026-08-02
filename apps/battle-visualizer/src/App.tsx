import { useEffect, useMemo, useState } from "react";
import { ShipInspector } from "./components/ShipInspector";
import { SimulationClock } from "./components/SimulationClock";
import { ReplayTimeline } from "./replay/ReplayTimeline";
import { BattleViewport } from "./rendering/BattleViewport";
import { BattleStore, useBattleStore } from "./state/battleStore";
import { BattleTelemetryClient } from "./telemetry/BattleTelemetryClient";
import { DemoTelemetrySource } from "./telemetry/DemoTelemetrySource";
import { initialDemoFrame } from "./telemetry/demoBattle";

const DEFAULT_WEBSOCKET_URL = "ws://localhost:8080/ws/battle-telemetry";

export const App = () => {
  const store = useMemo(() => new BattleStore(initialDemoFrame), []);
  const snapshot = useBattleStore(store);
  const [selectedShipId, setSelectedShipId] = useState<string>();

  useEffect(() => {
    const configuredUrl = import.meta.env.VITE_BATTLE_TELEMETRY_WS_URL as string | undefined;
    const useDemo = import.meta.env.VITE_USE_DEMO_TELEMETRY === "true";
    if (useDemo) {
      const demo = new DemoTelemetrySource(store);
      demo.start();
      return () => demo.stop();
    }

    const client = new BattleTelemetryClient(
      configuredUrl || DEFAULT_WEBSOCKET_URL,
      (frame) => store.setFrame(frame),
      (status) => store.setConnectionState(status),
    );
    client.start();
    return () => client.stop();
  }, [store]);

  useEffect(() => {
    if (selectedShipId && !snapshot.frame.ships.some((ship) => ship.id === selectedShipId)) {
      setSelectedShipId(undefined);
    }
  }, [selectedShipId, snapshot.frame]);

  useEffect(() => {
    if (!snapshot.playback.playing || snapshot.atLiveEdge) return undefined;
    const intervalMs = Math.max(20, Math.round(50 / snapshot.playback.speed));
    const timer = window.setInterval(() => {
      if (!store.advancePlayback()) {
        store.setPlayback({ ...store.getSnapshot().playback, playing: false });
      }
    }, intervalMs);
    return () => window.clearInterval(timer);
  }, [snapshot.atLiveEdge, snapshot.playback.playing, snapshot.playback.speed, store]);

  const selectedShip = snapshot.frame.ships.find((ship) => ship.id === selectedShipId);

  return (
    <main className="app-shell">
      <SimulationClock
        battleId={snapshot.frame.battleId}
        tick={snapshot.frame.tick}
        simulationTimeSeconds={snapshot.frame.simulationTimeSeconds}
        connectionState={snapshot.connectionState}
        playback={snapshot.playback}
        queuedFrames={snapshot.queuedFrames}
        atLiveEdge={snapshot.atLiveEdge}
        canStepBackward={snapshot.historyIndex > 0}
        canStepForward={snapshot.historyIndex < snapshot.historySize - 1}
        onPlaybackChange={(playback) => store.setPlayback(playback)}
        onStepBackward={() => store.stepBackward()}
        onStepForward={() => store.stepForward()}
        onJumpBackward={() => store.jumpByFrames(-10)}
        onJumpForward={() => store.jumpByFrames(10)}
        onJumpToLive={() => store.jumpToLive()}
      />

      <section className="battle-stage">
        <BattleViewport
          frame={snapshot.frame}
          connectionState={snapshot.connectionState}
          selectedShipId={selectedShipId}
          onSelectShip={setSelectedShipId}
        />

        <ShipInspector ship={selectedShip} onClose={() => setSelectedShipId(undefined)} />

        <ReplayTimeline
          currentIndex={snapshot.historyIndex}
          totalFrames={snapshot.historySize}
          firstTick={snapshot.firstHistoryTick}
          lastTick={snapshot.lastHistoryTick}
          currentTick={snapshot.frame.tick}
          atLiveEdge={snapshot.atLiveEdge}
          markers={snapshot.replayMarkers}
          bookmarks={snapshot.bookmarks}
          onSeek={(index) => store.seek(index)}
          onToggleBookmark={() => store.toggleBookmark()}
          onJumpToMarker={(tick) => store.seekTick(tick)}
        />

        <aside className="legend">
          <span><i className="alliance" />Alliance</span>
          <span><i className="hostile" />Hostile</span>
          <small>Drag pan · Wheel zoom · Shift-drag range · Double-click reset</small>
        </aside>
      </section>
    </main>
  );
};
