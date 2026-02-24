# P1 — Student Profile (B08): Implementation Plan

**Sprint:** 002
**Status:** Done
**Backlog item:** B08
**Goal:** Student can view and edit their profile (grade and school) via backend API and frontend page.

---

## API Contract

| Method | Path | Request Body | Response |
|--------|------|-------------|----------|
| GET | `/api/students/profile` | — | `StudentProfileResponse` |
| PUT | `/api/students/profile` | `{ grade: string, school: string }` | `StudentProfileResponse` |

### Response DTO

```
StudentProfileResponse { id: UUID, grade: String, school: String, createdAt: Instant }
```

---

## Tasks

### P1.1 — Student profile backend (service + DTOs + controller)

**Tests (write first):**
- [ ] `StudentServiceTest#shouldGetStudentProfile` — mock repo, verify returns student profile with grade and school
- [ ] `StudentServiceTest#shouldUpdateStudentProfile` — mock repo, verify grade and school updated
- [ ] `StudentServiceTest#shouldThrowWhenStudentNotFoundOnGetProfile` — verify EntityNotFoundException for unknown student
- [ ] `StudentControllerTest#shouldGetProfile` — `@WebMvcTest`, mock auth + service, verify 200 + JSON
- [ ] `StudentControllerTest#shouldUpdateProfile` — PUT with valid body, verify 200 + updated data
- [ ] `StudentControllerTest#shouldReturn401WhenNotAuthenticated` — no auth header, verify 401

**Implementation:**
- [ ] Create `dto/StudentProfileRequest.java` — Lombok class: `grade` (String), `school` (String)
- [ ] Create `dto/StudentProfileResponse.java` — Java Record: `id`, `grade`, `school`, `createdAt`
- [ ] Create `mapper/StudentMapper.java` — MapStruct: `Student` → `StudentProfileResponse`
- [ ] Create `service/StudentService.java` — `getProfile(UUID studentId)`, `updateProfile(UUID studentId, StudentProfileRequest)`
- [ ] Create `controller/StudentController.java` — `GET /api/students/profile`, `PUT /api/students/profile`, extracts student from SecurityContext (same pattern as SubjectController)

**Commit:** `feat(sprint002): P1.1 — add student profile backend API`

---

### P1.2 — Student profile frontend (page + navigation)

**Tests (write first):**
- [ ] `profileService.test.ts#shouldFetchProfile` — mock API, verify `getProfile()` returns StudentProfile
- [ ] `profileService.test.ts#shouldUpdateProfile` — mock API, verify `updateProfile(data)` sends PUT
- [ ] `ProfilePage.test.tsx#shouldDisplayCurrentProfile` — mock profile data, verify grade and school shown
- [ ] `ProfilePage.test.tsx#shouldUpdateProfile` — fill form, submit, verify API called with updated values
- [ ] `ProfilePage.test.tsx#shouldShowLoadingState` — verify loading indicator while fetching

**Implementation:**
- [ ] Add `StudentProfile` and `StudentProfileRequest` interfaces to `types/index.ts`
- [ ] Create `services/profileService.ts` — `getProfile()`, `updateProfile(data)`
- [ ] Create `pages/ProfilePage.tsx` — displays current grade and school, edit form with save button
- [ ] Update `components/Layout.tsx` — add "Profile" link in navigation
- [ ] Update `App.tsx` — add `/profile` route inside ProtectedRoute

**Commit:** `feat(sprint002): P1.2 — add student profile frontend page`

---

## Execution Order

1. P1.1 — Backend profile API (standalone, uses existing Student entity)
2. P1.2 — Frontend profile page (depends on P1.1 API contract)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P1.1 — add student profile backend API` | P1.1 |
| 2 | `feat(sprint002): P1.2 — add student profile frontend page` | P1.2 |

## Manual Testing

1. Login with Google
2. **GET profile:** `curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/students/profile` → returns current grade (null) and school (null)
3. **Update profile:** `curl -X PUT -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"grade":"7th","school":"Lincoln Middle School"}' http://localhost:8080/api/students/profile` → returns updated profile
4. **Frontend:** Navigate to `/profile` → shows grade and school
5. **Edit:** Change grade to "8th", click Save → profile updates
6. **Navigation:** Verify "Profile" link appears in nav bar

## Definition of Done

- [ ] All tests pass (`./mvnw verify` / `npm test`)
- [ ] GET /api/students/profile returns current student profile
- [ ] PUT /api/students/profile updates grade and school
- [ ] Frontend profile page displays and edits profile
- [ ] Profile link accessible from navigation
- [ ] No compilation warnings or TypeScript errors
