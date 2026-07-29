.PHONY: up up-ai down logs test status clean jooq

up:
	docker compose up -d --build

up-ai:
	docker compose --profile ai up -d --build

down:
	docker compose down

logs:
	docker compose logs -f --tail=200

test:
	cd apps/game-server && mvn test
	cd apps/ai-service && python -m pytest
	cd apps/web && npm test -- --run

status:
	curl -fsS http://localhost:8080/api/system/status | python -m json.tool

jooq:
	./scripts/generate-jooq.sh

clean:
	docker compose down -v --remove-orphans
