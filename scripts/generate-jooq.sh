#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

docker compose up -d postgres
until docker compose exec -T postgres pg_isready -U "${POSTGRES_USER:-beyond_signal}" -d "${POSTGRES_DB:-beyond_signal}" >/dev/null 2>&1; do sleep 1; done

docker compose run --rm game-server true >/dev/null 2>&1 || true

export JOOQ_JDBC_URL="jdbc:postgresql://localhost:${POSTGRES_PORT:-5432}/${POSTGRES_DB:-beyond_signal}"
export JOOQ_DB_USER="${POSTGRES_USER:-beyond_signal}"
export JOOQ_DB_PASSWORD="${POSTGRES_PASSWORD:-change-me}"

# PostgreSQL is internal by default, so use Maven inside the network and override the URL.
docker run --rm --network beyond-the-signal_beyond-signal \
  -v "$ROOT/apps/game-server:/workspace" -w /workspace \
  -e JOOQ_JDBC_URL="jdbc:postgresql://postgres:5432/${POSTGRES_DB:-beyond_signal}" \
  -e JOOQ_DB_USER="$JOOQ_DB_USER" -e JOOQ_DB_PASSWORD="$JOOQ_DB_PASSWORD" \
  maven:3.9.11-eclipse-temurin-21 mvn -Pjooq-codegen generate-sources

echo "jOOQ sources generated under apps/game-server/target/generated-sources/jooq"
