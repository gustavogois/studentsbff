# Sprint 002 — Planning

## Sprint Goal

**"School Context"** — Give the platform awareness of the student's school context through profile management and automatic extraction of school events from Gmail via AI.

## Sprint Metadata

| Field | Value |
|-------|-------|
| Sprint | 002 |
| Start date | 2026-02-22 |
| Goal | School context — student profile + Gmail AI parsing |
| Constraints | None |

## Selected Backlog Items

| ID | Feature | Priority | Notes |
|----|---------|----------|-------|
| B08 | Student profile management (grade, school) | Must | Simple: let students set/edit grade and school name. Student entity already has these fields in DB |
| B20 | Gmail integration for school email parsing (OAuth + AI) | Must | Full flow: expand Google OAuth scope to include Gmail read, fetch emails, parse with OpenAI GPT-4o to extract exams/assignments/deadlines, store as structured school events |

## Feature Details

### B08 — Student Profile Management

**Scope:** Grade and school fields only (already exist in `students` table).

**Backend:**
- `PUT /api/students/profile` — update grade and school
- `GET /api/students/profile` — get current student profile
- StudentProfileRequest / StudentProfileResponse DTOs
- StudentService methods for profile CRUD

**Frontend:**
- Profile page accessible from nav/dashboard
- Edit form with grade (dropdown or text) and school (text input)
- Save and display current values

### B20 — Gmail Integration with AI Parsing

**Scope:** OAuth Gmail scope + fetch emails + OpenAI AI parsing + structured events.

This is the larger feature, touching multiple layers:

**1. OAuth2 Scope Expansion**
- Add `https://www.googleapis.com/auth/gmail.readonly` to Google OAuth2 scopes
- Store Google access token and refresh token after OAuth success (currently not persisted)
- Need new DB columns or table for OAuth tokens

**2. Gmail API Client**
- Service to fetch recent emails using stored Google tokens
- Filter emails by date range and/or sender patterns (school domains)
- Return raw email content for parsing

**3. School Events Data Model**
- New `school_events` table (Flyway V2 migration):
  - id, student_id, title, event_type (EXAM, ASSIGNMENT, DEADLINE, OTHER)
  - subject_id (nullable FK to subjects — AI may link to existing subject)
  - description, event_date, source (GMAIL)
  - source_email_id (Gmail message ID for deduplication)
  - created_at, updated_at

**4. AI Parsing Service (OpenAI GPT-4o)**
- LLMProvider interface for future provider abstraction (per context.md decision)
- OpenAI implementation using GPT-4o
- Prompt engineering: extract structured events (title, type, date, related subject) from email text
- Return parsed events as DTOs

**5. API Endpoints**
- `POST /api/gmail/sync` — trigger email fetch + AI parsing for authenticated student
- `GET /api/school-events` — list extracted events for authenticated student
- `DELETE /api/school-events/{id}` — delete an incorrect event

**6. Frontend**
- "Sync Gmail" button/page to trigger email sync
- School events list/calendar showing extracted events
- Ability to dismiss/delete incorrect events

## Deferred Items

| ID | Feature | Reason | Target Sprint |
|----|---------|--------|---------------|
| B05 | Parent-student linking and parent read-only view | Focus Sprint 002 on student school context features | Sprint 003+ |
| B11 | Manual data entry (exams, assignments, deadlines) | Shares data model with B20 but manual UI deferred | Sprint 003 |

## Definition of Done

- [ ] Student can view and edit their profile (grade, school)
- [ ] Google OAuth flow requests Gmail read scope
- [ ] Google access/refresh tokens are persisted securely
- [ ] Backend can fetch emails from student's Gmail
- [ ] OpenAI GPT-4o parses emails and extracts school events
- [ ] Extracted events are stored in school_events table
- [ ] Student can view list of extracted school events
- [ ] Student can delete incorrect events
- [ ] Gmail sync deduplicates by email message ID
- [ ] LLMProvider interface exists for future provider switching
- [ ] All backend tests pass (`./mvnw verify`)
- [ ] Frontend builds and tests pass (`npm run build && npm test`)
- [ ] Deployed to STG via Railway

