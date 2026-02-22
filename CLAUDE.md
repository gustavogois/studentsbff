# StudentsBFF

## Language
All code, comments, documentation, commit messages, and generated files must be written in **English**.

StudentsBFF (Best Friend Forever) — study companion platform for middle school students.

## Tech Stack
- **Backend:** Spring Boot 3 + Java 21 + Gradle (Kotlin DSL) + JPA/Hibernate + MapStruct + Jackson + PostgreSQL
- **Frontend:** React 19 + TypeScript + Vite + Tailwind CSS + PWA
- **Database:** PostgreSQL 16 (Docker Compose for local dev)
- **AI (initial):** OpenAI API (GPT-4o)
- **AI (future):** Abstraction for multiple providers (Anthropic Claude, etc.)
- Always run existing tests after backend changes (`cd backend && ./gradlew check`) and verify frontend builds (`cd frontend && npm run build`)

## Project Structure
- `backend/` — Spring Boot REST API
- `frontend/` — React + Vite SPA (PWA)

## Documentation
Always read `README.md` first when you need to understand the project structure or find documentation files. Key docs:
- `context.md` — Product context, user research, and stakeholder decisions
- `PLAN.md` — Implementation plan with architecture, entities, and endpoints
- `docs/SPECIFICATION.md` — Product spec, functional/non-functional requirements, and **backlog**
- `docs/DATA_MODEL.md` — Entity definitions, relationships, Flyway migrations
- `docs/DEPLOYMENT.md` — Production deployment guide (Railway)
- `docs/plan/releases.md` — Release history and version log
- `docs/plan/sprints/` — Per-sprint planning and task logs

## Infrastructure as Code (IaC)
**Always prefer code over manual configuration.** All infrastructure config must live in the repository whenever possible. Only secrets and environment-specific URLs go in the Railway dashboard.

> For backend development conventions (packages, Lombok, DTOs, logging, Javadoc),
> invoke **/backend-dev**.

> For frontend development conventions (components, services, Tailwind, TypeScript, testing),
> invoke **/frontend-dev**.

## Hooks & Tooling

### Auto-format hook (PostToolUse)
After every `Edit` or `Write` tool call, `scripts/format-hook.sh` runs automatically:
- **Java files:** `spotlessApply` (auto-fixes style) → `gradlew compileJava` (surfaces real compile errors)
- **TS/TSX/CSS/JSON files:** `npx prettier --write` (auto-fixes formatting)

The format step uses `|| true` (transient issues are auto-fixed), but the compile step does **not** — compile errors are real and must be visible.

### Git state hook (PreToolUse)
Before every `Bash` tool call, staged and unstaged files are printed so you can see the working tree state.

## Database Migrations
- Flyway migrations are **forward-only**. Never manipulate `flyway_schema_history` or run manual DDL to undo a migration. To revert a change, create a new migration.
- Always write **backwards-compatible migrations** (add nullable columns, add new tables). Destructive changes (drop column, rename, change type) should only happen in a later migration after confirming the new code is stable in production.

## Development
- Start DB: `docker compose up -d`
- Start backend: `cd backend && ./gradlew bootRun`
- Start frontend: `cd frontend && npm run dev`

## Git Workflow & Conventions
- **Never commit without explicit user instruction.** Only run `git commit` when the user explicitly asks (e.g. "commit", "faca commit", "/git-conventions").
- Always use **conventional commits** (e.g., `feat:`, `fix:`, `docs:`, `chore:`)
- After making changes, stage ALL modified files before committing — double-check with `git status` to avoid missed files
- When committing, always include related documentation updates (SPECIFICATION.md, TODO.md, sprint docs) in the same commit or a paired `docs:` commit

## Task Execution Rules
- When asked to implement a task from a TODO or sprint doc, ONLY do what is specified — do not implement code when asked to plan, and do not plan when asked to implement
- Always read the relevant TODO/sprint document FIRST before starting any implementation
- After completing a task, update the corresponding TODO.md and sprint doc to mark it as done
- **Keep sessions focused.** A single session should cover one phase only (e.g., "implement tasks 4.7-4.8, test, commit"). Do NOT mix planning + implementing + deploying in the same session

## Documentation Updates
- When completing sprint tasks, always update: TODO.md, the relevant sprint doc, and SPECIFICATION.md if behavior changed
- Never skip documentation — if the user asks to implement + commit, documentation updates are implicitly required

## Sub-Agent Guidelines
- When delegating to sub-agents, verify their output for: correct import paths, correct function/enum names matching existing codebase, and proper default exports
- Always review and test sub-agent generated code before committing

## Tool Integration
- **Always check for existing automation** (skills, scripts, MCP servers) before starting manual work
- Available skills: `/refinement`, `/git-conventions`, `/plan-sprint-tasks`, `/implement-task`, `/backend-dev`, `/frontend-dev`, `/test-backend`, `/finishSprint`
- Available headless scripts: `scripts/run-sprint.sh`, `scripts/implement-task.sh`, `scripts/parallel-feature.sh`, `scripts/pre-pr-check.sh`, `scripts/format-hook.sh`

## Parallel Agents for Full-Stack Features
When a task involves **both backend and frontend** changes with a clear API contract, use parallel sub-agents to implement them simultaneously.

### When to parallelize
- New CRUD endpoint + corresponding UI page/component
- New API field that requires backend DTO changes + frontend type/display changes
- Any feature where backend and frontend work is independent once the API contract is defined

### When NOT to parallelize
- The frontend depends on discovering the backend response shape (no spec/contract yet)
- The task is backend-only or frontend-only
- The task is a bug fix where the root cause is unclear

### How to parallelize
1. **Define the API contract first** — URL, method, request body, response shape
2. **Launch two Task agents in parallel** (backend + frontend)
3. **Review & integrate** — check for contract mismatches
4. **Test** — Run full test suite: `cd backend && ./gradlew check && cd ../frontend && npm run build && npm test`

## Testing Workflow
- **Run tests proactively.** After writing or changing code, run the relevant tests immediately to verify correctness.
- Follow **TDD**: write tests first, watch them fail, then implement until they pass.
- Backend: `cd backend && ./gradlew check`
- Frontend: `cd frontend && npm test`

## Implementation Loop (Test-Gated)
When implementing tasks, follow this strict loop for **each task**:
1. **Read & Study Patterns** — Read the task spec AND at least one existing example of the same kind
2. **Implement** — Make changes across all necessary files
3. **Test** — Run `cd backend && ./gradlew check` and `cd frontend && npm run build && npm test`
4. **Fix** — If any test/build fails, analyze and fix. Repeat until green. **Never commit red code.**
5. **Doc** — Update TODO.md, sprint doc, and SPECIFICATION.md if behavior changed
6. **Commit** — Stage ALL changed files, commit with conventional format
7. **Next** — Move to the next task

**Stop and ask the user** if you encounter: a design ambiguity with multiple valid approaches, a failing test you cannot resolve after 2 attempts, or a task that requires changing the data model or public API contract.
