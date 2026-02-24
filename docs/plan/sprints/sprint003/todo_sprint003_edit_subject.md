# B23 — Edit Subject Name from the UI: Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B23
**Goal:** Allow students to rename a subject directly from the subjects list via inline editing. Backend and frontend service already exist.

---

## Context

- Backend `PUT /api/subjects/{id}` — already implemented
- Frontend `updateSubject(id, data)` in `subjectService.ts` — already implemented
- Only missing: UI interaction to trigger the rename

---

## Tasks

### B23.1 — Add inline edit to SubjectsPage

**Tests (write first):**
- [ ] `SubjectsPage.test.tsx#shouldShowEditButtonOnSubject` — verify edit icon/button rendered for each subject
- [ ] `SubjectsPage.test.tsx#shouldEnterEditModeOnClick` — click edit, verify input appears with current name
- [ ] `SubjectsPage.test.tsx#shouldSaveOnEnter` — type new name, press Enter, verify `updateSubject` called
- [ ] `SubjectsPage.test.tsx#shouldCancelOnEscape` — press Escape, verify original name restored, no API call
- [ ] `SubjectsPage.test.tsx#shouldRefreshListAfterRename` — after successful rename, verify list reloads with new name

**Implementation:**
- [ ] Update `pages/SubjectsPage.tsx`:
  - Add edit state: `const [editingId, setEditingId] = useState<string | null>(null)` and `const [editName, setEditName] = useState("")`
  - Add pencil icon button next to each subject name
  - On click: enter edit mode — replace name text with `<input>` pre-filled with current name
  - On Enter or blur: call `updateSubject(id, { name: editName })`, reload list, exit edit mode
  - On Escape: exit edit mode without saving
- [ ] Auto-focus the input when entering edit mode

**Commit:** `feat(sprint003): B23.1 — add inline subject rename on SubjectsPage`

---

## Execution Order

1. B23.1 — Single task, frontend-only

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B23.1 — add inline subject rename on SubjectsPage` | B23.1 |

## Manual Testing

1. Navigate to Subjects page → each subject shows a pencil/edit icon
2. Click edit → name becomes an editable input
3. Type a new name → press Enter → name updates, list refreshes
4. Click edit → press Escape → original name restored, no change
5. Click edit → click outside (blur) → name saved

## Definition of Done

- [ ] Edit icon visible on each subject in the list
- [ ] Inline input appears on click with current name pre-filled
- [ ] Enter saves, Escape cancels
- [ ] List refreshes after successful rename
- [ ] `npm run build` succeeds
- [ ] `npm test` passes
