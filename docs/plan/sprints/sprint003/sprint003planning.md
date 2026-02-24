# Sprint 003 — Planning

## Sprint Goal

**"Study Calendar Foundations"** — Enable students to manually register exams, assignments, and deadlines with dates, and visualize them in a calendar view. Also improve existing UX with i18n, grade enum, and subject editing.

## Sprint Metadata

| Field | Value |
|-------|-------|
| Sprint | 003 |
| Start date | 2026-02-24 |
| Goal | Study calendar foundations — manual data entry + calendar view + UX improvements |
| Version | 0.3.0 |
| Constraints | None |

## Retro from Sprint 002

- **Issue:** Too much rework — Gmail integration (B20) was fully implemented then removed because school Google accounts block unverified OAuth apps.
- **Action:** Validate feasibility and scope more carefully before implementing. Keep sprints focused on low-risk, well-understood items. Sprint 003 has no external dependencies or third-party API integrations.

## Selected Backlog Items

| ID | Feature | Priority | Notes |
|----|---------|----------|-------|
| B11 | Manual data entry (exams, assignments, deadlines) | Must | Core MVP path — without this there's no data to build study plans on. Full-stack: new entity, API, frontend page |
| B10 | Calendar view (daily/weekly) | Should | Complements B11 — visualize registered events on a calendar. Frontend-heavy |
| B21 | Internationalisation (i18n) | High | Set up react-i18next with EN, pt-BR, pt-PT. Extract all hardcoded strings. Do early so new features use i18n from the start |
| B22 | Grade as enum dropdown + turma field | High | Replace free-text grade with constrained dropdown (7th–12th). Add turma (class section) field. Full-stack |
| B23 | Edit subject name from the UI | High | Backend PUT endpoint already exists. Frontend service exists. Only missing: UI edit action (inline rename) |
| B24 | Confirm dialogs for all delete actions | High | Frontend-only. Topic delete has no confirm; subject uses raw `window.confirm`. Replace with proper styled modal |
| B25 | Friendly empty states with icons and CTAs | High | Frontend-only. Current empty states are plain text. Add icons and call-to-action buttons |

## Feature Details

### B11 — Manual Data Entry (exams, assignments, deadlines)

**Scope:** CRUD for school events (exams, assignments, deadlines) with date, optional subject link, and description.

**Backend:**
- New `school_events` table (Flyway migration) — different from the removed Gmail version; simpler schema, no `source_email_id`, source is always `MANUAL`
- `SchoolEvent` entity, repository, service, controller
- `POST /api/school-events` — create event
- `GET /api/school-events` — list events for authenticated student (with optional date range filter)
- `PUT /api/school-events/{id}` — update event
- `DELETE /api/school-events/{id}` — delete event

**Frontend:**
- Events page with list view and create/edit form
- Form fields: title, type (exam/assignment/deadline), date, subject (optional dropdown), description
- Delete with confirmation

### B10 — Calendar View

**Scope:** Display school events (from B11) in a calendar layout.

**Frontend:**
- Calendar component (monthly or weekly view)
- Events rendered on their dates
- Click event to view/edit details
- Could use a lightweight calendar library or custom grid

**Backend:** No changes needed — uses the same `GET /api/school-events` with date range filter.

### B21 — Internationalisation (i18n)

**Scope:** Frontend-only. Install `react-i18next`, create translation files (EN, pt-BR, pt-PT), extract all hardcoded strings, add language switcher to profile page.

See `todo_sprint003_i18n.md` for full plan.

### B22 — Grade Enum + Turma

**Scope:** Full-stack. Flyway migration to constrain grade values and add turma column. Backend enum + DTO changes. Frontend dropdown + new field.

See `todo_sprint003_grade_turma.md` for full plan.

### B23 — Edit Subject Name

**Scope:** Frontend-only. Add inline rename action to subjects list. Backend and service layer already exist.

See `todo_sprint003_edit_subject.md` for full plan.

### B24 — Confirm Dialogs for All Delete Actions

**Scope:** Frontend-only. Currently: subject delete uses raw `window.confirm`, topic delete has no confirmation at all. Replace both with a reusable styled confirmation modal component.

**Current state:**
- `SubjectsPage.tsx` — `window.confirm("Delete this subject?")` ✓ (works but ugly)
- `SubjectDetailPage.tsx` — `window.confirm("Delete this subject and all its topics?")` ✓ (works but ugly)
- `TopicList.tsx` — no confirmation at all ✗ (dangerous)
- New school events (B11) — will also need confirm on delete

**Implementation:**
- Create a reusable `ConfirmDialog` component (modal with title, message, confirm/cancel buttons)
- Replace all `window.confirm` calls and add confirm to topic delete
- Apply to new B11 event delete as well

### B25 — Friendly Empty States with Icons and CTAs

**Scope:** Frontend-only. Current empty states are plain text ("No subjects yet.", "No topics yet."). Replace with designed empty states that include an icon/illustration and a clear call-to-action button.

**Current state:**
- `DashboardPage.tsx` — `"No subjects yet."` (plain text)
- `SubjectsPage.tsx` — `"No subjects yet. Add one above."` (plain text)
- `TopicList.tsx` — `"No topics yet. Add one above."` (plain text)
- New school events (B11) — will also need empty state

**Implementation:**
- Create a reusable `EmptyState` component (icon + message + optional CTA button)
- Replace plain text empty states across all pages
- Apply to new B11 events page as well

## Deferred Items

