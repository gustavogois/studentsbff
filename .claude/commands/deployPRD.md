Release the current version to production. Executes the full release flow: CHANGELOG + tag → explicit `railway up` to PRD → GitHub Release.

> **Important:** Production never auto-deploys. Only this skill deploys to production via explicit `railway up`.

## Pre-flight checks

1. Confirm the sprint branch has been merged to `main` (check `docs/plan/releases.md` — STG should show deployment from `main`).
2. **Run the full Pre-Deployment Validation checklist** from CLAUDE.md (section "Pre-Deployment Validation"). Execute all 10 checks and produce the Deployment Readiness Report.
3. If any **BLOCKER** is found, stop and report the findings. Do NOT release until all blockers are resolved.
4. Confirm STG was deployed and tested (check `docs/plan/releases.md` Environment Versions table — STG should show deployment from `main` with a recent date).

## Step 1: Determine version

5. Read `docs/plan/releases.md` to find the current PRD version.
6. Read `CHANGELOG.md` to see what's in `[Unreleased]`.
7. Determine the new version based on the changes:
   - New user-facing feature → MINOR bump
   - Bug fix only → PATCH bump
   - Breaking API change → MAJOR bump
8. Ask the user to confirm the version number before proceeding.

## Step 2: Update CHANGELOG

9. Move all items from `[Unreleased]` to a new versioned section `[X.Y.Z] - YYYY-MM-DD`.
10. Leave `[Unreleased]` empty (ready for next sprint).
11. Commit on `main`:
    ```bash
    git checkout main
    git pull origin main
    git add CHANGELOG.md
    git commit -m "chore(release): bump to vX.Y.Z"
    ```

## Step 3: Create git tag

12. Create an annotated tag on `main`:
    ```bash
    git tag -a vX.Y.Z -m "Release vX.Y.Z — <sprint theme>"
    ```
13. Push the tag and the CHANGELOG commit:
    ```bash
    git push origin main
    git push origin vX.Y.Z
    ```

## Step 4: Deploy to production

14. Deploy backend to PRD:
    ```bash
    cd backend && railway up --service backend --environment production --detach
    ```
15. Deploy frontend to PRD:
    ```bash
    cd frontend && railway up --service frontend --environment production --detach
    ```
16. Wait 60 seconds for the deploy to propagate.
17. Smoke test — check backend health:
    ```bash
    curl -s -o /dev/null -w "%{http_code}" <PRD_BACKEND_URL>/actuator/health
    ```
    - If 200: deploy succeeded.
    - If not 200: warn the user and suggest checking Railway logs.

## Step 5: Create GitHub Release

18. Create a GitHub Release from the tag:
    ```bash
    gh release create vX.Y.Z --generate-notes --title "vX.Y.Z — <Sprint theme>"
    ```

## Step 6: Update version tracking

19. Update **Environment Versions** table in `docs/plan/releases.md`:
    - Set **PRD** row: version = `vX.Y.Z`, branch/tag = `main`, deployed at = today's date.
20. Update the **Current version** line in `docs/plan/releases.md`.
21. Mark the sprint as released in the sprint file:
    ```markdown
    **Released:** vX.Y.Z — YYYY-MM-DD
    ```
22. Commit:
    ```bash
    git add docs/plan/releases.md docs/plan/sprints/
    git commit -m "docs: update PRD version to vX.Y.Z"
    git push origin main
    ```
23. Switch back to the sprint branch:
    ```bash
    git checkout <sprint-branch>
    ```

## Report

Print a summary:
- Version released: `vX.Y.Z`
- Tag: link to the tag on GitHub
- GitHub Release: link to the release
- PRD URLs (backend + frontend from `docs/DEPLOYMENT.md`)
- Remind user to verify production manually

## Monitoring the deploy

**Option A — Railway CLI (terminal):**
```bash
railway status --environment production
railway logs --service backend --environment production
railway logs --service frontend --environment production
```

**Option B — Railway dashboard (browser):**
1. Go to [railway.app](https://railway.app) → studentsbff → production environment
2. Click backend or frontend service → Deployments tab

**What to look for:**
- Status **Active** = deploy succeeded, service is healthy
- Status **Failed** = deploy failed; check logs for the error
- Railway runs the healthcheck (`/actuator/health` for backend, `/` for frontend) before marking the deploy Active
