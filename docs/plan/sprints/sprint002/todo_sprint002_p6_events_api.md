# P6 — School Events API & Sync Orchestration (B20): Implementation Plan

**Sprint:** 002
**Status:** Not Started
**Backlog item:** B20 (part 5)
**Goal:** REST API for school events CRUD and Gmail sync orchestration that ties Gmail fetching, AI parsing, and event storage together.

---

## API Contract

### Gmail Sync

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| POST | `/api/gmail/sync` | `{ daysBack?: int }` | `GmailSyncResponse` (200) |

### School Events

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| GET | `/api/school-events` | — | `SchoolEventResponse[]` |
| DELETE | `/api/school-events/{id}` | — | 204 No Content |

### Response DTOs

```
SchoolEventResponse { id: UUID, title: String, eventType: String, subjectId: UUID?, subjectName: String?, description: String, eventDate: Instant, source: String, createdAt: Instant }
GmailSyncResponse { newEventsCount: int, skippedDuplicates: int, totalEmails: int }
```

---

## Tasks

### P6.1 — SchoolEvent service and DTOs

**Tests (write first):**
- [ ] `SchoolEventServiceTest#shouldListEventsForStudent` — mock repo returns 2 events, verify service returns both as DTOs
- [ ] `SchoolEventServiceTest#shouldDeleteEvent` — verify delete called, verify ownership check
- [ ] `SchoolEventServiceTest#shouldThrowWhenNotOwner` — verify AccessDeniedException when student doesn't own event
- [ ] `SchoolEventServiceTest#shouldThrowWhenEventNotFound` — verify EntityNotFoundException for unknown ID
- [ ] `SchoolEventServiceTest#shouldSaveEventsFromParsedData` — verify ParsedSchoolEvent → SchoolEvent mapping + save

**Implementation:**
- [ ] Create `dto/SchoolEventResponse.java` — Java Record: `id`, `title`, `eventType`, `subjectId`, `subjectName`, `description`, `eventDate`, `source`, `createdAt`
- [ ] Create `mapper/SchoolEventMapper.java` — MapStruct: `SchoolEvent` → `SchoolEventResponse`
- [ ] Create `service/SchoolEventService.java`:
  - `findAllByStudentId(UUID studentId)` → `List<SchoolEventResponse>`
  - `delete(UUID eventId, UUID studentId)` — with ownership check
  - `saveFromParsed(List<ParsedSchoolEvent> parsed, Student student, List<Subject> subjects)` — maps parsed events to entities, matches subject by name, saves

**Commit:** `feat(sprint002): P6.1 — add SchoolEvent service and DTOs`

---

### P6.2 — Gmail sync orchestration and API endpoints

**Tests (write first):**
- [ ] `GmailSyncServiceTest#shouldOrchestrateFullSync` — mock GmailService + EmailParsingService + SchoolEventService, verify orchestration flow
- [ ] `GmailSyncServiceTest#shouldSkipDuplicateEmails` — mock repo existsBySourceEmailId returning true, verify email skipped
- [ ] `GmailSyncServiceTest#shouldReturnSyncStats` — verify GmailSyncResponse contains correct counts
- [ ] `GmailSyncServiceTest#shouldThrowWhenNoGmailTokens` — verify descriptive error when user has no tokens
- [ ] `SchoolEventControllerTest#shouldListEvents` — `@WebMvcTest`, mock auth + service, verify 200 + JSON array
- [ ] `SchoolEventControllerTest#shouldDeleteEvent` — DELETE, verify 204
- [ ] `SchoolEventControllerTest#shouldReturn404WhenEventNotFound` — DELETE unknown ID, verify 404
- [ ] `GmailSyncControllerTest#shouldTriggerSync` — `@WebMvcTest`, POST, verify 200 + sync stats
- [ ] `GmailSyncControllerTest#shouldReturn401WhenNotAuthenticated` — no auth, verify 401

**Implementation:**
- [ ] Create `dto/GmailSyncRequest.java` — Lombok class: `daysBack` (Integer, optional, default 30)
- [ ] Create `dto/GmailSyncResponse.java` — Java Record: `newEventsCount`, `skippedDuplicates`, `totalEmails`
- [ ] Create `service/GmailSyncService.java`:
  - `sync(User user, int daysBack)` → `GmailSyncResponse`
  - Orchestrates: fetch emails → filter already-processed (by sourceEmailId) → parse with AI → save events
  - Returns stats (new, skipped, total)
- [ ] Create `controller/GmailSyncController.java` — `POST /api/gmail/sync`
- [ ] Create `controller/SchoolEventController.java` — `GET /api/school-events`, `DELETE /api/school-events/{id}`

**Commit:** `feat(sprint002): P6.2 — add Gmail sync orchestration and school events API`

---

## Execution Order

1. P6.1 — SchoolEvent service + DTOs (depends on P2.2 entity, P5.2 ParsedSchoolEvent)
2. P6.2 — Sync orchestration + controllers (depends on P6.1 + P4.1 GmailService + P5.2 EmailParsingService)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P6.1 — add SchoolEvent service and DTOs` | P6.1 |
| 2 | `feat(sprint002): P6.2 — add Gmail sync orchestration and school events API` | P6.2 |

## Manual Testing

1. Ensure all prerequisites (P1-P5) are complete and user has Gmail tokens
2. **Trigger sync:** `curl -X POST -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"daysBack":7}' http://localhost:8080/api/gmail/sync` → returns sync stats
3. **List events:** `curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/school-events` → returns extracted events
4. **Delete event:** `curl -X DELETE -H "Authorization: Bearer <JWT>" http://localhost:8080/api/school-events/{id}` → 204
5. **Re-sync:** Trigger sync again → duplicates should be skipped (check skippedDuplicates count)

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] Gmail sync fetches → parses → stores events in one flow
- [ ] Deduplication by source_email_id works
- [ ] Sync returns useful stats (new, skipped, total)
- [ ] School events CRUD respects ownership
- [ ] No compilation warnings
