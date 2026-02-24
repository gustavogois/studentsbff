# B22 — Grade as Enum Dropdown + Turma Field

**Sprint:** 003
**Priority:** High
**Status:** Planned
**Backlog Item:** B22
**Goal:** Replace the free-text `grade` field with a constrained enum dropdown (7th–12th) and add a new `turma` (class section) free-text field to the student profile.

---

## Context

Currently `grade` is a free-text `VARCHAR(20)` on the `students` table. Students can type anything. This task converts it to a validated enum with fixed options and adds a new `turma` attribute for the class section (e.g. "A", "B", "301").

---

## Grade Options

| Value | Display (EN) | Display (pt-BR) |
|-------|-------------|-----------------|
| `GRADE_7` | 7th Grade | 7º Ano |
| `GRADE_8` | 8th Grade | 8º Ano |
| `GRADE_9` | 9th Grade | 9º Ano |
| `GRADE_10` | 10th Grade | 1º Ano EM |
| `GRADE_11` | 11th Grade | 2º Ano EM |
| `GRADE_12` | 12th Grade | 3º Ano EM |

---

## API Contract

### `GET /api/students/profile`

**Response** (updated):
```json
{
  "id": "uuid",
  "grade": "GRADE_7",
  "turma": "A",
  "school": "Lincoln Middle School",
  "createdAt": "2026-02-24T..."
}
```

### `PUT /api/students/profile`

**Request** (updated):
```json
{
  "grade": "GRADE_7",
  "turma": "A",
  "school": "Lincoln Middle School"
}
```

### `GET /api/students/grades`

New endpoint — returns all valid grade options for the dropdown.

**Response:**
```json
[
  { "value": "GRADE_7", "label": "7th Grade" },
  { "value": "GRADE_8", "label": "8th Grade" },
  { "value": "GRADE_9", "label": "9th Grade" },
  { "value": "GRADE_10", "label": "10th Grade" },
  { "value": "GRADE_11", "label": "11th Grade" },
  { "value": "GRADE_12", "label": "12th Grade" }
]
```

---

## Implementation by task

### B22.1 — Flyway Migration V4

Create `V4__grade_enum_and_turma.sql`:
- Add `turma VARCHAR(100)` column to `students`
- Add `CHECK` constraint on `grade` to restrict to valid enum values
- Existing free-text grades that don't match must be set to `NULL` before the constraint

```sql
-- Nullify any existing grade values that don't match the new enum
UPDATE students SET grade = NULL
  WHERE grade IS NOT NULL
  AND grade NOT IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12');

-- Add turma column
ALTER TABLE students ADD COLUMN turma VARCHAR(100);

-- Add check constraint on grade
ALTER TABLE students ADD CONSTRAINT chk_student_grade
  CHECK (grade IS NULL OR grade IN ('GRADE_7','GRADE_8','GRADE_9','GRADE_10','GRADE_11','GRADE_12'));
```

### B22.2 — Backend: Grade Enum + Entity Changes

**Files to create:**
- `model/Grade.java` — Java enum with `GRADE_7`–`GRADE_12` and a `label` field

**Files to modify:**
- `model/Student.java` — Change `grade` from `String` to `Grade` enum (`@Enumerated(EnumType.STRING)`), add `turma` field
- `dto/StudentProfileRequest.java` — Change `grade` from `String` to `Grade`, add `turma`
- `dto/StudentProfileResponse.java` — Change `grade` from `String` to `Grade`, add `turma`
- `mapper/StudentMapper.java` — Update mapping if needed
- `service/StudentService.java` — Update `updateProfile` to set `turma`
- `controller/StudentController.java` — Add `GET /grades` endpoint

### B22.3 — Backend: Tests

**Tests to write/update:**
- `StudentServiceTest` — update existing tests with Grade enum + turma
- `StudentControllerTest` (if exists) — test `GET /grades` endpoint
- Validation: reject invalid grade values

### B22.4 — Frontend: Type + Service Changes

**Files to modify:**
- `types/index.ts` — Add `Grade` type union, add `turma` to `StudentProfile` and `StudentProfileRequest`
- `services/profileService.ts` — Add `getGrades()` function

### B22.5 — Frontend: ProfilePage Dropdown + Turma Input

**Files to modify:**
- `pages/ProfilePage.tsx`:
  - Replace `grade` text input with `<select>` dropdown populated from `GET /grades`
  - Add `turma` text input field
  - Update form state and submit handler

---

## Definition of Done

- [ ] V4 migration creates `turma` column and adds grade check constraint
- [ ] `Grade` enum exists with 6 values (GRADE_7–GRADE_12)
- [ ] `Student` entity uses `@Enumerated(EnumType.STRING)` for grade
- [ ] `StudentProfileRequest` and `StudentProfileResponse` include `grade` (as enum) and `turma`
- [ ] `GET /api/students/grades` returns the list of valid options
- [ ] ProfilePage renders a `<select>` for grade and a text input for turma
- [ ] `cd backend && ./mvnw verify` passes
- [ ] `cd frontend && npm run build && npm test` passes
