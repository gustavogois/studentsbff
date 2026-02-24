# B10 — Calendar View: Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B10
**Goal:** Display school events on a monthly calendar grid so students can visualize their exams, assignments, and deadlines at a glance.

---

## Context

Depends on B11 (manual data entry) — the calendar displays events from `GET /api/school-events?from=&to=`. No backend changes needed.

---

## Tasks

### B10.1 — Calendar grid component

**Tests (write first):**
- [ ] `Calendar.test.tsx#shouldRenderCurrentMonthGrid` — verify correct number of days rendered for current month
- [ ] `Calendar.test.tsx#shouldHighlightToday` — verify today's date has distinct styling
- [ ] `Calendar.test.tsx#shouldNavigateToNextMonth` — click next arrow, verify month label changes
- [ ] `Calendar.test.tsx#shouldNavigateToPreviousMonth` — click prev arrow, verify month label changes
- [ ] `Calendar.test.tsx#shouldRenderEventsOnCorrectDays` — pass events, verify they appear on the right date cells
- [ ] `Calendar.test.tsx#shouldCallOnEventClickWhenEventClicked` — click an event, verify callback fired with event data

**Implementation:**
- [ ] Create `components/Calendar.tsx`:
  - Props: `events: SchoolEvent[]`, `onEventClick?: (event: SchoolEvent) => void`, `onMonthChange?: (year: number, month: number) => void`
  - Monthly grid: 7 columns (Sun–Sat or Mon–Sun), rows for weeks
  - Month/year header with prev/next navigation arrows
  - Events shown as colored dots or small badges on their date cells
  - Color by event type: EXAM (red), ASSIGNMENT (blue), DEADLINE (orange)
  - Today highlighted with ring/background
  - Pure Tailwind CSS — no external calendar library

**Commit:** `feat(sprint003): B10.1 — add Calendar grid component`

---

### B10.2 — Calendar page integration

**Tests (write first):**
- [ ] `CalendarPage.test.tsx#shouldFetchEventsForCurrentMonth` — verify API called with current month range
- [ ] `CalendarPage.test.tsx#shouldRefetchOnMonthChange` — navigate month, verify new API call
- [ ] `CalendarPage.test.tsx#shouldShowLoadingState` — verify loading indicator while fetching
- [ ] `CalendarPage.test.tsx#shouldNavigateToEventOnClick` — click event, verify navigation or detail shown

**Implementation:**
- [ ] Create `pages/CalendarPage.tsx`:
  - Fetches events for displayed month via `getSchoolEvents(from, to)`
  - Passes events to `<Calendar>` component
  - On month change: refetch events for new month range
  - On event click: navigate to events page or show event detail
- [ ] Update `App.tsx` — add `/calendar` route
- [ ] Update `Layout.tsx` — add "Calendar" nav link

**Commit:** `feat(sprint003): B10.2 — add CalendarPage with month navigation and event display`

---

## Execution Order

1. B10.1 — Calendar grid component (standalone, can be tested with mock data)
2. B10.2 — CalendarPage integration (depends on B11 API + B10.1 component)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B10.1 — add Calendar grid component` | B10.1 |
| 2 | `feat(sprint003): B10.2 — add CalendarPage with month navigation and event display` | B10.2 |

## Manual Testing

1. Navigate to `/calendar` → see current month grid
2. Verify today is highlighted
3. Create events on different dates via Events page
4. Return to calendar → events appear as dots/badges on their dates
5. Click prev/next → month changes, events reload
6. Click an event → navigates to event detail or events page
7. Verify EXAM/ASSIGNMENT/DEADLINE have different colors

## Definition of Done

- [ ] Calendar renders a correct monthly grid with day numbers
- [ ] Today is visually highlighted
- [ ] Month navigation (prev/next) works
- [ ] Events appear on their correct dates
- [ ] Events are color-coded by type
- [ ] Clicking an event triggers navigation
- [ ] `npm run build` succeeds
- [ ] `npm test` passes
