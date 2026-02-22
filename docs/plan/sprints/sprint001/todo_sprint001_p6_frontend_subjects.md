# P6 — Frontend Subjects & Dashboard: Implementation Plan

**Sprint:** 001
**Status:** Done
**Backlog item:** B06 (continued)
**Goal:** Student dashboard shows subjects; subjects page allows full CRUD; topics CRUD within each subject.

---

## Tasks

### P6.1 — Subject and topic services

**Tests (write first):**
- [ ] `subjectService.test.ts#shouldFetchSubjects` — mock API, verify `getSubjects()` returns Subject[]
- [ ] `subjectService.test.ts#shouldCreateSubject` — mock API, verify `createSubject({ name })` sends POST
- [ ] `subjectService.test.ts#shouldDeleteSubject` — mock API, verify `deleteSubject(id)` sends DELETE
- [ ] `topicService.test.ts#shouldFetchTopics` — mock API, verify `getTopics(subjectId)` returns Topic[]
- [ ] `topicService.test.ts#shouldCreateTopic` — mock API, verify `createTopic(subjectId, { name, difficulty })` sends POST

**Implementation:**
- [ ] Create `frontend/src/services/subjectService.ts` — functions: `getSubjects()`, `getSubject(id)`, `createSubject(data)`, `updateSubject(id, data)`, `deleteSubject(id)`
- [ ] Create `frontend/src/services/topicService.ts` — functions: `getTopics(subjectId)`, `createTopic(subjectId, data)`, `updateTopic(subjectId, id, data)`, `deleteTopic(subjectId, id)`

**Commit:** `feat(sprint001): P6.1 — add subject and topic API services`

---

### P6.2 — Dashboard page

**Tests (write first):**
- [ ] `DashboardPage.test.tsx#shouldShowWelcomeMessage` — render with mocked user, verify greeting with user name
- [ ] `DashboardPage.test.tsx#shouldShowSubjectCards` — mock subjects, verify cards render with subject names
- [ ] `DashboardPage.test.tsx#shouldShowEmptyState` — mock empty subjects, verify "No subjects yet" message

**Implementation:**
- [ ] Create `frontend/src/pages/DashboardPage.tsx` — shows greeting ("Hello, {name}!"), lists subjects as cards, links to `/subjects` page
- [ ] Create `frontend/src/components/SubjectCard.tsx` — displays subject name, topic count, links to subject detail

**Commit:** `feat(sprint001): P6.2 — add Dashboard page and SubjectCard`

---

### P6.3 — Subjects page (list + create + delete)

**Tests (write first):**
- [ ] `SubjectsPage.test.tsx#shouldListSubjects` — mock API, verify subjects listed
- [ ] `SubjectsPage.test.tsx#shouldCreateSubject` — fill form, submit, verify API called
- [ ] `SubjectsPage.test.tsx#shouldDeleteSubject` — click delete, confirm, verify API called
- [ ] `SubjectsPage.test.tsx#shouldShowEmptyState` — no subjects, verify empty state message

**Implementation:**
- [ ] Create `frontend/src/pages/SubjectsPage.tsx` — lists all subjects, "Add Subject" form (input + button), delete button per subject, edit inline or navigate to detail
- [ ] Add route `/subjects` in App.tsx

**Commit:** `feat(sprint001): P6.3 — add Subjects page with CRUD`

---

### P6.4 — Subject detail page (topics CRUD)

**Tests (write first):**
- [ ] `SubjectDetailPage.test.tsx#shouldShowSubjectName` — mock subject, verify name displayed
- [ ] `SubjectDetailPage.test.tsx#shouldListTopics` — mock topics, verify topics listed with difficulty
- [ ] `SubjectDetailPage.test.tsx#shouldCreateTopic` — fill form, submit, verify API called
- [ ] `SubjectDetailPage.test.tsx#shouldDeleteTopic` — click delete, verify API called

**Implementation:**
- [ ] Create `frontend/src/pages/SubjectDetailPage.tsx` — shows subject name, lists topics with difficulty indicator (1-5), "Add Topic" form (name + difficulty slider/select), edit/delete per topic
- [ ] Create `frontend/src/components/TopicList.tsx` — reusable topic list with difficulty display
- [ ] Add route `/subjects/:id` in App.tsx

**Commit:** `feat(sprint001): P6.4 — add Subject detail page with topics CRUD`

---

## Execution Order

1. P6.1 — API services (foundation for pages)
2. P6.2 — Dashboard page (depends on P6.1 for subject data)
3. P6.3 — Subjects page (depends on P6.1)
4. P6.4 — Subject detail page (depends on P6.1, P6.3 for navigation)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P6.1 — add subject and topic API services` | P6.1 |
| 2 | `feat(sprint001): P6.2 — add Dashboard page and SubjectCard` | P6.2 |
| 3 | `feat(sprint001): P6.3 — add Subjects page with CRUD` | P6.3 |
| 4 | `feat(sprint001): P6.4 — add Subject detail page with topics CRUD` | P6.4 |

## Manual Testing

1. Login with Google
2. **Dashboard (empty):** `/dashboard` shows "Hello, {name}!" and "No subjects yet"
3. **Add subject:** Navigate to `/subjects`, type "Mathematics", click Add → subject appears in list
4. **View subject:** Click on "Mathematics" → navigates to `/subjects/{id}`
5. **Add topic:** Type "Fractions", set difficulty to 4, click Add → topic appears
6. **Edit topic:** Change difficulty to 2, save → updated
7. **Delete topic:** Click delete → topic removed
8. **Delete subject:** Back to `/subjects`, click delete on "Mathematics" → removed
9. **Dashboard (with data):** Add a subject + topics, go to `/dashboard` → subject card shows with topic count

## Definition of Done

- [ ] All tests pass (`npm test`)
- [ ] Frontend builds (`npm run build`)
- [ ] Dashboard shows subjects overview
- [ ] Subjects page supports full CRUD
- [ ] Subject detail page supports topic CRUD
- [ ] Empty states are handled gracefully
- [ ] No TypeScript errors
