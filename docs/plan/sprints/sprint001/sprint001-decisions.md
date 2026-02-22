# Sprint 001 — Decisions Log

## Decision 1: H2 PostgreSQL Compatibility Mode for Tests
**Context:** Tests use H2 in-memory database but Flyway migration uses PostgreSQL syntax.
**Decision:** Use `MODE=PostgreSQL` in H2 connection URL. The migration SQL uses `gen_random_uuid()` which H2 supports in PostgreSQL mode.
**Alternatives:** Could use Testcontainers with real PostgreSQL, but adds complexity for sprint 001.

## Decision 2: JWT Token Storage in localStorage
**Context:** Need to persist JWT across page refreshes.
**Decision:** Store JWT in `localStorage`. Simpler than httpOnly cookies for this MVP stage.
**Trade-off:** Vulnerable to XSS but acceptable for walking skeleton. Can migrate to httpOnly cookies later.

## Decision 3: Controller Student Resolution Pattern
**Context:** Controllers need to resolve the current student from the authenticated user.
**Decision:** Controllers extract email from `SecurityContextHolder`, look up User by email, then Student by user ID. Each controller has a private `getCurrentStudentId()` helper method.
**Alternative:** Could use a custom `@CurrentStudent` annotation with argument resolver, but that's over-engineering for sprint 001.

## Decision 4: SubjectService Returns DTOs
**Context:** Whether services should return entities or DTOs.
**Decision:** Services return response DTOs directly (using MapStruct internally). This keeps controllers thin.
**Alternative:** Services return entities, controllers do mapping. Chose DTO approach for cleaner controller code.

## Decision 5: Topic Difficulty Default Value
**Context:** TopicRequest.difficulty is optional.
**Decision:** Default to 3 (middle of 1-5 range) when not provided. Set in service layer.

## Decision 6: @WebMvcTest with @WithMockUser for Controller Tests
**Context:** How to test controllers with JWT security.
**Decision:** Use `@WebMvcTest` with `@WithMockUser(username = "test@example.com")` for authenticated tests. Mock UserRepository and StudentRepository to resolve the current student.
**Alternative:** Full `@SpringBootTest` with `@AutoConfigureMockMvc`, but that's heavier for unit tests.

## Decision 7: Frontend API Client Base URL
**Context:** Whether to use proxy or direct URL for API calls.
**Decision:** Use `import.meta.env.VITE_API_URL || ""` — empty string in dev (uses Vite proxy), full URL in production.

## Decision 8: ParentStudent Composite Key
**Context:** How to model the parent-student many-to-many relationship.
**Decision:** Use `@EmbeddedId` with a separate `ParentStudentId` class. Cleaner than `@IdClass` for composite keys.

## Decision 9: Flyway Disabled in Test Profile
**Context:** Whether to use Flyway or JPA auto-DDL for tests.
**Decision:** Disable Flyway in test profile, use `ddl-auto: create-drop`. H2 in PostgreSQL mode does not support all Flyway migration syntax perfectly. Hibernate auto-DDL generates compatible schema from JPA entities.
**Trade-off:** Migration SQL is not validated during unit tests, but integration tests with Testcontainers can cover that in future sprints.

## Decision 10: Spring Boot Version 3.4.3
**Context:** Which Spring Boot 3.x version to use.
**Decision:** 3.4.3 (latest stable at time of sprint). Uses Spring Framework 6.2.3 and Spring Security 6.4.3.
