# StudentsBFF — Technical Debt

Track known technical debt items here. Each item should include context, impact, and proposed resolution.

| ID | Description | Severity | Sprint Found | Sprint Resolved |
|----|-------------|----------|--------------|-----------------|
| TD01 | JWT stored in localStorage — vulnerable to XSS. Migrate to httpOnly cookies when hardening security. | Medium | 001 | — |
| TD02 | Flyway disabled in test profile (H2 + ddl-auto: create-drop). Migration SQL not validated in unit tests. Add Testcontainers integration tests to validate Flyway migrations against real PostgreSQL. | Low | 001 | — |
