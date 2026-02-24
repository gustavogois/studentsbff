# P7 — Frontend Events (B20): Implementation Plan

**Sprint:** 002
**Status:** Cancelled — Gmail integration removed from scope (school Google accounts block unverified OAuth apps)
**Backlog item:** B20 (part 6)
**Goal:** Frontend pages for triggering Gmail sync and viewing/managing extracted school events.

---

## Tasks

### P7.1 — TypeScript types and event services

**Tests (write first):**
- [ ] `schoolEventService.test.ts#shouldFetchEvents` — mock API, verify `getSchoolEvents()` returns SchoolEvent[]
- [ ] `schoolEventService.test.ts#shouldDeleteEvent` — mock API, verify `deleteSchoolEvent(id)` sends DELETE
- [ ] `gmailSyncService.test.ts#shouldTriggerSync` — mock API, verify `syncGmail(daysBack)` sends POST and returns SyncResult

**Implementation:**
- [ ] Add types to `types/index.ts`:
  - `SchoolEvent { id, title, eventType, subjectId?, subjectName?, description, eventDate, source, createdAt }`
  - `GmailSyncResult { newEventsCount, skippedDuplicates, totalEmails }`
  - `GmailSyncRequest { daysBack?: number }`
- [ ] Create `services/schoolEventService.ts` — `getSchoolEvents()`, `deleteSchoolEvent(id)`
- [ ] Create `services/gmailSyncService.ts` — `syncGmail(daysBack?: number)`

**Commit:** `feat(sprint002): P7.1 — add school event types and API services`

---

### P7.2 — School events page and Gmail sync UI

**Tests (write first):**
- [ ] `SchoolEventsPage.test.tsx#shouldShowSyncButton` — verify "Sync Gmail" button renders
- [ ] `SchoolEventsPage.test.tsx#shouldListEvents` — mock events, verify events listed with title, type badge, and date
- [ ] `SchoolEventsPage.test.tsx#shouldShowEmptyState` — no events, verify "No events yet" message with sync prompt
- [ ] `SchoolEventsPage.test.tsx#shouldDeleteEvent` — click delete, verify API called
- [ ] `SchoolEventsPage.test.tsx#shouldShowSyncResults` — after sync, verify success message with counts
- [ ] `SchoolEventsPage.test.tsx#shouldShowLoadingDuringSync` — verify loading state while sync in progress

**Implementation:**
- [ ] Create `pages/SchoolEventsPage.tsx`:
  - "Sync Gmail" button at top — triggers sync, shows loading spinner during sync
  - After sync: success toast/banner showing "Found X new events (Y duplicates skipped)"
  - Events list sorted by event_date (upcoming first)
  - Each event card: title, type badge (color-coded: EXAM=red, ASSIGNMENT=blue, DEADLINE=orange, OTHER=gray), date, description, linked subject name if any
  - Delete button per event (with confirmation)
  - Empty state: "No events yet. Click 'Sync Gmail' to import school events from your email."
- [ ] Create `components/EventCard.tsx` — reusable event card component with type badge and delete action
- [ ] Update `components/Layout.tsx` — add "Events" link in navigation (between Subjects and Profile)
- [ ] Update `App.tsx` — add `/events` route inside ProtectedRoute

**Commit:** `feat(sprint002): P7.2 — add school events page with Gmail sync UI`

---

## Execution Order

1. P7.1 — Types + API services (foundation for pages)
2. P7.2 — Events page + sync UI (depends on P7.1)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P7.1 — add school event types and API services` | P7.1 |
| 2 | `feat(sprint002): P7.2 — add school events page with Gmail sync UI` | P7.2 |

## Manual Testing

1. Login and navigate to `/events` → should show empty state with "Sync Gmail" prompt
2. Click "Sync Gmail" → loading spinner → success message with event counts
3. Events list populated with extracted events:
   - Exam events have red badge
   - Assignment events have blue badge
   - Deadline events have orange badge
4. Click delete on an event → confirmation → event removed
5. Click "Sync Gmail" again → should show "0 new events, X duplicates skipped"
6. Verify "Events" link in navigation bar
7. Mobile: verify responsive layout (events list stacks vertically)

## Definition of Done

- [ ] All tests pass (`npm test`)
- [ ] Frontend builds (`npm run build`)
- [ ] Gmail sync button triggers backend sync
- [ ] Events listed with type badges and dates
- [ ] Delete event works
- [ ] Empty state displayed when no events
- [ ] Sync results feedback shown to user
- [ ] "Events" link in navigation
- [ ] No TypeScript errors
