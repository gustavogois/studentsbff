# Sprint 001 — Planning

## Sprint Goal

**"Walking Skeleton"** — End-to-end app that runs: student can register, login, and manage subjects/topics. Proves the full stack works.

## Sprint Metadata

| Field | Value |
|-------|-------|
| Sprint | 001 |
| Start date | 2026-02-22 |
| End date | 2026-02-22 |
| Status | Done |
| Goal | Walking skeleton — full-stack student flow |
| Constraints | None |

## Selected Backlog Items

| ID | Feature | Priority | Notes |
|----|---------|----------|-------|
| B01 | Project scaffolding (Spring Boot + React + Docker Compose) | Must | Maven, Java 21, React 19, PostgreSQL 16, Docker Compose |
| B02 | Database schema V1 (users, students, subjects, topics) | Must | Flyway V1 migration. Parent table included in schema but parent endpoints deferred |
| B03 | Google OAuth2 authentication (login with Google, JWT session) | Must | Spring Security OAuth2 Client + Google provider. Backend issues JWT session token after OAuth callback. No email/password registration. Prepares for Gmail API scope later |
| B04 | CRUD subjects and topics | Must | Student owns subjects. Ownership validation on all endpoints |
| B06 | Frontend: Google login, dashboard, subjects pages | Must | React 19, Vite, Tailwind CSS, React Router. "Login with Google" button instead of register form |
| B07 | Protected routes and auth context | Must | JWT stored in localStorage, Axios interceptor, ProtectedRoute component |

## Deferred Items

| ID | Feature | Reason | Target Sprint |
|----|---------|--------|---------------|
| B05 | Parent-student linking and parent read-only view | Focus Sprint 1 on student experience only | Sprint 002 |

## Definition of Done

- [x] Backend starts and connects to PostgreSQL via Docker Compose
- [x] Flyway migration runs successfully
- [x] User can login with Google and receive JWT session token
- [x] First-time Google login auto-creates user + student profile
- [x] Authenticated user can CRUD subjects
- [x] Authenticated user can CRUD topics within a subject
- [x] Frontend "Login with Google" page works end-to-end
- [x] Frontend dashboard shows user's subjects
- [x] Frontend subjects page allows add/edit/delete
- [x] Protected routes redirect unauthenticated users to login
- [x] All backend tests pass (`./mvnw test`) — 41 tests
- [x] Frontend builds and tests pass (`npm run build && npm test`) — 25 tests
- [~] ~~Deployed to STG via Railway~~ — deferred (local development only for now)

## Decisions Made During Refinement

1. **Scope:** B01-B04 + B06-B07 (student-focused full-stack)
2. **Parent features deferred:** B05 moves to Sprint 002
3. **No external deadlines:** Work at natural pace
4. **Sprint theme:** Walking skeleton — prove the full stack works end-to-end
5. **Google OAuth2 only** — no email/password registration. Spring Security OAuth2 Client with Google provider. Backend issues JWT after OAuth callback. Prepares for Gmail API scope later
6. **No new backlog items added**
7. **DB schema includes parent_students table** even though B05 is deferred — avoids a schema migration change later
8. **Users table:** `password` column becomes nullable (not needed for OAuth users). `email` and `name` populated from Google profile

## Next Step

Run `/plan-sprint-tasks` to break these items into concrete TDD tasks with execution order.
