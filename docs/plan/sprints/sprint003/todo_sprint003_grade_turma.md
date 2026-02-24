# B22 — Grade as Enum Dropdown + Turma Field: Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B22
**Goal:** Replace the free-text `grade` field with a constrained enum dropdown (7th-12th) and add a new `turma` (class section) free-text field to the student profile.

---

## Grade Options

| Value | Display (EN) | Display (pt-BR) |
|-------|-------------|-----------------|
| `GRADE_7` | 7th Grade | 7o Ano |
| `GRADE_8` | 8th Grade | 8o Ano |
| `GRADE_9` | 9th Grade | 9o Ano |
| `GRADE_10` | 10th Grade | 1o Ano EM |
| `GRADE_11` | 11th Grade | 2o Ano EM |
| `GRADE_12` | 12th Grade | 3o Ano EM |

---

## API Contract

### `GET /api/students/grades` (new)

**Response:**
```json
[
  { "value": "GRADE_7", "label": "7th Grade" },
  ...
]
```

### `GET /api/students/profile` (updated)
### `PUT /api/students/profile` (updated)

Response and request now include `grade` as enum string and `turma` as string.

---

## Tasks

### B22.1 — Flyway migration + Grade enum + entity changes

**Tests (write first):**
- [ ] `StudentServiceTest#shouldUpdateProfileWithGradeEnum` — verify Grade enum saved correctly
- [ ] `StudentServiceTest#shouldUpdateProfileWithTurma` — verify turma field saved
- [ ] `StudentServiceTest#shouldAcceptNullGradeAndTurma` — verify nullable fields work

**Implementation:**
- [ ] Create `V4__grade_enum_and_turma.sql`:
  ```sql
  UPDATE students SET grade = NULL
    WHERE grade IS NOT NULL
    AND grade NOT IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12');
  ALTER TABLE students ADD COLUMN turma VARCHAR(100);
  ALTER TABLE students ADD CONSTRAINT chk_student_grade
    CHECK (grade IS NULL OR grade IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12'));
  ```
- [ ] Create `model/Grade.java` — enum with `GRADE_7`–`GRADE_12` and a `label` field
- [ ] Update `model/Student.java` — change `grade` from `String` to `Grade` (`@Enumerated(EnumType.STRING)`), add `turma` field
- [ ] Update `dto/StudentProfileRequest.java` — change `grade` to `Grade`, add `turma`
- [ ] Update `dto/StudentProfileResponse.java` — change `grade` to `Grade`, add `turma`
- [ ] Update `service/StudentService.java` — set `turma` in `updateProfile`
- [ ] Add `GET /api/students/grades` endpoint to `StudentController.java`

**Commit:** `feat(sprint003): B22.1 — add Grade enum, turma field, and grades endpoint`

---

### B22.2 — Frontend dropdown + turma input

**Tests (write first):**
- [ ] `ProfilePage.test.tsx#shouldRenderGradeDropdown` — verify `<select>` with grade options rendered
- [ ] `ProfilePage.test.tsx#shouldRenderTurmaInput` — verify turma text input rendered
- [ ] `ProfilePage.test.tsx#shouldSubmitWithGradeAndTurma` — select grade, type turma, submit, verify API called with both

**Implementation:**
- [ ] Update `types/index.ts` — add `Grade` type, add `turma` to `StudentProfile` and `StudentProfileRequest`
- [ ] Add `getGrades()` to `services/profileService.ts`
- [ ] Update `pages/ProfilePage.tsx`:
  - Replace grade text input with `<select>` populated from `GET /api/students/grades`
  - Add `turma` text input field
  - Update form state and submit handler

**Commit:** `feat(sprint003): B22.2 — add grade dropdown and turma input to profile page`

---

## Execution Order

1. B22.1 — Backend: migration + enum + entity + DTOs + endpoint
2. B22.2 — Frontend: dropdown + turma input (depends on B22.1 API)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B22.1 — add Grade enum, turma field, and grades endpoint` | B22.1 |
| 2 | `feat(sprint003): B22.2 — add grade dropdown and turma input to profile page` | B22.2 |

## Manual Testing

1. **GET grades:** `curl http://localhost:8080/api/students/grades` → returns list of 6 grade options
2. **Update profile:** PUT with `{ "grade": "GRADE_7", "turma": "A", "school": "..." }` → 200
3. **Invalid grade:** PUT with `{ "grade": "INVALID" }` → 400 (rejected by validation)
4. **Frontend:** Profile page shows dropdown for grade, text input for turma
5. **Select grade + type turma → Save** → profile updates

## Definition of Done

- [ ] V4 migration creates turma column and grade CHECK constraint
- [ ] `Grade` enum exists with 6 values
- [ ] `GET /api/students/grades` returns valid options
- [ ] ProfilePage renders `<select>` for grade and text input for turma
- [ ] `./mvnw verify` passes
- [ ] `npm run build && npm test` passes