| ID | Feature | Reason | Target Sprint |
|----|---------|--------|---------------|
| B09 | AI-powered study plan generation | Depends on B11 data existing first. Next sprint after calendar foundations are solid | Sprint 004 |
| B05 | Parent-student linking | No study plan to show parents yet | Sprint 004+ |

## Definition of Done

- [ ] Student can create, view, edit, and delete school events (exams, assignments, deadlines)
- [ ] Events have: title, type, date, optional subject link, description
- [ ] Calendar view displays events on their dates
- [ ] All UI strings use i18n translation keys (EN, pt-BR, pt-PT)
- [ ] Language switcher works and persists choice
- [ ] Grade field is a constrained dropdown (7th–12th), turma field added
- [ ] Subject name can be edited from the UI
- [ ] All delete actions show a styled confirmation dialog
- [ ] Empty states have icons and call-to-action buttons
- [ ] All backend tests pass (`./mvnw verify`)
- [ ] Frontend builds and tests pass (`npm run build && npm test`)

## Decisions Made During Refinement

1. **B11 promoted to Sprint 003** — manual data entry is the foundation for the MVP study planner. With Gmail cancelled, it's the only data input path
2. **B10 added** — calendar view complements B11 and gives immediate visual value
3. **B09 deferred to Sprint 004** — depends on B11 data; lower risk to build on solid foundations
4. **Sprint kept moderate (7 items, 2 are small UX polish)** — lesson from Sprint 002 retro: avoid overcommitting. B24/B25 are small frontend-only items that improve overall quality
5. **No external API dependencies** — all items are self-contained (no Gmail, no OpenAI)
6. **i18n done early** — so B11 and B10 frontend code uses translation keys from the start
7. **Sprint version: 0.3.0**

## Suggested Execution Order

```
B21 (i18n setup)          — do first so all new UI code uses t() from the start
B22 (grade enum + turma)  — small full-stack, independent
B23 (edit subject)         — small frontend-only, independent
B24 (confirm dialogs)     — small frontend-only, reusable component
B25 (empty states)         — small frontend-only, reusable component
B11 (manual data entry)   — largest item, full-stack, core of the sprint
B10 (calendar view)       — depends on B11 API being ready
```

**Parallelization:** B22, B23, B24, B25 are independent and can run in parallel. B10 depends on B11. B24/B25 should be done before B11 so the new events page uses them from the start.

## Task Breakdown

7 features, 16 tasks total. See individual task files for TDD details.

| Feature | File | Tasks | Scope |
|---------|------|-------|-------|
| B21 | `todo_sprint003_i18n.md` | B21.1, B21.2, B21.3 | i18n infrastructure + string extraction + language switcher |
| B22 | `todo_sprint003_grade_turma.md` | B22.1, B22.2 | Grade enum + turma backend + frontend |
| B23 | `todo_sprint003_edit_subject.md` | B23.1 | Inline subject rename |
| B24 | `todo_sprint003_b24_confirm_dialog.md` | B24.1, B24.2 | ConfirmDialog component + integration |
| B25 | `todo_sprint003_b25_empty_states.md` | B25.1, B25.2 | EmptyState component + integration |
| B11 | `todo_sprint003_b11_manual_data_entry.md` | B11.1–B11.5 | School events CRUD full-stack |
| B10 | `todo_sprint003_b10_calendar_view.md` | B10.1, B10.2 | Calendar grid + page |

## Global Execution Order

```
B21.1 → B21.2 → B21.3    (i18n — must be first)
B22.1 → B22.2             (grade+turma — independent)
B23.1                      (edit subject — independent)
B24.1 → B24.2             (confirm dialogs — independent, needed before B11.5)
B25.1 → B25.2             (empty states — independent, needed before B11.5)
B11.1 → B11.2 → B11.3 → B11.4 → B11.5    (events CRUD — uses B24+B25)
B10.1 → B10.2             (calendar — depends on B11 API)
```

## Commit Plan (16 commits)

| Order | Commit message | Feature |
|-------|---------------|---------|
| 1 | `feat(sprint003): B21.1 — add i18n infrastructure with 3 locales` | B21 |
| 2 | `feat(sprint003): B21.2 — extract all hardcoded strings to i18n translation keys` | B21 |
| 3 | `feat(sprint003): B21.3 — add language switcher to Profile page` | B21 |
| 4 | `feat(sprint003): B22.1 — add Grade enum, turma field, and grades endpoint` | B22 |
| 5 | `feat(sprint003): B22.2 — add grade dropdown and turma input to profile page` | B22 |
| 6 | `feat(sprint003): B23.1 — add inline subject rename on SubjectsPage` | B23 |
| 7 | `feat(sprint003): B24.1 — add reusable ConfirmDialog component` | B24 |
| 8 | `feat(sprint003): B24.2 — replace window.confirm with ConfirmDialog in all pages` | B24 |
| 9 | `feat(sprint003): B25.1 — add reusable EmptyState component` | B25 |
| 10 | `feat(sprint003): B25.2 — replace plain text empty states in existing pages` | B25 |
| 11 | `feat(sprint003): B11.1 — add school_events migration, entity, and repository` | B11 |
| 12 | `feat(sprint003): B11.2 — add school event service, DTOs, and mapper` | B11 |
| 13 | `feat(sprint003): B11.3 — add school event REST controller` | B11 |
| 14 | `feat(sprint003): B11.4 — add school event frontend types and service` | B11 |
| 15 | `feat(sprint003): B11.5 — add school events page with create, list, and delete` | B11 |
| 16 | `feat(sprint003): B10.2 — add CalendarPage with month navigation and event display` | B10 |

## Next Step

Run `/implement-task` to start implementing tasks following the TDD loop.
