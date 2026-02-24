# B25 — Friendly Empty States: Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B25
**Goal:** Replace plain text empty states with a reusable `EmptyState` component featuring icons and call-to-action buttons.

---

## Context

Current empty states are plain text:
- `DashboardPage.tsx` — `"No subjects yet."`
- `SubjectsPage.tsx` — `"No subjects yet. Add one above."`
- `TopicList.tsx` — `"No topics yet. Add one above."`

---

## Tasks

### B25.1 — Create reusable EmptyState component

**Tests (write first):**
- [ ] `EmptyState.test.tsx#shouldRenderMessageAndIcon` — verify message text and icon element rendered
- [ ] `EmptyState.test.tsx#shouldRenderCTAButtonWhenProvided` — verify action button appears with label
- [ ] `EmptyState.test.tsx#shouldNotRenderCTAWhenOmitted` — verify no button when `actionLabel` not provided
- [ ] `EmptyState.test.tsx#shouldCallOnActionWhenClicked` — click CTA button, verify callback fired

**Implementation:**
- [ ] Create `components/EmptyState.tsx`
- [ ] Props: `icon: ReactNode`, `message: string`, `actionLabel?: string`, `onAction?: () => void`
- [ ] Layout: centered vertically, muted icon (text-gray-400, size 48px), message below, optional indigo CTA button
- [ ] Use simple SVG icons or emoji characters (no icon library needed — keep it lightweight)

**Commit:** `feat(sprint003): B25.1 — add reusable EmptyState component`

---

### B25.2 — Replace plain text empty states in existing pages

**Tests (write first):**
- [ ] `DashboardPage.test.tsx#shouldShowEmptyStateWithCTA` — verify EmptyState rendered with "Add your first subject" CTA
- [ ] `SubjectsPage.test.tsx#shouldShowEmptyStateWithCTA` — verify EmptyState with "Add Subject" CTA
- [ ] Update existing empty state test assertions to match new component

**Implementation:**
- [ ] Update `DashboardPage.tsx` — replace `"No subjects yet."` with `<EmptyState>` + CTA linking to subjects page
- [ ] Update `SubjectsPage.tsx` — replace `"No subjects yet. Add one above."` with `<EmptyState>` + CTA that focuses the input
- [ ] Update `TopicList.tsx` — replace `"No topics yet. Add one above."` with `<EmptyState>` (no CTA since form is above)

**Commit:** `feat(sprint003): B25.2 — replace plain text empty states in existing pages`

---

## Execution Order

1. B25.1 — Create EmptyState component + tests
2. B25.2 — Replace empty states in existing pages + update tests

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B25.1 — add reusable EmptyState component` | B25.1 |
| 2 | `feat(sprint003): B25.2 — replace plain text empty states in existing pages` | B25.2 |

## Manual Testing

1. Login → Dashboard with no subjects → see styled empty state with icon and "Add your first subject" button
2. Click CTA → navigates to subjects page
3. Subjects page with no subjects → see styled empty state
4. Add a subject → empty state disappears, subject card shows
5. Subject detail with no topics → see styled empty state for topics

## Definition of Done

- [ ] `EmptyState` component exists with full test coverage
- [ ] All pages use `EmptyState` instead of plain text
- [ ] Each empty state has an appropriate icon and message
- [ ] Dashboard and Subjects empty states have CTA buttons
- [ ] `npm run build` succeeds
- [ ] `npm test` passes
