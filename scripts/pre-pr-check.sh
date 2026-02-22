#!/usr/bin/env bash
# Runs backend tests and frontend build. If anything fails, attempts to fix it.
# Usage: ./scripts/pre-pr-check.sh

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

claude -p "Run the following checks in order:
1. cd backend && ./mvnw verify
2. cd frontend && npm run build
If anything fails, fix the issue and re-run the failing check to confirm it passes. \
Explain what was wrong and what you fixed." \
  --allowedTools "Read,Edit,Bash,Glob,Grep"
