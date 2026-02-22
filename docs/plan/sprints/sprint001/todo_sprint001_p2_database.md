# P2 — Database Schema & Entities: Implementation Plan

**Sprint:** 001
**Status:** Done
**Backlog item:** B02
**Goal:** Flyway V1 migration creates all tables; JPA entities map to them; repositories provide basic CRUD.

---

## Tasks

### P2.1 — Flyway V1 migration

**Tests (write first):**
- [ ] `FlywayMigrationTest#migrationRunsSuccessfully` — `@SpringBootTest` with H2 that verifies context loads and Flyway applies V1 (test profile uses `ddl-auto: validate` after Flyway runs)

**Implementation:**
- [ ] Create `backend/src/main/resources/db/migration/V1__initial_schema.sql` with all 5 tables (users, students, parent_students, subjects, topics)
- [ ] Adjust `users.password` to be nullable (OAuth-only users)
- [ ] Adjust `students.grade` to be nullable (auto-created profiles may not have grade yet)
- [ ] Update `application-test.yml` to enable Flyway for H2 (needed for migration test)

**Commit:** `feat(sprint001): P2.1 — add Flyway V1 migration`

---

### P2.2 — JPA entities and enums

**Tests (write first):**
- [ ] `UserRepositoryTest#shouldSaveAndFindByEmail` — `@DataJpaTest`, save a User, find by email
- [ ] `StudentRepositoryTest#shouldSaveAndFindByUserId` — `@DataJpaTest`, save Student linked to User
- [ ] `SubjectRepositoryTest#shouldFindAllByStudentId` — `@DataJpaTest`, save 2 subjects for a student, verify findAll returns both

**Implementation:**
- [ ] Create `model/User.java` — entity with Lombok, UUID PK, `@Enumerated(STRING)` for role
- [ ] Create `model/Role.java` — enum: `STUDENT`, `PARENT`
- [ ] Create `model/Student.java` — entity with `@OneToOne` to User
- [ ] Create `model/ParentStudent.java` — entity with composite PK (`@IdClass` or `@EmbeddedId`)
- [ ] Create `model/Subject.java` — entity with `@ManyToOne` to Student
- [ ] Create `model/Topic.java` — entity with `@ManyToOne` to Subject, difficulty 1-5
- [ ] Create `repository/UserRepository.java` — `findByEmail(String email)`
- [ ] Create `repository/StudentRepository.java` — `findByUserId(UUID userId)`
- [ ] Create `repository/SubjectRepository.java` — `findAllByStudentId(UUID studentId)`
- [ ] Create `repository/TopicRepository.java` — `findAllBySubjectId(UUID subjectId)`
- [ ] Create `repository/ParentStudentRepository.java` — (deferred usage, but entity + repo exist for schema match)

**Commit:** `feat(sprint001): P2.2 — add JPA entities and repositories`

---

## Execution Order

1. P2.1 — Flyway migration (must exist before entities can be validated)
2. P2.2 — JPA entities and repositories

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P2.1 — add Flyway V1 migration` | P2.1 |
| 2 | `feat(sprint001): P2.2 — add JPA entities and repositories` | P2.2 |

## Manual Testing

1. `docker compose up -d && cd backend && ./mvnw spring-boot:run` — verify Flyway applies V1 migration (check logs for "Successfully applied 1 migration")
2. Connect to PostgreSQL (`docker compose exec postgres psql -U studentsbff`) and verify tables exist: `\dt`

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] Flyway migration applies cleanly on fresh DB
- [ ] All 5 tables created with correct columns, constraints, and foreign keys
- [ ] No compilation warnings
