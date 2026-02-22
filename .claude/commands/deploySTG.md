Deploy to staging by merging the sprint PR to `main`, which triggers CI to deploy automatically.

## Pre-flight checks

1. Confirm the current branch is NOT `main` (staging deploys from sprint branches only).
2. **Run the full Pre-Deployment Validation checklist** from CLAUDE.md (section "Pre-Deployment Validation"). Execute all 10 checks and produce the Deployment Readiness Report.
3. If any **BLOCKER** is found, stop and report the findings. Do NOT deploy until all blockers are resolved.
4. If only **WARNINGs** exist, report them and proceed with deployment.

## Merge PR to main

5. Find the open PR for the current sprint branch:
   ```bash
   gh pr list --head $(git branch --show-current) --state open --json number,title
   ```
   - If no PR found → stop and tell the user to run `/finishSprint` first to create the PR.
6. Merge the PR to `main`:
   ```bash
   gh pr merge <number> --merge
   ```

## Wait for CI deploy

7. Wait for the CI workflow triggered by the merge to `main`:
   ```bash
   gh run list --branch main --limit 1 --json status,conclusion,databaseId,name
   ```
   Poll every 30 seconds until the run completes:
   ```bash
   gh run watch <run-id>
   ```
8. Verify the CI `deploy-staging` jobs passed (backend + frontend).
   - If CI failed → stop and report the failure. Do NOT proceed.

## Smoke test

9. Smoke test — check backend health:
   ```bash
   curl -s -o /dev/null -w "%{http_code}" <STG_BACKEND_URL>/actuator/health
   ```
   - If 200: deploy succeeded.
   - If not 200: warn the user and suggest checking Railway logs.

## Update version tracking

10. Checkout `main` and pull the merged changes:
    ```bash
    git checkout main
    git pull origin main
    ```
11. Update the **Environment Versions** table in `docs/plan/releases.md`:
    - Set **STG** row: version = current tag or `(untagged)`, branch = `main`, deployed at = today's date.
12. Commit and push:
    ```bash
    git add docs/plan/releases.md
    git commit -m "docs: update STG environment version"
    git push origin main
    ```
13. Switch back to the sprint branch:
    ```bash
    git checkout <sprint-branch>
    ```

## Report

Print a summary:
- PR merged: `#<number> — <title>`
- CI run: link or status
- STG URLs (backend + frontend)
- Health check result

## Monitoring the deploy

Instruct the user on how to follow the deploy progress:

**Option A — Railway CLI (terminal):**
```bash
railway status --environment staging
railway logs --service backend --environment staging
railway logs --service frontend --environment staging
```

**Option B — Railway dashboard (browser):**
1. Go to [railway.app](https://railway.app) → open the studentsbff project
2. Switch to the **staging** environment
3. Click the **backend** or **frontend** service
4. Open the **Deployments** tab — the latest deploy shows its status

**Option C — GitHub Actions:**
1. Go to the repository on GitHub → **Actions** tab
2. Find the workflow run triggered by the merge to `main`
3. Check that all steps pass (build, test, deploy-staging)

**What to look for:**
- Status **Active** = deploy succeeded, service is healthy
- Status **Failed** = deploy failed; check logs for the error
- Railway runs the healthcheck (`/actuator/health` for backend, `/` for frontend) before marking the deploy Active
