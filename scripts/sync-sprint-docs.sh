#!/usr/bin/env bash
# Syncs sprint doc, TODO.md, and SPECIFICATION.md after implementation work.
# Usage: ./scripts/sync-sprint-docs.sh

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

claude -p "Read docs/TODO.md, the current sprint doc under docs/plan/sprints/, and docs/SPECIFICATION.md. \
Cross-reference with git log to find recently completed work. \
Update all three documents to reflect current state: \
- Mark completed tasks in TODO.md \
- Add completion notes in the sprint doc \
- Update SPECIFICATION.md if any behavior changed. \
Do NOT commit — only update the files." \
  --allowedTools "Read,Edit,Bash,Glob,Grep"
