#!/usr/bin/env bash
# Pre-deployment validation pipeline.
# Runs all checks from the CLAUDE.md "Pre-Deployment Validation" section
# and produces a deployment-readiness report with BLOCKER/WARNING severity.
#
# Usage: ./scripts/pre-deploy-validate.sh [stg|prd]
#   stg = validate for staging (default)
#   prd = validate for production

set -euo pipefail

cd "$(git rev-parse --show-toplevel)"

ENV="${1:-stg}"

claude -p "Run the full Pre-Deployment Validation checklist from CLAUDE.md for the **${ENV}** environment.

Execute each check in order:

1. Run full test suite: cd backend && ./mvnw verify, then cd frontend && npm run build && npm test
2. Read backend SecurityConfig.java and WebConfig.java — verify CORS allowedOrigins uses the frontend.url property. Cross-check with application-prod.yml
3. Read application-prod.yml — verify OAuth2 redirect-uri uses {baseUrl} (not hardcoded)
4. Read frontend/.env.production — verify VITE_API_URL matches the actual backend domain
5. Search for 'localhost' in frontend/src/ and backend/src/main/ (exclude test files). Flag any hardcoded localhost in runtime code
6. Compare Node version in frontend/nixpacks.toml with .github/workflows/frontend-ci.yml
7. Compare JDK version in backend/nixpacks.toml with .github/workflows/backend-ci.yml
8. Find all \${VAR} references in application-prod.yml. Cross-reference with docs/DEPLOYMENT.md. Flag any missing
9. Verify server.forward-headers-strategy: framework is set in application-prod.yml
10. Verify backend/railway.toml has healthcheckPath = '/actuator/health' and frontend/railway.toml has healthcheckPath = '/'

After all checks, produce a **Deployment Readiness Report** in this format:

## Deployment Readiness Report — ${ENV^^}

| # | Check | Status | Severity | Details |
|---|-------|--------|----------|---------|
| 1 | Tests | PASS/FAIL | BLOCKER | ... |
| ... | ... | ... | ... | ... |

### Summary
- Total checks: 10
- Passed: X
- Blockers: Y
- Warnings: Z

### Verdict
READY TO DEPLOY / NOT READY (list blockers to fix)

Do NOT fix any issues — only report them. Do NOT deploy anything." \
  --allowedTools "Read,Bash,Glob,Grep"
