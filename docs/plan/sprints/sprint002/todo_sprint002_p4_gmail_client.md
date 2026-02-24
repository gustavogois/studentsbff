# P4 — Gmail API Client (B20): Implementation Plan

**Sprint:** 002
**Status:** Cancelled — Gmail integration removed from scope (school Google accounts block unverified OAuth apps)
**Backlog item:** B20 (part 3)
**Goal:** Service that fetches recent emails from a student's Gmail using stored OAuth tokens.

---

## Tasks

### P4.1 — Gmail service to fetch emails

**Tests (write first):**
- [ ] `GmailServiceTest#shouldFetchRecentEmails` — mock Gmail API (or RestTemplate), verify returns list of EmailMessage DTOs
- [ ] `GmailServiceTest#shouldFilterByDateRange` — verify only emails from last N days returned
- [ ] `GmailServiceTest#shouldThrowWhenNoTokens` — user has no stored tokens, verify descriptive exception
- [ ] `GmailServiceTest#shouldExtractEmailBody` — verify plain text extraction from email payload
- [ ] `GmailServiceTest#shouldHandleEmptyInbox` — verify returns empty list gracefully

**Implementation:**
- [ ] Add Maven dependency: `com.google.api-client:google-api-client` + `com.google.apis:google-api-services-gmail`
- [ ] Create `dto/EmailMessage.java` — Java Record: `messageId` (Gmail ID), `from`, `subject`, `body` (plain text), `receivedAt` (Instant)
- [ ] Create `service/GmailService.java`:
  - `fetchRecentEmails(User user, int daysBack)` → `List<EmailMessage>`
  - Builds Gmail API client using stored access token
  - Fetches messages list with query filter (e.g., `newer_than:{daysBack}d`)
  - Gets full message content for each, extracts plain text body
  - Returns list of EmailMessage DTOs
- [ ] Create `config/GmailConfig.java` — `@Configuration` with Gmail API application name and default fetch settings (max messages, days back)
- [ ] Add config properties to `application-dev.yml`: `gmail.max-messages: 50`, `gmail.default-days-back: 30`

**Commit:** `feat(sprint002): P4.1 — add Gmail API client service`

---

## Execution Order

1. P4.1 — Single task (depends on P3.1 for stored OAuth tokens)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P4.1 — add Gmail API client service` | P4.1 |

## Manual Testing

1. Ensure user has logged in with Gmail scope (P3.1 done)
2. Write a temporary test endpoint or use a test to call `gmailService.fetchRecentEmails(user, 7)`
3. Verify returns actual emails from the user's Gmail
4. Verify email body is extracted as plain text
5. Verify messageId is populated (needed for dedup in later phases)

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] GmailService can fetch emails using stored tokens
- [ ] Email body extracted as plain text
- [ ] Date filtering works
- [ ] Graceful handling of no tokens / empty inbox
- [ ] No compilation warnings
