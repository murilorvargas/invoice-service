#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "==> Stopping and removing containers, volumes, networks, and built images..."
docker compose down -v --remove-orphans --rmi local

echo "==> Done."
