#!/usr/bin/env bash
# Implement a single task using the test-gated Implementation Loop.
#
# Usage: ./scripts/implement-task.sh "P5.1"

set -euo pipefail

if [ -z "${1:-}" ]; then
  echo "Usage: $0 <task-id>"
  echo "Example: $0 P5.1"
  exit 1
fi

TASK_ID="$1"

cd "$(git rev-parse --show-toplevel)"

claude -p "Run /implement-task ${TASK_ID}" \
  --allowedTools "Read,Edit,Write,Bash,Glob,Grep"
