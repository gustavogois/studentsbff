#!/usr/bin/env bash
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel)
FILE=$(jq -r '.tool_input.file_path' < /dev/stdin)

if [[ "$FILE" == *.java ]]; then
  cd "$ROOT/backend"
  ./mvnw spotless:apply -q 2>&1 | tail -3 || true
  ./mvnw compile -q 2>&1 | tail -5
elif [[ "$FILE" == *.ts || "$FILE" == *.tsx || "$FILE" == *.css || "$FILE" == *.json ]]; then
  cd "$ROOT/frontend"
  npx prettier --write "$FILE" 2>&1 | tail -3 || true
fi
