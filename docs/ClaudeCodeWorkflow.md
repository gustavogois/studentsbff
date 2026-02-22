# Claude Code Workflow — StudentsBFF

End-to-end development lifecycle, from sprint planning to production with observability.
Every phase lists the **commands/skills/scripts** to use, **when** to use them, and **what to expect**.

---

## Overview — The Sprint Lifecycle

```
 Refinement ──► Plan Sprint ──► Implement Tasks ──► Finish Sprint ──► Deploy STG ──► Deploy PRD ──► Observe
      │              │                │                    │               │              │             │
 /refinement    /plan-sprint     /implement-task      /finishSprint    /deploySTG    /deployPRD     Railway
               -tasks           run-sprint.sh        sync-sprint-     pre-deploy-   pre-deploy-    logs
                                parallel-feature.sh  docs.sh          validate.sh   validate.sh
```

Each phase is **one focused session**. Complete one phase fully, commit, then start a new session for the next.

---

## Phase 0: Session Start — Orientation

Every time you open Claude Code on this project, the following happens automatically:

| What | How | You see |
|------|-----|---------|
| `CLAUDE.md` loaded | Injected into context | All project rules active |
| `MEMORY.md` loaded | Injected into context | Cross-session learnings |
| PostToolUse hook | `scripts/format-hook.sh` | Java auto-formatted + compiled after every Edit/Write |
| PreToolUse hook | Git state printed | Staged/unstaged files shown before every Bash call |

**No action needed** — this is automatic.

---

## Phase 1: Refinement (Product Owner)

**Goal:** Decide what goes into the next sprint. Review the backlog, gather priorities, add/reprioritize items, and select the sprint scope.

### Interactive

```
/refinement
```

**What it does:**
1. Reads the latest sprint doc, backlog (`SPECIFICATION.md`), tech debt, and release history
2. Asks structured PO-style questions: retrospective, priorities, new items, reprioritization, constraints, sprint goal
3. Updates the backlog table and release roadmap in `SPECIFICATION.md`
4. Creates the sprint planning doc: `docs/plan/sprints/sprintNNN/sprintNNNplanning.md`
5. Presents a summary of all decisions made

**Prerequisites:**
- Previous sprint completed (or this is the first sprint)

**Output:** Updated backlog, new sprint planning doc with selected items, decision summary.

