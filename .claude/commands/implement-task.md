Implement one or more tasks from the current sprint using the test-gated Implementation Loop.

**Input:** $ARGUMENTS

## Step 0: Resolve what to implement

The input can be any of the following (or a mix):

| Input format | Example | Meaning |
|-------------|---------|---------|
| Single task ID | `P5.1` | One specific task |
| Multiple task IDs | `P5.1 P5.2 P5.3` | Those specific tasks, in the given order |
| Phase ID | `P5` | All tasks in that phase |
| Free-text instruction | `"implement all tasks from phases P1, P2 and P3"` | Parse the intent and resolve to concrete task IDs |

### Resolution rules

1. **Find the current sprint** — from the branch name or most recent sprint folder in `docs/plan/sprints/`
2. **Find the todo files** — list all `todo_sprintNNN_*.md` files in the sprint folder
3. **Expand phase IDs** — read the todo file and extract all task IDs
4. **Respect document order** — tasks within a phase are executed in document order
5. **Skip completed tasks** — if a task is already marked as done, skip it
6. **Build the task list** — present the ordered list and ask for confirmation before starting

---

## For each task, execute Steps 1-7:

### Step 1: Identify the task
Read the task description in the todo file and any linked spec sections.

### Step 2: Read & Study Patterns
Find at least one existing example of the same kind of artifact. Summarize conventions.

### Step 3: Implement
Make changes across all necessary files following `/backend-dev` and `/frontend-dev` conventions.

### Step 4: Test
```bash
cd backend && ./mvnw verify
cd ../frontend && npm run build && npm test
```
Repeat until green. **Never proceed with red tests.**

### Step 5: Fix (if needed)
If tests fail after 2 fix attempts, **STOP and ask the user**.

### Step 6: Update documentation
- Update `docs/TODO.md` — mark the task as complete
- Update the sprint todo file
- Update `docs/SPECIFICATION.md` if behavior changed

### Step 7: Commit
Stage ALL changed files. Commit with conventional format including task ID:
```
feat(sprintNNN): implement <task-id> — <short description>
```

### Then move to the next task.

## Stop conditions
**Stop and ask the user** if you encounter:
- A design ambiguity with multiple valid approaches
- A failing test you cannot resolve after 2 attempts
- A task that requires changing the data model or public API contract beyond what the spec defines
