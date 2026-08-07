#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/src/docker-compose.yaml"

echo "Resetting local PostgreSQL database..."
docker compose -f "$COMPOSE_FILE" down -v
docker compose -f "$COMPOSE_FILE" up -d db

echo "Database reset complete."
echo "You can now start the Spring Boot application."
