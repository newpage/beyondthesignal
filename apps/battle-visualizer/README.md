# Tactical Battle Visualizer

Milestone 4.1 establishes the browser visualization runtime for Beyond the
Signal.

## Run locally

```bash
cd apps/battle-visualizer
npm install
npm run dev
```

The application runs deterministic demo telemetry by default.

To use a live game-server WebSocket:

```bash
cp .env.example .env.local
```

Then set:

```text
VITE_BATTLE_TELEMETRY_WS_URL=ws://localhost:8080/ws/battle-telemetry
```

Expected WebSocket messages are JSON `BattleFrame` objects matching
`src/types.ts`.

## Included in Sprint 4.1

- React and PixiJS application shell
- Responsive tactical canvas
- Deterministic starfield
- Ship glyph rendering
- Camera pan and zoom input
- Simulation clock
- Play/pause and playback speed
- Reconnecting WebSocket telemetry client
- Deterministic demo telemetry fallback

The server-side WebSocket publisher and tactical overlays are intentionally
reserved for the next integration sprint.
