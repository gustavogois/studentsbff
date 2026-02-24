# B24 — Confirm Dialogs for All Delete Actions: Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B24
**Goal:** Replace raw `window.confirm` calls and missing confirmations with a reusable styled `ConfirmDialog` component across all delete actions.

---

## Context

Current state:
- `SubjectsPage.tsx` — `window.confirm("Delete this subject?")` (works but unstyled)
- `SubjectDetailPage.tsx` — `window.confirm("Delete this subject and all its topics?")` (works but unstyled)
- `TopicList.tsx` — **no confirmation at all** (dangerous)
- New school events (B11) — will use ConfirmDialog from the start

---

## Tasks

### B24.1 — Create reusable ConfirmDialog component

**Tests (write first):**
- [ ] `ConfirmDialog.test.tsx#shouldRenderTitleAndMessage` — verify title and message text displayed when `open={true}`
- [ ] `ConfirmDialog.test.tsx#shouldNotRenderWhenClosed` — verify nothing rendered when `open={false}`
- [ ] `ConfirmDialog.test.tsx#shouldCallOnConfirm` — click confirm button, verify `onConfirm` callback fired
- [ ] `ConfirmDialog.test.tsx#shouldCallOnCancel` — click cancel button, verify `onCancel` callback fired

**Implementation:**
- [ ] Create `components/ConfirmDialog.tsx` — modal overlay with title, message, Cancel + Confirm buttons
- [ ] Props: `open: boolean`, `title: string`, `message: string`, `onConfirm: () => void`, `onCancel: () => void`, `confirmLabel?: string` (default: "Delete"), `variant?: "danger" | "default"`
- [ ] Use Tailwind for styling: centered modal, backdrop overlay, red confirm button for danger variant
- [ ] All strings use `t()` for i18n (depends on B21 being done first, otherwise use plain English initially)

**Commit:** `feat(sprint003): B24.1 — add reusable ConfirmDialog component`

---

### B24.2 — Replace window.confirm in existing pages

**Tests (write first):**
- [ ] `SubjectsPage.test.tsx#shouldShowConfirmDialogOnDelete` — click delete, verify ConfirmDialog appears
- [ ] `SubjectsPage.test.tsx#shouldDeleteSubjectOnConfirm` — confirm dialog, verify API called
- [ ] `SubjectsPage.test.tsx#shouldCancelDeleteOnCancel` — cancel dialog, verify API NOT called
- [ ] `SubjectDetailPage.test.tsx#shouldShowConfirmDialogOnDeleteSubject` — same for subject detail page
- [ ] `TopicList.test.tsx#shouldShowConfirmDialogOnDeleteTopic` — verify topic delete now shows confirmation

**Implementation:**
- [ ] Update `SubjectsPage.tsx` — replace `window.confirm` with `ConfirmDialog` state management
- [ ] Update `SubjectDetailPage.tsx` — replace `window.confirm` for subject delete with `ConfirmDialog`
- [ ] Update `TopicList.tsx` — add `ConfirmDialog` for topic delete (currently has no confirmation)
- [ ] Pattern: `const [deleteTarget, setDeleteTarget] = useState<string | null>(null)` — setting ID opens dialog, confirm triggers delete + clears, cancel clears

**Commit:** `feat(sprint003): B24.2 — replace window.confirm with ConfirmDialog in all pages`

---

## Execution Order

1. B24.1 — Create ConfirmDialog component + tests
2. B24.2 — Replace window.confirm in existing pages + update tests

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B24.1 — add reusable ConfirmDialog component` | B24.1 |
| 2 | `feat(sprint003): B24.2 — replace window.confirm with ConfirmDialog in all pages` | B24.2 |

## Manual Testing

1. Navigate to Subjects page → click delete on a subject → styled dialog appears
2. Click Cancel → dialog closes, subject still exists
3. Click Delete → subject removed, list refreshes
4. Navigate to Subject Detail → click Delete Subject → styled dialog appears
5. Navigate to Subject Detail → click delete on a topic → styled dialog now appears (previously had no confirm)
6. Verify dialog is centered, has backdrop, red delete button

## Definition of Done

- [ ] `ConfirmDialog` component exists with full test coverage
- [ ] All delete actions in SubjectsPage, SubjectDetailPage, TopicList use ConfirmDialog
- [ ] No remaining `window.confirm` calls in the codebase
- [ ] Topic delete now has confirmation (previously missing)
- [ ] `npm run build` succeeds
- [ ] `npm test` passes