## Decisions Made During Refinement

1. **Scope:** B08 (profile) + B20 (Gmail + AI) — focus on school context
2. **B05 (parent features) deferred** — moves to Sprint 003+
3. **B20 promoted from Could to Must** — key differentiator and path to MVP study planner
4. **Gmail only, no manual entry UI** — B11 deferred; data model supports both sources but only Gmail frontend for now
5. **Profile scope: grade + school only** — no avatar, preferences, or notification settings
6. **AI provider: OpenAI GPT-4o** — with LLMProvider abstraction for future Claude/other providers
7. **No external deadlines** — work at natural pace
8. **Sprint version: 0.2.0**

## Task Breakdown

7 phases, 12 tasks total. See individual task files for TDD details.

| Phase | File | Tasks | Scope |
|-------|------|-------|-------|
| P1 | `todo_sprint002_p1_student_profile.md` | P1.1, P1.2 | Student profile backend + frontend (B08) |
| P2 | `todo_sprint002_p2_schema_v2.md` | P2.1, P2.2 | Flyway V2 migration + SchoolEvent entity (B20) |
| P3 | `todo_sprint002_p3_oauth_tokens.md` | P3.1 | OAuth token persistence + Gmail scope (B20) |
| P4 | `todo_sprint002_p4_gmail_client.md` | P4.1 | Gmail API client service (B20) |
| P5 | `todo_sprint002_p5_llm_parsing.md` | P5.1, P5.2 | LLMProvider + email parsing (B20) |
| P6 | `todo_sprint002_p6_events_api.md` | P6.1, P6.2 | School events API + sync orchestration (B20) |
| P7 | `todo_sprint002_p7_frontend_events.md` | P7.1, P7.2 | Frontend events page + Gmail sync UI (B20) |

## Global Execution Order

```
P1.1 → P1.2    (B08 — independent, can start immediately)
P2.1 → P2.2    (B20 — DB foundation)
       P3.1    (depends on P2.2)
       P4.1    (depends on P3.1)
P5.1 → P5.2    (depends on P4.1 for EmailMessage DTO)
P6.1 → P6.2    (depends on P2.2 + P4.1 + P5.2)
P7.1 → P7.2    (depends on P1.1 + P6.2 for all APIs)
```

**Parallelization opportunity:** P1 (profile) and P2 (schema) are independent and can run in parallel.

## Commit Plan (12 commits)

| Order | Commit message | Phase |
|-------|---------------|-------|
| 1 | `feat(sprint002): P1.1 — add student profile backend API` | P1 |
| 2 | `feat(sprint002): P2.1 — add Flyway V2 migration for school events and OAuth tokens` | P2 |
| 3 | `feat(sprint002): P2.2 — add SchoolEvent entity and update User with OAuth token fields` | P2 |
| 4 | `feat(sprint002): P1.2 — add student profile frontend page` | P1 |
| 5 | `feat(sprint002): P3.1 — persist Google OAuth tokens and add Gmail scope` | P3 |
| 6 | `feat(sprint002): P4.1 — add Gmail API client service` | P4 |
| 7 | `feat(sprint002): P5.1 — add LLMProvider interface and OpenAI implementation` | P5 |
| 8 | `feat(sprint002): P5.2 — add email parsing service with AI extraction` | P5 |
| 9 | `feat(sprint002): P6.1 — add SchoolEvent service and DTOs` | P6 |
| 10 | `feat(sprint002): P6.2 — add Gmail sync orchestration and school events API` | P6 |
| 11 | `feat(sprint002): P7.1 — add school event types and API services` | P7 |
| 12 | `feat(sprint002): P7.2 — add school events page with Gmail sync UI` | P7 |

## Next Step

Run `/implement-task` to start implementing tasks following the TDD loop.
