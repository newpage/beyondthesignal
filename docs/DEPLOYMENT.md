# Deployment

## Requirements

- Docker Engine 25+
- Docker Compose v2
- 4 GB RAM minimum without Ollama
- Additional RAM/VRAM according to the Ollama model selected

## Local deployment

```bash
cp .env.example .env
# Change POSTGRES_PASSWORD before any shared deployment.
docker compose up -d --build
```

Open the platform dashboard at http://localhost:3000 and the live battle
visualizer at http://localhost:4173.

Check status:

```bash
docker compose ps
curl -fsS http://localhost:8080/api/system/status
```

The visualizer connects to `ws://localhost:8080/ws/battle-telemetry` by
default. Override `VITE_BATTLE_TELEMETRY_WS_URL` before building when the game
server is exposed through another hostname.

## Ollama-enabled deployment

```bash
docker compose --profile ai up -d --build
docker compose exec ollama ollama pull "${OLLAMA_MODEL:-llama3.2:3b}"
```

The AI service starts even when Ollama is not running. Its health response reports Ollama as unavailable rather than crashing the platform.

## Stop

```bash
docker compose down
```

## Reset all local data

```bash
docker compose down -v
```
