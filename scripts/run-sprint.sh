#!/usr/bin/env bash
# Autonomous sprint execution with test gates.
# Picks up all incomplete tasks from TODO.md, implements them one-by-one,
# runs the full test suite after each, and only commits when green.
#
# Usage: ./scripts/run-sprint.sh

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

claude -p "Read docs/TODO.md and identify all incomplete tasks for the current sprint. \
For each task in priority order, follow the Implementation Loop from CLAUDE.md: \
1) Read the relevant spec docs and existing code \
2) Implement the changes across all necessary files \
3) Run the full test suite: cd backend && ./gradlew check, then cd frontend && npm run build && npm test \
4) If any tests fail, analyze the failure, fix the code, and re-run until all tests pass \
5) Stage ALL changed files, create a conventional commit with the task ID \
6) Update docs/TODO.md marking the task complete \
If you encounter a design ambiguity, STOP and ask me before proceeding. \
Continue until all sprint tasks are done or you hit a blocker." \
  --allowedTools "Read,Edit,Write,Bash,Glob,Grep"
