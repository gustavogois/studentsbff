# Sprint 003 — Decisions Log

Decisions made during autonomous implementation of sprint 003 tasks.

## B21 — i18n

1. **i18n mock strategy:** Using `vi.mock('react-i18next')` in test setup to return keys as-is from `t()`. This avoids brittle assertions on translated text and keeps tests locale-agnostic.
2. **Translation key namespace:** Using flat dot-notation keys like `nav.dashboard`, `subjects.title`. No nested JSON structure — keeps things simple.
3. **pt-PT translations:** Starting with copies of pt-BR where identical, diverging only where European Portuguese differs.

## B22 — Grade + Turma

4. **Flyway migration numbering:** Using V4 for grade enum + turma since V3 was the Gmail removal migration.
5. **Grade enum serialization:** Using `@Enumerated(EnumType.STRING)` for Grade enum. The column stays VARCHAR(20) but gets a CHECK constraint.
6. **Backward compatibility:** The grade column remains nullable. Existing data with free-text values gets set to NULL via the migration.

## B23 — Edit Subject

7. **Save trigger:** Save on Enter keypress and on blur (clicking away). Escape cancels. No separate save button to keep UI clean.

## B24 — Confirm Dialog

8. **Implementation approach:** Using a portal-based modal with backdrop. Tailwind only, no animation library.
9. **Variant:** Default variant is "danger" (red confirm button) since all current use cases are delete actions.

## B25 — Empty States

10. **Icons:** Using inline SVG icons (no icon library dependency). Simple outline-style icons.
11. **CTA buttons:** Dashboard empty state navigates to /subjects. SubjectsPage empty state focuses the input field. TopicList has no CTA since the form is directly above.

## B11 — Manual Data Entry

12. **Migration number:** Using V5 for school_events since V4 is used by B22 (grade+turma). V2 created and V3 dropped the old Gmail-based school_events, so this is a fresh table with a simpler schema.
13. **EventType enum:** Three values: EXAM, ASSIGNMENT, DEADLINE. Using `@Enumerated(EnumType.STRING)`.
14. **Subject link:** Optional @ManyToOne to Subject with ON DELETE SET NULL — if a subject is deleted, events keep their other data.
15. **Date range filter:** Using Spring Data JPA method naming: `findAllByStudentIdAndEventDateBetween`.

## B10 — Calendar View

16. **Calendar library:** Pure Tailwind CSS grid — no external calendar library. Keeps bundle size minimal.
17. **Week start:** Sunday (US convention). Can be made configurable later via i18n.
18. **Event display:** Colored dots on calendar cells. Color by type: EXAM=red, ASSIGNMENT=blue, DEADLINE=orange.
