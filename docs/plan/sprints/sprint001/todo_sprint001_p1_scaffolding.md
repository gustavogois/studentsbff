# P1 — Project Scaffolding: Implementation Plan

**Sprint:** 001
**Status:** Done
**Backlog item:** B01
**Goal:** Both backend (Spring Boot 3 + Maven) and frontend (React 19 + Vite) projects compile, start, and pass a smoke test.

---

## Tasks

### P1.1 — Backend Maven project with Spring Boot 3

**Tests (write first):**
- [ ] `StudentsBffApplicationTest#contextLoads` — verifies Spring context starts without errors (smoke test)

**Implementation:**
- [ ] Generate `backend/pom.xml` with Spring Boot 3.3+, Java 21, and dependencies: Spring Web, Spring Data JPA, Spring Security, Spring OAuth2 Client, PostgreSQL Driver, Flyway, Lombok, MapStruct, jjwt, Validation, H2 (test), JUnit 5, Mockito
- [ ] Create `backend/src/main/java/com/studentsbff/StudentsBffApplication.java`
- [ ] Create `backend/src/main/resources/application.yml` (common config)
- [ ] Create `backend/src/main/resources/application-dev.yml` (PostgreSQL datasource, Flyway, JWT dev secret)
- [ ] Create `backend/src/main/resources/application-test.yml` (H2, `create-drop`, Flyway disabled)
- [ ] Create `backend/.mvn/wrapper/` with Maven wrapper files
- [ ] Create `backend/mvnw` and `backend/mvnw.cmd`

**Commit:** `feat(sprint001): P1.1 — scaffold backend Maven project`

---

### P1.2 — Docker Compose for local PostgreSQL

**Tests (write first):**
- [ ] _No automated test — manual verification: `docker compose up -d` starts PostgreSQL on port 5432_

**Implementation:**
- [ ] Create `docker-compose.yml` at project root with PostgreSQL 16 (db: studentsbff, user: studentsbff, pass: studentsbff, port 5432, named volume `pgdata`)

**Commit:** `feat(sprint001): P1.2 — add Docker Compose for local PostgreSQL`

---

### P1.3 — Frontend Vite + React 19 + TypeScript + Tailwind CSS

**Tests (write first):**
- [ ] `App.test.tsx#renders without crashing` — basic render smoke test with Vitest + React Testing Library

**Implementation:**
- [ ] Initialize `frontend/` with Vite (React + TypeScript template)
- [ ] Install dependencies: React 19, React Router, Axios, Tailwind CSS 4, Vitest, @testing-library/react, @testing-library/jest-dom
- [ ] Configure `vite.config.ts` with API proxy to `http://localhost:8080`
- [ ] Configure `tailwind.config.js` and `postcss.config.js`
- [ ] Create `frontend/src/index.css` with Tailwind directives
- [ ] Create `frontend/src/App.tsx` with placeholder route structure
- [ ] Create `frontend/src/main.tsx` as entry point
- [ ] Add npm scripts: `dev`, `build`, `test`, `lint`
- [ ] Verify `npm run build` and `npm test` pass

**Commit:** `feat(sprint001): P1.3 — scaffold frontend React 19 + Vite + Tailwind`

---

## Execution Order

1. P1.1 — Backend Maven project
2. P1.2 — Docker Compose
3. P1.3 — Frontend Vite project

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P1.1 — scaffold backend Maven project` | P1.1 |
| 2 | `feat(sprint001): P1.2 — add Docker Compose for local PostgreSQL` | P1.2 |
| 3 | `feat(sprint001): P1.3 — scaffold frontend React 19 + Vite + Tailwind` | P1.3 |

## Manual Testing

1. `docker compose up -d` — verify PostgreSQL starts (`docker compose ps` shows healthy)
2. `cd backend && ./mvnw spring-boot:run` — verify app starts on port 8080 (will fail on Flyway if no migration yet, that's expected — the smoke test uses H2)
3. `cd frontend && npm run dev` — verify dev server starts on port 5173, shows placeholder page

## Definition of Done

- [ ] All tests pass (`./mvnw verify` / `npm test`)
- [ ] No compilation warnings
- [ ] `docker compose up -d` starts PostgreSQL successfully
- [ ] Frontend `npm run build` produces dist/ output
