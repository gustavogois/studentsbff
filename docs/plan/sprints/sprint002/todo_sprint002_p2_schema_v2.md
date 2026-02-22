# P2 — Database Schema V2 & Entities (B20): Implementation Plan

**Sprint:** 002
**Status:** Not Started
**Backlog item:** B20 (part 1)
**Goal:** Add school_events table and OAuth token columns to support Gmail integration.

---

## Tasks

### P2.1 — Flyway V2 migration

**Tests (write first):**
- [ ] Flyway migration validated implicitly by `@DataJpaTest` integration tests in P2.2

**Implementation:**
- [ ] Create `V2__add_school_events_and_oauth_tokens.sql`:
  - `ALTER TABLE users ADD COLUMN google_access_token TEXT`
  - `ALTER TABLE users ADD COLUMN google_refresh_token TEXT`
  - `ALTER TABLE users ADD COLUMN google_token_expiry TIMESTAMP`
  - `CREATE TABLE school_events`:
    - `id UUID PRIMARY KEY DEFAULT gen_random_uuid()`
    - `student_id UUID NOT NULL REFERENCES students(id)`
    - `title VARCHAR(500) NOT NULL`
    - `event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('EXAM', 'ASSIGNMENT', 'DEADLINE', 'OTHER'))`
    - `subject_id UUID REFERENCES subjects(id) ON DELETE SET NULL` (nullable — AI may or may not match a subject)
    - `description TEXT`
    - `event_date TIMESTAMP NOT NULL`
    - `source VARCHAR(20) NOT NULL CHECK (source IN ('GMAIL', 'MANUAL'))`
    - `source_email_id VARCHAR(255)` (nullable — Gmail message ID for dedup)
    - `created_at TIMESTAMP NOT NULL DEFAULT now()`
    - `updated_at TIMESTAMP NOT NULL DEFAULT now()`
  - `CREATE INDEX idx_school_events_student_id ON school_events(student_id)`
  - `CREATE INDEX idx_school_events_source_email_id ON school_events(source_email_id)`

**Commit:** `feat(sprint002): P2.1 — add Flyway V2 migration for school events and OAuth tokens`

---

### P2.2 — JPA entities and repositories

**Tests (write first):**
- [ ] `SchoolEventRepositoryTest#shouldSaveAndFindByStudentId` — save event, find by student ID, verify result
- [ ] `SchoolEventRepositoryTest#shouldFindBySourceEmailId` — save event with email ID, find by it, verify for dedup
- [ ] `SchoolEventRepositoryTest#shouldReturnEmptyWhenNoEventsForStudent` — verify empty list

**Implementation:**
- [ ] Create `model/EventType.java` — enum: `EXAM`, `ASSIGNMENT`, `DEADLINE`, `OTHER`
- [ ] Create `model/EventSource.java` — enum: `GMAIL`, `MANUAL`
- [ ] Create `model/SchoolEvent.java` — JPA entity: id, student (ManyToOne), title, eventType, subject (ManyToOne nullable), description, eventDate, source, sourceEmailId, createdAt, updatedAt. Lombok @Data/@Builder
- [ ] Create `repository/SchoolEventRepository.java` — `findAllByStudentId(UUID)`, `findBySourceEmailId(String)`, `existsBySourceEmailId(String)`
- [ ] Update `model/User.java` — add `googleAccessToken`, `googleRefreshToken`, `googleTokenExpiry` fields

**Commit:** `feat(sprint002): P2.2 — add SchoolEvent entity and update User with OAuth token fields`

---

## Execution Order

1. P2.1 — Flyway V2 migration (must exist before entities can be tested)
2. P2.2 — JPA entities and repositories (depends on P2.1 schema)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P2.1 — add Flyway V2 migration for school events and OAuth tokens` | P2.1 |
| 2 | `feat(sprint002): P2.2 — add SchoolEvent entity and update User with OAuth token fields` | P2.2 |

## Manual Testing

1. Start PostgreSQL via Docker Compose
2. Start backend: `cd backend && ./mvnw spring-boot:run`
3. Verify Flyway applies V2 migration without errors (check logs)
4. Connect to DB and verify:
   - `SELECT column_name FROM information_schema.columns WHERE table_name='users'` — should include google_access_token, google_refresh_token, google_token_expiry
   - `SELECT * FROM school_events` — table exists, empty

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] V2 migration applies cleanly on fresh DB
- [ ] V2 migration applies cleanly after V1
- [ ] SchoolEvent entity maps correctly to DB
- [ ] User entity has new OAuth token fields
- [ ] Repository queries work for school events
- [ ] No compilation warnings
