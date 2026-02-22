#!/usr/bin/env bash
# Updates TODO.md by cross-referencing recent git commits with the current sprint doc.
# Usage: ./scripts/update-todos.sh

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

claude -p "Read docs/TODO.md and the current sprint doc under docs/plan/sprints/. \
Check git log for recent commits. \
Update docs/TODO.md marking any completed tasks based on what was actually committed. \
Do NOT commit — only update the file." \
  --allowedTools "Read,Edit,Bash,Glob,Grep"