**What it does NOT do:**
- Break items into implementation tasks (that's `/plan-sprint-tasks`)
- Write any code
- Commit (waits for user instruction)

**When done:** Review the artifacts, then commit and move to Phase 2:
```
commit  (or /git-conventions for format reference)
```

---

## Phase 2: Sprint Planning

**Goal:** Break down backlog items into concrete, test-first tasks with execution order and commit plan.

### Interactive (recommended)

```
/plan-sprint-tasks
```

**What it does:**
1. Reads the sprint planning doc (`docs/plan/sprints/sprintNNN/sprintNNNplanning.md`)
2. Reads `SPECIFICATION.md` and `DATA_MODEL.md` for context
3. For each backlog item: breaks down into tasks, plans tests first (TDD)
4. Creates per-phase TODO files in `docs/plan/sprints/sprintNNN/`
5. Presents execution order and commit plan for approval

**Prerequisites:**
- Sprint planning doc exists with backlog items selected
- Branch created: `git checkout -b sprintNNN`

**Output:** Task files with tests planned first, execution order, commit plan, manual testing steps.

**When done:** Review the plan, ask for adjustments, then commit:
```
commit  (or /git-conventions for format reference)
```

---

## Phase 3: Implementation

**Goal:** Implement each task following the test-gated loop: read → implement → test → fix → doc → commit.

### Rule: Parallelize backend + frontend by default

When a task touches **both backend and frontend**, always use parallel sub-agents. This is the default, not an option.

```bash
./scripts/parallel-feature.sh \
  "Add POST /api/subjects endpoint" \
  "POST /api/subjects — Request: { name: string } — Response: SubjectResponse"
```

**How it works:** Defines the API contract first, then launches two sub-agents in parallel (one for Spring Boot, one for React), each in an isolated worktree. Both follow the test-gated loop independently.

**Only go serial when:**
- The frontend needs to discover the backend response shape (no contract defined yet)
- The task is a bug fix where the root cause is unclear and might span both stacks

### `/implement-task` — flexible input

`/implement-task` accepts tasks, phases, or free-text instructions. It resolves any input into an ordered list of concrete task IDs, confirms with you, then iterates through the test-gated loop for each one.

| Input | Example | What it does |
|-------|---------|-------------|
| Single task | `/implement-task P5.1` | Implements one task |
| Multiple tasks | `/implement-task P5.1 P5.2 P5.3` | Implements those tasks in the given order |
| Phase | `/implement-task P5` | Expands to all tasks in that phase, in document order |
| Multiple phases | `/implement-task P1 P2 P3` | All tasks from P1, then P2, then P3 |
| Mix | `/implement-task P1 P2.3 P3` | All of P1, then just P2.3, then all of P3 |
| Free text | `/implement-task implement phases P1 and P2 respecting the document order` | Parses the intent and resolves to task IDs |

**How it resolves:**
1. Finds the current sprint from branch name or latest sprint folder
2. For each phase ID, reads the matching `todo_sprintNNN_pN_*.md` and extracts all task IDs
3. Respects the "Execution Order" section (or natural `### PX.N` order) within each phase
4. Skips tasks already marked as done (`✅` / `[x]`)
5. Presents the final ordered list for confirmation before starting

**For each task:**
1. Reads the task spec and studies existing patterns
2. Implements (parallel backend + frontend when applicable)
3. Runs full test suite — fixes failures (up to 2 attempts, then asks you)
4. Updates docs (TODO.md, sprint todo file, SPECIFICATION.md)
5. Commits with conventional format

**Stop conditions** (Claude will ask you):
- Design ambiguity with multiple valid approaches
- Failing test after 2 fix attempts
- Data model or API contract changes beyond spec

### Headless execution

#### Single task or phase

```bash
./scripts/implement-task.sh "P5.1"    # one task
./scripts/implement-task.sh "P5"      # entire phase
```

Runs autonomously via `claude -p`. Good for background execution.

#### Full sprint

```bash
./scripts/run-sprint.sh
```

Picks up ALL incomplete tasks from TODO.md and implements them one by one in priority order. Stops on design ambiguities.

### Convention references (use during implementation)

| Need | Command |
|------|---------|
| Backend conventions (packages, DTOs, Lombok, logging) | `/backend-dev` |
| Frontend conventions (components, services, Tailwind) | `/frontend-dev` |
| Test conventions for backend | `/test-backend` |
| Git commit format | `/git-conventions` |

### Automated quality gates during implementation

These run **automatically** via hooks — no manual action needed:

| Hook | Trigger | What it does |
|------|---------|--------------|
| **Auto-format** (PostToolUse) | Every `Edit` or `Write` on `.java` | Runs `spotless:apply` then `mvnw compile` (compile errors surface immediately) |
| **Auto-format** (PostToolUse) | Every `Edit` or `Write` on `.ts/.tsx/.css/.json` | Runs `npx prettier --write` |
| **Git state** (PreToolUse) | Every `Bash` call | Prints staged + unstaged files so you see working tree state |

### Pre-commit gate (runs on `git commit`)

The `.husky/pre-commit` hook runs:
1. `npx lint-staged` in `frontend/` (linting + formatting)
2. `spotlessCheck` in `backend/` (only when `.java` files are staged)

If either fails, the commit is blocked — fix and retry.

### Pre-PR quality check

```bash
./scripts/pre-pr-check.sh
```

Runs `./mvnw verify` + `npm run build`. If anything fails, attempts to fix it automatically. Run this as the final gate after all tasks are implemented and before moving to Phase 4 (Finish Sprint).

---

## Phase 4: Finish Sprint

**Goal:** Mark tasks done, review docs for drift, commit, push, open/update PR.

### Interactive

```
/finishSprint
```

**What it does:**
1. Identifies the current sprint from the branch name
2. Asks you what was NOT completed (keeps those open)
3. Marks completed tasks in TODO files and sprint doc
4. Reviews `SPECIFICATION.md`, `DATA_MODEL.md`, `CHANGELOG.md` for drift
5. Commits documentation updates
6. Pushes branch and creates/updates the PR

### Supporting scripts

| Script | When to use |
|--------|-------------|
| `./scripts/sync-sprint-docs.sh` | Quick sync of TODO.md, sprint doc, and SPECIFICATION.md without full sprint close |
| `./scripts/update-todos.sh` | Sync TODO.md with recent git commits (lightweight) |

---

## Phase 5: Deploy to Staging

**Goal:** Merge the sprint PR to `main` so CI deploys to STG, then validate.

### Interactive

```
/deploySTG
```

**What it does:**
1. **Pre-flight validation** — runs ALL 10 checks from the Pre-Deployment Validation checklist:
   - Tests green
   - CORS matches frontend URL
   - OAuth2 redirect URI uses `{baseUrl}`
   - Frontend `VITE_API_URL` matches backend domain
   - No hardcoded `localhost`
   - Node/Java version consistency
   - Required env vars present
   - Forward headers configured
   - Health check endpoints configured
2. If **BLOCKER** found → stops, reports findings, does NOT deploy
3. If only **WARNINGs** → reports and proceeds
4. Merges the sprint PR to `main` (via `gh pr merge`)
5. Waits for CI to deploy to STG (`deploy-staging` job)
6. Smoke tests the backend health endpoint
7. Updates `docs/plan/releases.md` with STG version

**Prerequisites:**
- PR must exist (created by `/finishSprint`)
- All CI checks on the PR must pass

### Headless validation only (no deploy)

```bash
./scripts/pre-deploy-validate.sh stg
```

Runs the 10 checks and produces a Deployment Readiness Report. Does NOT deploy.

### Monitor the deploy

```bash
# CLI
railway status --environment staging
railway logs --service backend --environment staging
railway logs --service frontend --environment staging
```

Or use the Railway dashboard: railway.app → studentsbff → staging environment → Deployments tab.
Or: GitHub → Actions tab → check the workflow run triggered by the merge to `main`.

---

## Phase 6: Deploy to Production

**Goal:** Release the sprint — CHANGELOG, tag, explicit `railway up` to PRD, GitHub Release.

> **Important:** Production never auto-deploys. Only `/deployPRD` deploys to production via explicit `railway up`.

### Interactive

```
/deployPRD
```

**What it does:**
1. **Pre-flight validation** — same 10 checks as STG
2. Confirms STG was deployed and tested (sprint PR already merged to `main` by `/deploySTG`)
3. Determines version (MAJOR/MINOR/PATCH) and asks you to confirm
4. Updates CHANGELOG.md (moves `[Unreleased]` → `[vX.Y.Z]`)
5. Creates annotated git tag on `main` (`vX.Y.Z`)
6. Deploys to production via explicit `railway up` (backend + frontend)
7. Smoke tests production endpoints
8. Creates GitHub Release with auto-generated notes
9. Updates `docs/plan/releases.md` with PRD version

### Headless validation only (no deploy)

```bash
./scripts/pre-deploy-validate.sh prd
```

### Monitor the deploy

```bash
# CLI
railway status --environment production
railway logs --service backend --environment production
railway logs --service frontend --environment production
```

Or: Railway dashboard → production environment → Deployments tab.

---

## Phase 7: Observability

**Goal:** Monitor errors, performance, and release health after deploy.

### Railway logs

```bash
# Real-time logs
railway logs --service backend --environment production
railway logs --service frontend --environment production

# Check deployment status
railway status --environment production
```

### Health checks

Railway automatically runs health checks on every deploy:
- Backend: `GET /actuator/health`
- Frontend: `GET /`

If the health check fails, Railway rolls back to the previous deployment.

---

## CI/CD Pipeline

These run automatically on push/PR — no manual action needed.

### Backend CI (`.github/workflows/backend-ci.yml`)

Triggers on push/PR to `main` affecting `backend/**`:
1. Checkout + Set up Java 21
2. **Run tests** — `./mvnw verify`
3. Upload test report
4. (On push to main) **Deploy to staging** via Railway CLI

> CI only deploys to **staging**, never to production. Production is deployed via `/deployPRD`.

### Frontend CI (`.github/workflows/frontend-ci.yml`)

Triggers on push/PR to `main` affecting `frontend/**`:
1. Checkout + Set up Node 22
2. Install dependencies
3. Lint + test + build

---

## Quick Reference — All Commands

### Skills (interactive, inside Claude Code)

| Skill | Phase | Purpose |
|-------|-------|---------|
| `/refinement` | Refinement | PO-led backlog refinement and sprint scoping |
| `/plan-sprint-tasks` | Planning | Break backlog into TDD tasks |
| `/implement-task <id>` | Implementation | Implement a single task (test-gated loop) |
| `/backend-dev` | Implementation | Backend code conventions reference |
| `/frontend-dev` | Implementation | Frontend code conventions reference |
| `/test-backend` | Implementation | Backend test conventions reference |
| `/git-conventions` | Any | Commit message and PR format reference |
| `/finishSprint` | Sprint close | Mark done, sync docs, push, open PR |
| `/deploySTG` | Staging deploy | Validate + merge PR + CI deploys to staging |
| `/deployPRD` | Production deploy | Full release flow to production |

### Scripts (headless, run from terminal)

| Script | Phase | Purpose |
|--------|-------|---------|
| `./scripts/implement-task.sh <id>` | Implementation | Single task, headless |
| `./scripts/run-sprint.sh` | Implementation | All tasks, headless |
| `./scripts/parallel-feature.sh <desc> <contract>` | Implementation | Parallel backend + frontend |
| `./scripts/pre-pr-check.sh` | Sprint close | Run tests + build, auto-fix failures |
| `./scripts/sync-sprint-docs.sh` | Sprint close | Sync TODO, sprint doc, SPECIFICATION |
| `./scripts/update-todos.sh` | Sprint close | Lightweight TODO sync from git log |
| `./scripts/pre-deploy-validate.sh [stg\|prd]` | Deploy | Run 10 validation checks, report only |
| `./scripts/format-hook.sh` | Automatic | PostToolUse hook (auto-format + compile) |

### Shell commands (manual)

| Command | When |
|---------|------|
| `docker compose up -d` | Start local PostgreSQL |
| `cd backend && ./mvnw spring-boot:run` | Start backend locally |
| `cd frontend && npm run dev` | Start frontend locally |
| `cd backend && ./mvnw verify` | Run backend tests |
| `cd frontend && npm run build && npm test` | Build + test frontend |

---

## Decision Tree — Which Command Do I Use?

```
What do I need to do?
│
├─ Decide what goes into the next sprint?
│  └─ /refinement
│
├─ Break sprint items into TDD tasks?
│  └─ /plan-sprint-tasks
│
├─ Implement code?
│  ├─ Task touches backend + frontend?
│  │  └─ Parallel by default → ./scripts/parallel-feature.sh <desc> <contract>
│  ├─ Tasks or phases, interactively?
│  │  └─ /implement-task <tasks, phases, or free text>
│  ├─ Tasks or phases, headless?
│  │  └─ ./scripts/implement-task.sh <tasks or phases>
│  └─ All sprint tasks, headless?
│     └─ ./scripts/run-sprint.sh
│
├─ Close the sprint?
│  ├─ Full close (mark done, sync docs, PR)?
│  │  └─ /finishSprint
│  ├─ Just sync docs?
│  │  └─ ./scripts/sync-sprint-docs.sh
│  └─ Just update TODOs from git log?
│     └─ ./scripts/update-todos.sh
│
├─ Deploy?
│  ├─ To staging?
│  │  ├─ Validate only? → ./scripts/pre-deploy-validate.sh stg
│  │  └─ Validate + deploy? → /deploySTG
│  └─ To production?
│     ├─ Validate only? → ./scripts/pre-deploy-validate.sh prd
│     └─ Full release? → /deployPRD
│
├─ Check quality before PR?
│  └─ ./scripts/pre-pr-check.sh
│
└─ Monitor after deploy?
   ├─ Logs → railway logs --service <svc> --environment <env>
   └─ Health → /actuator/health (backend) or / (frontend)
```

---

## Typical Sprint Flow — Step by Step

```
Session 1 — Refinement
  1. /refinement
  2. Answer PO questions (retro, priorities, new items, sprint goal)
  3. Review updated backlog + sprint planning doc → commit

Session 2 — Sprint Planning
  4. git checkout -b sprintNNN
  5. /plan-sprint-tasks
  6. Review TDD task plan → adjust → commit

Session 3 — Implementation (repeat per task or batch)
  7. /implement-task P5.1
  8. /implement-task P5.2
  9. ... (or ./scripts/run-sprint.sh for all at once)

Session 4 — Sprint Close
  10. /finishSprint
  11. Review PR on GitHub

Session 5 — Staging Deploy
  12. /deploySTG (merges sprint PR → CI deploys to STG)
  13. Manual testing on STG URLs
  14. Fix issues if any → push to main → CI re-deploys STG

Session 6 — Production Release
  15. /deployPRD (CHANGELOG + tag + explicit railway up to PRD)
  16. Monitor: Railway logs
  17. Celebrate
```
