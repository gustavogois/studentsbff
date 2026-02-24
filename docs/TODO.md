# StudentsBFF — TODO

Current sprint tasks are tracked here. Updated after each task completion.

## Sprint 002 — School Context

### P1 — Student Profile (B08)
- [x] P1.1 — Student profile backend (service + DTOs + controller)
- [x] P1.2 — Student profile frontend (page + navigation)

### P2 — Database Schema V2 & Entities (B20)
- [x] P2.1 — Flyway V2 migration (school_events table + OAuth token columns)
- [x] P2.2 — SchoolEvent entity, enums, repository + User token fields

### P3 — OAuth Token Persistence & Gmail Scope (B20)
- [x] P3.1 — Persist Google OAuth tokens and add Gmail scope

### P4 — Gmail API Client (B20)
- [x] P4.1 — Gmail service to fetch emails

### P5 — LLM Provider & Email Parsing (B20)
- [x] P5.1 — LLMProvider interface and OpenAI implementation
- [x] P5.2 — Email parsing service with AI extraction

### P6 — School Events API & Sync (B20)
- [x] P6.1 — SchoolEvent service and DTOs
- [x] P6.2 — Gmail sync orchestration and school events API endpoints

### P7 — Frontend Events (B20)
- [x] P7.1 — School event types and API services
- [x] P7.2 — School events page with Gmail sync UI

---

## Sprint 001 — Walking Skeleton (Done)

<details>
<summary>All 21 tasks completed</summary>

### P1 — Project Scaffolding (B01)
- [x] P1.1 — Backend Maven project with Spring Boot 3
- [x] P1.2 — Docker Compose for local PostgreSQL
- [x] P1.3 — Frontend Vite + React 19 + TypeScript + Tailwind CSS

### P2 — Database Schema & Entities (B02)
- [x] P2.1 — Flyway V1 migration
- [x] P2.2 — JPA entities and repositories

### P3 — Google OAuth2 Authentication (B03)
- [x] P3.1 — JWT service (issue + validate tokens)
- [x] P3.2 — JWT authentication filter
- [x] P3.3 — OAuth2 success handler and Security config
- [x] P3.4 — GET /api/users/me endpoint

### P4 — CRUD Subjects & Topics (B04)
- [x] P4.1 — Subject service and DTOs
- [x] P4.2 — Subject controller
- [x] P4.3 — Topic service and DTOs
- [x] P4.4 — Topic controller

### P5 — Frontend Foundation & Auth (B06, B07)
- [x] P5.1 — TypeScript types and API client
- [x] P5.2 — AuthContext and auth service
- [x] P5.3 — ProtectedRoute and Layout components
- [x] P5.4 — Login page and OAuth callback

### P6 — Frontend Subjects & Dashboard (B06)
- [x] P6.1 — Subject and topic API services
- [x] P6.2 — Dashboard page and SubjectCard
- [x] P6.3 — Subjects page with CRUD
- [x] P6.4 — Subject detail page with topics CRUD

</details>
