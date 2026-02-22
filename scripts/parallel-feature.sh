#!/usr/bin/env bash
# Parallel full-stack feature implementation.
# Launches Claude to implement backend and frontend in parallel sub-agents.
#
# Usage: ./scripts/parallel-feature.sh "feature description" "API contract"

set -euo pipefail

if [ $# -lt 2 ]; then
  echo "Usage: $0 <feature-description> <api-contract>"
  exit 1
fi

FEATURE="$1"
CONTRACT="$2"

cd "$(git rev-parse --show-toplevel)"

claude -p "Implement this feature using parallel sub-agents as described in CLAUDE.md.

## Feature
${FEATURE}

## API Contract
${CONTRACT}

## Instructions
1. Launch TWO parallel Task agents:
   - **Backend agent:** Read .claude/commands/backend-dev.md. Implement controller, service, repository, DTOs with MapStruct, and tests. Run 'cd backend && ./gradlew check' until green.
   - **Frontend agent:** Read .claude/commands/frontend-dev.md. Implement types, service function, React page/component, and tests. Run 'cd frontend && npm run build && npm test' until green.
2. After both complete, review for contract mismatches.
3. Run full test suite: cd backend && ./gradlew check && cd ../frontend && npm run build && npm test
4. Stage ALL changed files. Do NOT commit." \
  --allowedTools "Read,Edit,Write,Bash,Glob,Grep,Task"
