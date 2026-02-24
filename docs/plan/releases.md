# StudentsBFF — Releases

## Versioning Convention

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** — Breaking API changes or major UX overhaul
- **MINOR** — New features (backward-compatible)
- **PATCH** — Bug fixes and minor improvements

## Environment Versions

| Environment | Version | Date | Notes |
|-------------|---------|------|-------|
| STG | _not deployed_ | — | — |
| PRD | _not deployed_ | — | — |

## Release Flow

1. Sprint implementation complete → `/finishSprint` (push + PR)
2. `/deploySTG` — merge PR to `main`, CI deploys to staging
3. Manual testing on staging
4. `/deployPRD` — CHANGELOG update, git tag, explicit `railway up` to production
5. GitHub Release created with auto-generated notes

## Release History

### v0.1.0 — Sprint 001: Walking Skeleton
- **Sprint:** 001
- **Status:** Implemented (pending deployment)
- **Scope:** Project scaffolding, Flyway V1 DB schema, Google OAuth2 + JWT, CRUD subjects & topics, React frontend with login/dashboard/subjects pages, protected routes

### v0.2.0 — Sprint 002: School Context
- **Sprint:** 002
- **Status:** Done
- **Scope:** Student profile management (B08). Gmail integration (B20) cancelled — school Google accounts block unverified OAuth apps; all Gmail/OpenAI code removed. Added mandatory exception logging for all error responses.
