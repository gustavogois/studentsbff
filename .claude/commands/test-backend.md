Guide the user on how to test the StudentsBFF backend based on the current state of development.

## Testing Strategy by Layer

### 1. Repository — `@DataJpaTest`
For entities and repositories. Loads only JPA + H2, no web layer.

```java
@DataJpaTest
class SubjectRepositoryTest {
    @Autowired SubjectRepository repo;

    @Test
    void shouldSaveAndFindSubject() {
        var saved = repo.save(new Subject(null, "Mathematics", student));
        assertThat(repo.findById(saved.getId())).as("saved subject").isPresent();
    }
}
```

### 2. Service — Plain JUnit + Mockito
Zero Spring context. Tests business logic in isolation using mocks.

```java
class SubjectServiceTest {
    @Mock SubjectRepository repo;
    @InjectMocks SubjectService service;
}
```

### 3. Controller — `@WebMvcTest`
Tests HTTP layer: status codes, JSON serialization, request validation. Mocks the service layer.

```java
@WebMvcTest(SubjectController.class)
class SubjectControllerTest {
    @Autowired MockMvc mvc;
    @MockBean SubjectService service;
}
```

### 4. Smoke Test — `@SpringBootTest`
Full Spring context with H2. Run before committing a milestone.

## Gradle Commands

| Situation | Command |
|---|---|
| Changed an entity/repository | `./gradlew test --tests "*RepositoryTest"` |
| Changed a service | `./gradlew test --tests "*ServiceTest"` |
| Changed a controller | `./gradlew test --tests "*ControllerTest"` |
| Before committing | `./gradlew check` |

## Assertion Rules

All AssertJ assertions must include a descriptive message via `.as("description")`.

```java
// Good
assertThat(response.name()).as("subject name").isEqualTo("Math");
assertThatThrownBy(() -> service.getSubject(id, userId))
        .as("subject not found should throw")
        .isInstanceOf(EntityNotFoundException.class);
```

## Your Task

Analyze what has been implemented so far and:
1. Report which tests should already exist but are missing
2. Suggest the next test to write based on the most recently completed task
3. If the user asks, write the test directly
