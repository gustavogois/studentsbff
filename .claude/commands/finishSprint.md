Close out the current sprint: mark tasks as done, review project docs for drift, commit, push, and open/update the PR.

## Step 1: Identify the current sprint
1. Check the current git branch name for the sprint number
2. Locate the sprint files in `docs/plan/sprints/sprintNNN/`

## Step 2: Determine what was completed
3. Ask the user: **"Is there anything that was NOT implemented, or any task that should remain open?"**
   - Wait for the answer before proceeding

## Step 3: Update task list
4. Mark completed tasks as `- [x]`, keep incomplete ones as `- [ ]`

## Step 4: Update sprint doc
5. Fill in Status column for completed tasks
6. Update sprint Status field to `Done`

## Step 5: Review project documentation for drift
8. **`docs/SPECIFICATION.md`** — move completed backlog items to Implemented table
9. **`docs/DATA_MODEL.md`** — check for undocumented entities/columns
10. **`CHANGELOG.md`** — verify `[Unreleased]` section has all changes

## Step 6: Commit
```bash
git add docs/ CHANGELOG.md
git commit -m "docs(sprint NNN): mark sprint complete and update project docs"
```

## Step 7: Push and open/update PR
14. Push the sprint branch
15. Check if PR exists, create or update it

## Report
- Sprint closed: `sprintNNN`
- Tasks marked done: N / N total
- Tasks left open (if any)
- Docs updated: list each file
- PR: link
- Remind user: deploy to staging before production
