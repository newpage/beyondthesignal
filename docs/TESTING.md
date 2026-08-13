# Testing

## Milestone 7.2.3 acceptance test

1. Run `docker compose up -d --build`.
2. Confirm all required containers are healthy or running.
3. Open http://localhost:3000.
4. Confirm Game Server, PostgreSQL, Redis, and AI Service report `UP`.
5. Open http://localhost:4173 and confirm live ships move, can be selected, and display vessel details.
6. Confirm Ollama reports either `UP` or `DEGRADED`; Ollama is optional for the base deployment.
7. Restart the stack and confirm the Flyway migration remains successful.
8. Run the automated tests below.

## Java tests

```bash
cd apps/game-server
mvn test
```

## Python tests

```bash
cd apps/ai-service
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
pytest
```

## Frontend tests

```bash
cd apps/web
npm install
npm test -- --run
```

## Battle visualizer tests

```bash
cd apps/battle-visualizer
npm ci
npm test
npm run build
```
