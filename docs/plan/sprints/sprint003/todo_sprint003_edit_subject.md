# B23 — Edit Subject Name from the UI

**Sprint:** 003
**Priority:** High
**Status:** Planned
**Backlog Item:** B23
**Goal:** Allow students to rename a subject directly from the subjects list or detail page. The backend `PUT /api/subjects/{id}` endpoint and the frontend `updateSubject` service already exist — only the UI interaction is missing.

---

## Context

The backend already exposes `PUT /api/subjects/{id}` and the frontend service `updateSubject()` in `subjectService.ts` is implemented. The only gap is that no UI element triggers the rename flow.

---

## Existing infrastructure

| Layer | Status | File |
|-------|--------|------|
| Backend `PUT /api/subjects/{id}` | Done | `SubjectController.java` |
| Frontend service `updateSubject()` | Done | `services/subjectService.ts` |
| UI edit action | **Missing** | — |

---

## Implementation

### B23.1 — Add edit action to SubjectCard or SubjectsPage

**Option A — Inline rename on SubjectsPage** (recommended):
- Add an edit (pencil) icon button next to each subject name
- On click, replace the subject name with an `<input>` pre-filled with the current name
- On Enter or blur, call `updateSubject(id, { name })` and refresh the list
- On Escape, cancel editing

**Option B — Edit on SubjectDetailPage:**
- Add an edit button next to the subject title on the detail page
- Same inline edit behavior

**Files to modify:**
- `pages/SubjectsPage.tsx` or `components/SubjectCard.tsx` — add edit icon + inline input state
- `pages/__tests__/SubjectsPage.test.tsx` — add test for rename flow

---

## Definition of Done

- [ ] User can click an edit action on a subject to rename it
- [ ] Rename calls `PUT /api/subjects/{id}` via the existing service
- [ ] Subject list refreshes with the new name after save
- [ ] Escape cancels the edit without saving
- [ ] Test covers the rename flow
- [ ] `cd frontend && npm run build && npm test` passes
