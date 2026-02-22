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

_No releases yet._
