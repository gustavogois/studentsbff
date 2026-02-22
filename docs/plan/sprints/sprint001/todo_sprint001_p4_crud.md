# P4 — CRUD Subjects & Topics: Implementation Plan

**Sprint:** 001
**Status:** Not Started
**Backlog item:** B04
**Goal:** Authenticated student can create, read, update, and delete subjects and topics. Ownership validation ensures students only access their own data.

---

## API Contract

### Subjects

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| GET | `/api/subjects` | — | `SubjectResponse[]` |
| POST | `/api/subjects` | `{ name: string }` | `SubjectResponse` (201) |
| GET | `/api/subjects/{id}` | — | `SubjectResponse` |
| PUT | `/api/subjects/{id}` | `{ name: string }` | `SubjectResponse` |
| DELETE | `/api/subjects/{id}` | — | 204 No Content |

### Topics

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| GET | `/api/subjects/{subjectId}/topics` | — | `TopicResponse[]` |
| POST | `/api/subjects/{subjectId}/topics` | `{ name: string, difficulty?: int }` | `TopicResponse` (201) |
| PUT | `/api/subjects/{subjectId}/topics/{id}` | `{ name: string, difficulty?: int }` | `TopicResponse` |
| DELETE | `/api/subjects/{subjectId}/topics/{id}` | — | 204 No Content |

### Response DTOs

```
SubjectResponse { id: UUID, name: String, createdAt: Instant, updatedAt: Instant }
TopicResponse   { id: UUID, name: String, difficulty: int, createdAt: Instant, updatedAt: Instant }
```

---

## Tasks

### P4.1 — Subject service and DTOs

**Tests (write first):**
- [ ] `SubjectServiceTest#shouldListSubjectsForStudent` — mock repo returns 2 subjects, verify service returns both
- [ ] `SubjectServiceTest#shouldCreateSubject` — verify service saves subject with correct student reference
- [ ] `SubjectServiceTest#shouldUpdateSubject` — verify name updated, verify ownership check
- [ ] `SubjectServiceTest#shouldDeleteSubject` — verify delete called, verify ownership check
- [ ] `SubjectServiceTest#shouldThrowWhenSubjectNotFound` — verify EntityNotFoundException for unknown ID
- [ ] `SubjectServiceTest#shouldThrowWhenNotOwner` — verify AccessDeniedException when student doesn't own subject

**Implementation:**
- [ ] Create `dto/SubjectRequest.java` — Lombok class: `name` (required, @NotBlank)
- [ ] Create `dto/SubjectResponse.java` — Java Record: `id`, `name`, `createdAt`, `updatedAt`
- [ ] Create `mapper/SubjectMapper.java` — MapStruct: `Subject` → `SubjectResponse`
- [ ] Create `service/SubjectService.java` — CRUD methods, each takes `studentId` parameter for ownership validation

**Commit:** `feat(sprint001): P4.1 — add Subject service and DTOs`

---

### P4.2 — Subject controller

**Tests (write first):**
- [ ] `SubjectControllerTest#shouldListSubjects` — `@WebMvcTest`, mock service, verify 200 + JSON array
- [ ] `SubjectControllerTest#shouldCreateSubject` — POST with valid body, verify 201 + response
- [ ] `SubjectControllerTest#shouldReturn400WhenNameBlank` — POST with blank name, verify 400
- [ ] `SubjectControllerTest#shouldUpdateSubject` — PUT with valid body, verify 200
- [ ] `SubjectControllerTest#shouldDeleteSubject` — DELETE, verify 204
- [ ] `SubjectControllerTest#shouldReturn404WhenNotFound` — GET unknown ID, verify 404

**Implementation:**
- [ ] Create `controller/SubjectController.java` — REST endpoints, extracts student from SecurityContext
- [ ] Create `config/GlobalExceptionHandler.java` — `@RestControllerAdvice` handling EntityNotFoundException (404), AccessDeniedException (403), MethodArgumentNotValid (400)

**Commit:** `feat(sprint001): P4.2 — add Subject controller`

---

### P4.3 — Topic service and DTOs

**Tests (write first):**
- [ ] `TopicServiceTest#shouldListTopicsForSubject` — mock repo, verify returns topics
- [ ] `TopicServiceTest#shouldCreateTopic` — verify saves with correct subject, default difficulty 3
- [ ] `TopicServiceTest#shouldUpdateTopic` — verify name and difficulty updated
- [ ] `TopicServiceTest#shouldDeleteTopic` — verify delete called
- [ ] `TopicServiceTest#shouldThrowWhenSubjectNotOwned` — verify ownership check on subject before topic operations
- [ ] `TopicServiceTest#shouldValidateDifficultyRange` — verify difficulty must be 1-5

**Implementation:**
- [ ] Create `dto/TopicRequest.java` — Lombok class: `name` (@NotBlank), `difficulty` (optional, default 3, @Min(1) @Max(5))
- [ ] Create `dto/TopicResponse.java` — Java Record: `id`, `name`, `difficulty`, `createdAt`, `updatedAt`
- [ ] Create `mapper/TopicMapper.java` — MapStruct: `Topic` → `TopicResponse`
- [ ] Create `service/TopicService.java` — CRUD methods, delegates ownership check to SubjectService

**Commit:** `feat(sprint001): P4.3 — add Topic service and DTOs`

---

### P4.4 — Topic controller

**Tests (write first):**
- [ ] `TopicControllerTest#shouldListTopics` — `@WebMvcTest`, verify 200 + JSON array
- [ ] `TopicControllerTest#shouldCreateTopic` — POST, verify 201
- [ ] `TopicControllerTest#shouldReturn400WhenNameBlank` — POST with blank name, verify 400
- [ ] `TopicControllerTest#shouldReturn400WhenDifficultyOutOfRange` — POST with difficulty=6, verify 400
- [ ] `TopicControllerTest#shouldUpdateTopic` — PUT, verify 200
- [ ] `TopicControllerTest#shouldDeleteTopic` — DELETE, verify 204

**Implementation:**
- [ ] Create `controller/TopicController.java` — REST endpoints under `/api/subjects/{subjectId}/topics`

**Commit:** `feat(sprint001): P4.4 — add Topic controller`

---

## Execution Order

1. P4.1 — Subject service + DTOs
2. P4.2 — Subject controller + exception handler
3. P4.3 — Topic service + DTOs
4. P4.4 — Topic controller

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P4.1 — add Subject service and DTOs` | P4.1 |
| 2 | `feat(sprint001): P4.2 — add Subject controller` | P4.2 |
| 3 | `feat(sprint001): P4.3 — add Topic service and DTOs` | P4.3 |
| 4 | `feat(sprint001): P4.4 — add Topic controller` | P4.4 |

## Manual Testing

1. Login via Google OAuth to get a JWT token
2. **List subjects (empty):** `curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/subjects` → `[]`
3. **Create subject:** `curl -X POST -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"name":"Mathematics"}' http://localhost:8080/api/subjects` → 201 with SubjectResponse
4. **List subjects:** → array with 1 subject
5. **Create topic:** `curl -X POST -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"name":"Fractions","difficulty":4}' http://localhost:8080/api/subjects/{id}/topics` → 201
6. **Update subject:** PUT with `{"name":"Math"}` → 200
7. **Delete topic:** DELETE → 204
8. **Delete subject:** DELETE → 204

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] All 10 endpoints respond with correct status codes
- [ ] Ownership validation prevents cross-student access
- [ ] Validation returns 400 for blank names and invalid difficulty
- [ ] No compilation warnings
