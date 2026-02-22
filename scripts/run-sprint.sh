#!/usr/bin/env bash
# Autonomous sprint execution with test gates.
# Picks up all incomplete tasks from TODO.md, implements them one-by-one,
# runs the full test suite after each, and only commits when green.
#
# Usage: ./scripts/run-sprint.sh
#
# Output: sprint execution log written to sprint-execution-<timestamp>.log
# The log includes all decisions made autonomously.

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

LOGFILE="sprint-execution-$(date +%Y%m%d-%H%M%S).log"

echo "=== Sprint execution started at $(date) ===" | tee "$LOGFILE"
echo "Log file: $LOGFILE"
echo ""

claude -p "You are running in FULLY AUTONOMOUS mode. The user is away and cannot answer questions.

## Your mission
Read docs/TODO.md and implement ALL incomplete tasks for the current sprint, in order (P1.1 → P1.2 → ... → P6.4).

## For each task:
1) Read the task spec in docs/plan/sprints/sprint001/todo_sprint001_*.md
2) Read /backend-dev and /frontend-dev conventions from .claude/commands/
3) Write tests FIRST (TDD), then implement until tests pass
4) Run the full test suite: cd backend && ./mvnw verify (if backend exists), then cd frontend && npm run build && npm test (if frontend exists)
5) If tests fail, analyze and fix. Up to 3 attempts per failure before moving on
6) Stage ALL changed files and commit with: feat(sprint001): P{X}.{Y} — {description}
7) Update docs/TODO.md marking the task as done ([x])
8) Push to origin after each commit

## Autonomous decision-making rules
- NEVER stop to ask questions. YOU decide and move on.
- When you face a design ambiguity with multiple valid approaches, pick the SIMPLEST one.
- Log every significant decision by writing it to docs/plan/sprints/sprint001/sprint001-decisions.md (append, do not overwrite).
  Format: '### P{X}.{Y} — {decision title}\n{what you decided and why}\n'
- If a test fails after 3 fix attempts, log it as a known issue in the decisions file and move to the next task.
- If a task is blocked by a missing dependency from a previous task that failed, skip it and log the reason.

## Important conventions
- Backend package: com.studentsbff, packages: model/, controller/, service/, repository/, dto/, config/, mapper/
- JPA entities use Lombok (@Getter, @Setter, @NoArgsConstructor). Response DTOs are Java Records. Request DTOs use Lombok.
- MapStruct for entity→DTO mapping. Services return entities, controllers map to DTOs.
- Frontend: React 19 + TypeScript + Vite + Tailwind CSS. Vitest for tests. Services layer for API calls.
- All code, comments, and commits in English.
- UUID primary keys everywhere.
- Tests: backend uses H2 + create-drop (Flyway disabled in test profile unless testing migrations). Frontend uses Vitest + React Testing Library.

## When done
After all tasks are processed, write a final summary to the decisions file with:
- Tasks completed successfully
- Tasks that failed or were skipped
- Total commits made
- Any issues that need human attention

Continue until ALL sprint tasks are processed." \
  --allowedTools "Read,Edit,Write,Bash,Glob,Grep" 2>&1 | tee -a "$LOGFILE"

echo "" | tee -a "$LOGFILE"
echo "=== Sprint execution finished at $(date) ===" | tee -a "$LOGFILE"
echo "Full log: $LOGFILE"
echo "Decisions: docs/plan/sprints/sprint001/sprint001-decisions.md"
