Plan the detailed tasks for the next sprint using Test-Driven Development (TDD).

## Process

### 1. Analyse

- Read the sprint planning doc at `docs/plan/sprints/sprint{NNN}/sprint{NNN}planning.md`
- Read previous sprint files for conventions and patterns
- Read `docs/SPECIFICATION.md` and `docs/DATA_MODEL.md` if needed
- Identify dependencies between backlog items and determine execution order

### 2. Plan tests first (TDD)

For each task, **plan the tests before the implementation**:

#### Backend
- **Unit tests** for services and business logic
- **Integration tests** for controllers (MockMvc) and repositories
- Use `@DataJpaTest` for repository tests, `@WebMvcTest` for controller tests

#### Frontend
- **Unit tests** for utilities and non-React logic
- **Hook tests** for custom hooks with testable logic
- Use **Vitest** as the test runner

### 3. Create sprint files

Create one file per phase/feature in `docs/plan/sprints/sprint{NNN}/`:

**File naming:** `todo_sprint{NNN}_{phase_id}_{short_name}.md`

**Each file must contain:**

```markdown
# {Phase ID} — {Feature Name}: Implementation Plan

**Sprint:** {NNN}
**Status:** Not Started
**Goal:** {One-sentence goal}

---

## Tasks

### {Phase}.1 — {Task name}

**Tests (write first):**
- [ ] `{TestClassName}#{testMethodName}` — {what it validates}

**Implementation:**
- [ ] {Step 1}
- [ ] {Step 2}

**Commit:** `{type}({scope}): {message}`

---

## Execution Order
{Numbered list showing task order considering dependencies}

## Commit Plan
| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `test({scope}): add tests for ...` | tests |
| 2 | `feat({scope}): implement ...` | implementation |

## Manual Testing
Step-by-step instructions for manual validation after implementation.

## Definition of Done
- [ ] All tests pass (`./mvnw verify` / `npm test`)
- [ ] New code has >=80% test coverage
- [ ] No compilation warnings
- [ ] Manual testing steps executed and passing
```

### 4. Present the plan

After creating all files, present a summary:
- Sprint theme and scope
- Execution order across phases
- Total estimated commits
- Any decisions needing user input

## Rules
- All generated files in **English**
- Never commit without explicit user instruction
- Consult `/backend-dev` and `/frontend-dev` for code conventions
