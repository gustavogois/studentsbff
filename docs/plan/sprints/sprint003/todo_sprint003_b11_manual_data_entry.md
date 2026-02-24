# B11 — Manual Data Entry (Exams, Assignments, Deadlines): Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B11
**Goal:** Full CRUD for school events so students can manually register exams, assignments, and deadlines with dates and optional subject linking.

---

## API Contract

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| POST | `/api/school-events` | `SchoolEventRequest` | `SchoolEventResponse` (201) |
| GET | `/api/school-events` | — (query: `?from=&to=`) | `List<SchoolEventResponse>` (200) |
| GET | `/api/school-events/{id}` | — | `SchoolEventResponse` (200) |
| PUT | `/api/school-events/{id}` | `SchoolEventRequest` | `SchoolEventResponse` (200) |
| DELETE | `/api/school-events/{id}` | — | 204 No Content |

### Request DTO

```
SchoolEventRequest {
  title: String (required),
  eventType: EventType (required — EXAM, ASSIGNMENT, DEADLINE),
  eventDate: LocalDate (required),
  subjectId: UUID (optional — link to existing subject),
  description: String (optional)
}
```

### Response DTO

```
SchoolEventResponse {
  id: UUID,
  title: String,
  eventType: EventType,
  eventDate: LocalDate,
  subjectId: UUID (nullable),
  subjectName: String (nullable),
  description: String (nullable),
  createdAt: Instant,
  updatedAt: Instant
}
```

---

## Tasks

### B11.1 — Flyway migration V4 + entity + enum

**Tests (write first):**
- [ ] `SchoolEventRepositoryTest#shouldSaveAndFindByStudentId` — save event, query by student, verify returned
- [ ] `SchoolEventRepositoryTest#shouldFindByStudentIdAndDateRange` — save events on different dates, query with from/to, verify filter works
- [ ] `SchoolEventRepositoryTest#shouldDeleteEvent` — save event, delete, verify gone

**Implementation:**
- [ ] Create `V4__add_school_events.sql`:
  ```sql
  CREATE TABLE school_events (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      student_id UUID NOT NULL REFERENCES students(id),
      subject_id UUID REFERENCES subjects(id) ON DELETE SET NULL,
      title VARCHAR(255) NOT NULL,
      event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('EXAM','ASSIGNMENT','DEADLINE')),
      event_date DATE NOT NULL,
      description TEXT,
      created_at TIMESTAMP NOT NULL DEFAULT now(),
      updated_at TIMESTAMP NOT NULL DEFAULT now()
  );
  CREATE INDEX idx_school_events_student_date ON school_events(student_id, event_date);
  ```
- [ ] Create `model/EventType.java` — enum: `EXAM`, `ASSIGNMENT`, `DEADLINE`
- [ ] Create `model/SchoolEvent.java` — JPA entity with `@ManyToOne` to Student and optional `@ManyToOne` to Subject
- [ ] Create `repository/SchoolEventRepository.java` — methods: `findAllByStudentId`, `findAllByStudentIdAndEventDateBetween`

**Commit:** `feat(sprint003): B11.1 — add school_events migration, entity, and repository`

---

### B11.2 — Backend service + DTOs + mapper

**Tests (write first):**
- [ ] `SchoolEventServiceTest#shouldCreateEvent` — mock repo, verify event saved with correct fields
- [ ] `SchoolEventServiceTest#shouldCreateEventWithSubjectLink` — verify subject FK set when subjectId provided
- [ ] `SchoolEventServiceTest#shouldListEventsByStudent` — mock repo, verify list returned
- [ ] `SchoolEventServiceTest#shouldListEventsByDateRange` — verify from/to filter used
- [ ] `SchoolEventServiceTest#shouldUpdateEvent` — verify fields updated
- [ ] `SchoolEventServiceTest#shouldDeleteEvent` — verify delete called
- [ ] `SchoolEventServiceTest#shouldThrowWhenEventNotFound` — verify EntityNotFoundException
- [ ] `SchoolEventServiceTest#shouldThrowWhenEventNotOwnedByStudent` — verify AccessDeniedException

**Implementation:**
- [ ] Create `dto/SchoolEventRequest.java` — Lombok `@Data`: `title` (`@NotBlank`), `eventType` (`@NotNull`), `eventDate` (`@NotNull`), `subjectId` (nullable), `description` (nullable)
- [ ] Create `dto/SchoolEventResponse.java` — Java Record: `id`, `title`, `eventType`, `eventDate`, `subjectId`, `subjectName`, `description`, `createdAt`, `updatedAt`
- [ ] Create `mapper/SchoolEventMapper.java` — MapStruct: `toResponse(SchoolEvent)` with `subjectName` from `event.getSubject().getName()`
- [ ] Create `service/SchoolEventService.java` — CRUD methods with ownership validation (event belongs to student)

**Commit:** `feat(sprint003): B11.2 — add school event service, DTOs, and mapper`

---

### B11.3 — Backend controller

