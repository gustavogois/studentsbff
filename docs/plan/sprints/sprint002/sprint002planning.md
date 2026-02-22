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

## Next Step

Run `/plan-sprint-tasks` to break these items into concrete TDD tasks with execution order.
