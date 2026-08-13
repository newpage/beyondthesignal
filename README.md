# Beyond the Signal

**Beyond the Signal** is an AI-first episodic starship command game. The player commands an original exploration vessel, works with human or AI bridge officers, investigates unknown phenomena, and makes decisions whose consequences persist across episodes.

The current release is **Milestone 7.3: Battle Scenario Control**.

## Architecture

- **React + TypeScript** health dashboard
- **React + PixiJS** tactical battle visualizer
- **Java 21 + Eclipse Vert.x 5** authoritative game server
- **Python 3.12 + FastAPI** AI service
- **PostgreSQL** authoritative persistence
- **Redis** messaging/readiness foundation
- **Ollama** optional local model runtime
- **Flyway SQL migrations**
- **jOOQ code generation profile**
- **Docker Compose** deployment

## Start

```bash
cp .env.example .env
docker compose up -d --build
```

Open:

- Dashboard: http://localhost:3000
- Battle visualizer: http://localhost:4173
- Game API: http://localhost:8080/api/health
- System status: http://localhost:8080/api/system/status
- AI API: http://localhost:8000/health
- AI OpenAPI: http://localhost:8000/docs

Ollama remains optional. To launch it too:

```bash
docker compose --profile ai up -d --build
```

Then pull the configured model:

```bash
docker compose exec ollama ollama pull llama3.2:3b
```

## Test

See [docs/TESTING.md](docs/TESTING.md) and [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md).
