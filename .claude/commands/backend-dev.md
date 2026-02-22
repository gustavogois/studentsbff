Apply backend development conventions for StudentsBFF (Spring Boot / Java).

## Backend Package Structure (`backend/src/main/java/com/studentsbff/`)
- `model/`      — JPA entities
- `controller/` — REST controllers
- `service/`    — business logic
- `repository/` — JPA repositories
- `dto/`        — request/response DTOs
- `config/`     — Spring config (CORS, Security, JWT)
- `mapper/`     — MapStruct mapper interfaces (entity -> response DTO)

## Backend Conventions
- UUID PKs via `@GeneratedValue(strategy = GenerationType.UUID)`
- `ddl-auto: validate` — schema managed exclusively via Flyway migrations
- Flyway migrations: `backend/src/main/resources/db/migration/`
- Tests use H2 + `create-drop`; Flyway disabled in test profile

### Lombok vs Records

Use **Lombok** for:
- **JPA entities** — JPA requires mutable fields and a no-arg constructor; Records cannot satisfy this
- **Request DTOs** — need a public no-arg constructor + setters for Jackson deserialization
- Preferred Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor`
- JPA entities use `@NoArgsConstructor(access = AccessLevel.PROTECTED)` to satisfy JPA without exposing the no-arg constructor

Use **Java Records** for:
- **Response DTOs** — immutable by design; Jackson 2.12+ deserializes Records natively
- Any class that is a **pure, read-only data carrier** with no mutation after construction

## API Response DTOs

Controllers must **never return JPA entities directly**. Every API endpoint
must return a dedicated response DTO (a Java Record in `dto/`).

Rules:
- Create one `*Response` record per resource in `dto/`
- Map entities -> DTOs in the **controller** using a MapStruct mapper (in `mapper/`)
- Services return domain entities (or `void`); they do not depend on DTOs
- **Exception:** aggregation services may return DTOs directly when there is no single entity to map from

## Logging

### Framework
Use **SLF4J via Lombok's `@Slf4j`** — it generates `private static final Logger log`
automatically. Never declare a Logger field manually.

### Log Levels
| Level   | When to use |
|---------|-------------|
| `ERROR` | Unrecoverable failures; unexpected exceptions that abort the operation |
| `WARN`  | Handled anomalies; e.g. external API returned unexpected data |
| `INFO`  | Significant business events: user created, study plan generated |
| `DEBUG` | Detailed flow useful during development; disabled in production by default |

### Where to Log
- **Services** — log business events (`INFO`) and exceptions (`ERROR`/`WARN`)
- **Controllers** — do NOT add manual logs; Spring's access log covers the HTTP layer
- **Repositories / DTOs / Entities** — do NOT log

### Parameterised Messages (mandatory)
Always use `{}` placeholders — never string concatenation.

```java
// Good
log.info("Creating subject '{}' for student '{}'", name, studentId);
```

### Exception Logging
Pass the exception as the **last argument**.

```java
log.error("Study plan generation failed for student '{}'", studentId, e);
```

## Java Comments & Javadoc
- Every class must have a class-level Javadoc comment explaining its purpose
- Every public method must have a Javadoc comment with `@param`, `@return`, `@throws`
- Add field-level Javadoc on entity fields when the name alone is not self-explanatory
- Do not document Lombok-generated methods — document the fields instead

## Build Commands
- Full check: `cd backend && ./gradlew check`
- Run app: `cd backend && ./gradlew bootRun`
- Compile only: `cd backend && ./gradlew compileJava`
- Run specific test: `cd backend && ./gradlew test --tests "*ServiceTest"`
