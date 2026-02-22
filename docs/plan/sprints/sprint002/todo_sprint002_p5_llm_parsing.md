# P5 — LLM Provider & Email Parsing (B20): Implementation Plan

**Sprint:** 002
**Status:** Not Started
**Backlog item:** B20 (part 4)
**Goal:** AI service that parses email content into structured school events using OpenAI GPT-4o, with an abstraction layer for future provider switching.

---

## Tasks

### P5.1 — LLMProvider interface and OpenAI implementation

**Tests (write first):**
- [ ] `OpenAiProviderTest#shouldSendCorrectRequestFormat` — mock RestTemplate, verify request body includes model, messages, response_format
- [ ] `OpenAiProviderTest#shouldParseApiResponse` — mock API response JSON, verify parsed string returned
- [ ] `OpenAiProviderTest#shouldThrowOnApiError` — mock 4xx/5xx response, verify descriptive exception
- [ ] `OpenAiProviderTest#shouldIncludeSystemAndUserMessages` — verify both system prompt and user content sent

**Implementation:**
- [ ] Create `ai/LLMProvider.java` — interface: `String complete(String systemPrompt, String userMessage)`
- [ ] Create `ai/OpenAiProvider.java` — `@Service` implementing LLMProvider:
  - Uses `RestTemplate` to call `https://api.openai.com/v1/chat/completions`
  - Model: `gpt-4o` (configurable)
  - Sends system prompt + user message
  - Parses response and returns content string
- [ ] Create `ai/OpenAiConfig.java` — `@ConfigurationProperties("openai")`: `apiKey`, `model` (default "gpt-4o"), `maxTokens` (default 2000)
- [ ] Add config to `application-dev.yml`: `openai.api-key: ${OPENAI_API_KEY}`, `openai.model: gpt-4o`
- [ ] Add test config to `application-test.yml`: mock OpenAI key

**Commit:** `feat(sprint002): P5.1 — add LLMProvider interface and OpenAI implementation`

---

### P5.2 — Email parsing service

**Tests (write first):**
- [ ] `EmailParsingServiceTest#shouldExtractExamEvent` — mock LLM returning JSON with exam event, verify ParsedSchoolEvent mapped correctly
- [ ] `EmailParsingServiceTest#shouldExtractMultipleEvents` — mock LLM returning array with 2 events, verify both parsed
- [ ] `EmailParsingServiceTest#shouldHandleNoEventsFound` — mock LLM returning empty array, verify empty list returned
- [ ] `EmailParsingServiceTest#shouldMapEventTypesCorrectly` — verify EXAM, ASSIGNMENT, DEADLINE, OTHER mapped to enum
- [ ] `EmailParsingServiceTest#shouldIncludeStudentSubjectsInPrompt` — verify subject names passed to LLM for context matching
- [ ] `EmailParsingServiceTest#shouldHandleMalformedLLMResponse` — mock LLM returning invalid JSON, verify graceful fallback

**Implementation:**
- [ ] Create `dto/ParsedSchoolEvent.java` — Java Record: `title`, `eventType` (String), `eventDate` (String, ISO format), `description`, `relatedSubjectName` (nullable)
- [ ] Create `service/EmailParsingService.java`:
  - `parseEmails(List<EmailMessage> emails, List<String> subjectNames)` → `List<ParsedSchoolEvent>`
  - Builds system prompt: "You are a school email parser. Extract school events (exams, assignments, deadlines) from emails. Return JSON array..."
  - Includes student's subject names for AI to match against
  - Sends each email (or batch) to LLMProvider
  - Parses JSON response into ParsedSchoolEvent list
  - Handles malformed responses gracefully
- [ ] Prompt template stored as constant or resource file

**Commit:** `feat(sprint002): P5.2 — add email parsing service with AI extraction`

---

## Execution Order

1. P5.1 — LLMProvider + OpenAI (standalone, no dependencies beyond Spring context)
2. P5.2 — Email parsing service (depends on P5.1 LLMProvider + P4.1 EmailMessage DTO)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P5.1 — add LLMProvider interface and OpenAI implementation` | P5.1 |
| 2 | `feat(sprint002): P5.2 — add email parsing service with AI extraction` | P5.2 |

## Manual Testing

1. Set `OPENAI_API_KEY` environment variable
2. Write a test that sends a sample school email to EmailParsingService
3. Verify response contains structured events with correct types and dates
4. Test with a non-school email — verify returns empty events list
5. Test with multiple events in one email — verify all extracted

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] LLMProvider interface allows easy provider switching
- [ ] OpenAI GPT-4o integration works with correct API format
- [ ] Email parsing extracts structured events from email text
- [ ] Handles edge cases: no events, malformed AI response, non-school emails
- [ ] Subject name matching works (AI suggests related subject)
- [ ] No compilation warnings
