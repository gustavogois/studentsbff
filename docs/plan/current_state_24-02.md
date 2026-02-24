# StudentsBFF — Current State (2026-02-24)

## Sprint 001 (v0.1.0) — "Walking Skeleton" — Done

Merged to `main` on 2026-02-22. PR #1.

### Delivered
- Backend: Spring Boot 3 + Java 21 + PostgreSQL 16 + Flyway V1
- Google OAuth2 login with JWT session tokens
- CRUD subjects and topics with ownership validation
- Frontend: React 19 + TypeScript + Tailwind CSS — Login, Dashboard, Subjects, Topics
- 41 backend tests + 25 frontend tests

---

## Sprint 002 — "School Context" — Code Complete, Not Tested

Branch: `claude/plan-sprint-002-OhEhl` (not merged yet).

### Implemented (12 tasks, P1.1–P7.2)
- **B08** — Student profile management (grade, school) — backend + frontend
- **B20** — Gmail integration with AI parsing:
  - Flyway V2 migration (school_events table, OAuth token columns)
  - OAuth token persistence + Gmail scope
  - Gmail API client service
  - LLMProvider interface + OpenAI GPT-4o implementation
  - Email parsing service (AI extraction of school events)
  - School events API + sync orchestration
  - Frontend: school events page + Gmail sync UI

### Bugs Fixed During Manual Testing
- Dev ports changed to 8081 (backend) and 5174 (frontend) to avoid conflicts
- `OpenAiProvider` and `EmailParsingService` — added `@Autowired` on primary constructor (Spring couldn't resolve between multiple constructors)
- `OpenAiConfig` — changed `@Configuration` to `@Component`

### Blocking Issue: Google OAuth Login
- OAuth consent screen is in **Testing** mode — need to add test user emails in Google Cloud Console (APIs & Services → OAuth consent screen → Test users)
- Manual end-to-end testing not completed yet

### Open Question: Gmail Integration (B20) Viability
The student's Google Workspace for Education account has restrictions:
- Cannot send/receive emails outside the school domain
- OAuth for third-party apps likely blocked by school admin
- Gmail API access requires admin whitelist

**Alternatives discussed:**
1. **B11 — Manual data entry** (already in backlog) — student inputs exams/deadlines manually
2. **Parent Gmail account** — read school communications from parent's personal inbox
3. **Google Classroom API** — if the school uses Classroom (also requires admin approval)
4. **Photo/OCR capture (B19)** — take photos of school agenda/boards

**Recommendation:** Prioritize B11 (manual entry) as the reliable baseline. Gmail/Classroom integration becomes optional.

---

## Tech Debt

| ID | Description | Severity | Sprint |
|----|-------------|----------|--------|
| TD01 | JWT stored in localStorage — vulnerable to XSS. Migrate to httpOnly cookies. | Medium | 001 |
| TD02 | Flyway disabled in test profile (H2 + ddl-auto). Add Testcontainers for migration validation. | Low | 001 |

---

## Next Steps

1. Resolve OAuth consent screen → complete manual login test
2. Test sprint 002 end-to-end (profile, login, Gmail sync if possible)
3. Decide on B20 (Gmail) scope given school account limitations
4. Close sprint 002 (`/finishSprint`, merge to main)
5. Plan sprint 003 — likely B11 (manual event entry) and/or B05 (parent features)