**Tests (write first):**
- [ ] `SchoolEventControllerTest#shouldCreateEvent` — POST with valid body, verify 201 + response
- [ ] `SchoolEventControllerTest#shouldListEvents` — GET, verify 200 + list
- [ ] `SchoolEventControllerTest#shouldListEventsWithDateFilter` — GET with from/to params, verify filter passed to service
- [ ] `SchoolEventControllerTest#shouldGetEventById` — GET by id, verify 200
- [ ] `SchoolEventControllerTest#shouldUpdateEvent` — PUT with valid body, verify 200
- [ ] `SchoolEventControllerTest#shouldDeleteEvent` — DELETE, verify 204
- [ ] `SchoolEventControllerTest#shouldReturn401WhenNotAuthenticated` — no auth, verify 401

**Implementation:**
- [ ] Create `controller/SchoolEventController.java`:
  - `POST /api/school-events` → 201 Created
  - `GET /api/school-events?from=&to=` → 200 OK (optional date range filter)
  - `GET /api/school-events/{id}` → 200 OK
  - `PUT /api/school-events/{id}` → 200 OK
  - `DELETE /api/school-events/{id}` → 204 No Content
  - Uses same `getCurrentStudentId()` pattern as SubjectController

**Commit:** `feat(sprint003): B11.3 — add school event REST controller`

---

### B11.4 — Frontend service + types

**Tests (write first):**
- [ ] `schoolEventService.test.ts#shouldFetchEvents` — mock API, verify `getSchoolEvents()` returns list
- [ ] `schoolEventService.test.ts#shouldFetchEventsWithDateRange` — verify query params passed
- [ ] `schoolEventService.test.ts#shouldCreateEvent` — verify POST called with request body
- [ ] `schoolEventService.test.ts#shouldUpdateEvent` — verify PUT called
- [ ] `schoolEventService.test.ts#shouldDeleteEvent` — verify DELETE called

**Implementation:**
- [ ] Add types to `types/index.ts`: `SchoolEvent`, `SchoolEventRequest`, `EventType`
- [ ] Create `services/schoolEventService.ts`: `getSchoolEvents(from?, to?)`, `getSchoolEvent(id)`, `createSchoolEvent(data)`, `updateSchoolEvent(id, data)`, `deleteSchoolEvent(id)`

**Commit:** `feat(sprint003): B11.4 — add school event frontend types and service`

---

### B11.5 — Frontend events page

**Tests (write first):**
- [ ] `SchoolEventsPage.test.tsx#shouldDisplayEventsList` — mock events, verify titles and dates shown
- [ ] `SchoolEventsPage.test.tsx#shouldShowEmptyState` — no events, verify EmptyState component rendered
- [ ] `SchoolEventsPage.test.tsx#shouldCreateEvent` — fill form, submit, verify API called
- [ ] `SchoolEventsPage.test.tsx#shouldDeleteEventWithConfirm` — click delete, confirm dialog, verify API called
- [ ] `SchoolEventsPage.test.tsx#shouldShowEventTypeLabels` — verify EXAM/ASSIGNMENT/DEADLINE rendered with labels

**Implementation:**
- [ ] Create `pages/SchoolEventsPage.tsx`:
  - List of events with title, type badge, date, optional subject name
  - Create form: title, type dropdown, date picker, subject dropdown (from existing subjects), description textarea
  - Delete with ConfirmDialog (from B24)
  - EmptyState (from B25) when no events
  - Edit inline or via form toggle
- [ ] Update `App.tsx` — add `/events` route
- [ ] Update `Layout.tsx` — add "Events" nav link

**Commit:** `feat(sprint003): B11.5 — add school events page with create, list, and delete`

---

## Execution Order

1. B11.1 — Migration + entity + repository (+ repo tests)
2. B11.2 — Service + DTOs + mapper (+ service tests)
3. B11.3 — Controller (+ controller tests)
4. B11.4 — Frontend types + service (+ service tests)
5. B11.5 — Frontend events page (+ page tests)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B11.1 — add school_events migration, entity, and repository` | B11.1 |
| 2 | `feat(sprint003): B11.2 — add school event service, DTOs, and mapper` | B11.2 |
| 3 | `feat(sprint003): B11.3 — add school event REST controller` | B11.3 |
| 4 | `feat(sprint003): B11.4 — add school event frontend types and service` | B11.4 |
| 5 | `feat(sprint003): B11.5 — add school events page with create, list, and delete` | B11.5 |

## Manual Testing

1. **Create event:** POST `/api/school-events` with `{ "title": "Math Exam", "eventType": "EXAM", "eventDate": "2026-03-15" }` → 201
2. **Create with subject:** POST with `"subjectId": "<existing-subject-uuid>"` → 201 with `subjectName` populated
3. **List events:** GET `/api/school-events` → 200 with list
4. **Filter by date:** GET `/api/school-events?from=2026-03-01&to=2026-03-31` → only March events
5. **Update event:** PUT `/api/school-events/{id}` with new title → 200
6. **Delete event:** DELETE `/api/school-events/{id}` → 204
7. **Frontend:** Navigate to `/events` → see events list
8. **Frontend create:** Fill form, submit → event appears in list
9. **Frontend delete:** Click delete → ConfirmDialog appears → confirm → event removed

## Definition of Done

- [ ] All backend tests pass (`./mvnw verify`)
- [ ] All frontend tests pass (`npm test`)
- [ ] CRUD operations work end-to-end
- [ ] Events can optionally link to subjects
- [ ] Date range filter works on GET
- [ ] Events page uses ConfirmDialog and EmptyState components
- [ ] Nav bar has "Events" link
