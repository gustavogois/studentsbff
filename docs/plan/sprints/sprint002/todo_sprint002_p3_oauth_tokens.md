# P3 — OAuth Token Persistence & Gmail Scope (B20): Implementation Plan

**Sprint:** 002
**Status:** Not Started
**Backlog item:** B20 (part 2)
**Goal:** Expand Google OAuth2 to request Gmail read scope and persist access/refresh tokens for later Gmail API calls.

---

## Background

Currently, the OAuth2 success handler (`OAuth2AuthenticationSuccessHandler`) only extracts profile info (email, name, picture) from the OAuth2User. It does not capture or store the access/refresh tokens needed for Gmail API calls. We need to:

1. Add Gmail read scope to the OAuth2 registration
2. Request offline access to get a refresh token
3. Capture tokens via `OAuth2AuthorizedClientRepository`
4. Persist tokens to the User entity

---

## Tasks

### P3.1 — Persist Google OAuth tokens and add Gmail scope

**Tests (write first):**
- [ ] `OAuth2TokenServiceTest#shouldPersistTokensForUser` — mock UserRepository, verify access token + refresh token + expiry saved
- [ ] `OAuth2TokenServiceTest#shouldUpdateTokensOnReLogin` — user exists with old tokens, verify tokens updated
- [ ] `OAuth2TokenServiceTest#shouldHandleNullRefreshToken` — verify graceful handling when Google doesn't return refresh token (not first login)
- [ ] `OAuth2AuthenticationSuccessHandlerTest#shouldPersistTokensDuringOAuth` — verify handler calls token persistence after extracting OAuth2AuthorizedClient

**Implementation:**
- [ ] Update `application-dev.yml`: add scope `https://www.googleapis.com/auth/gmail.readonly`, add `authorization-grant-type: authorization_code`
- [ ] Create `service/OAuth2TokenService.java` — `persistTokens(User user, String accessToken, String refreshToken, Instant expiry)`, `getValidAccessToken(User user)` (checks expiry, refreshes if needed)
- [ ] Modify `OAuth2AuthenticationSuccessHandler.java`:
  - Inject `OAuth2AuthorizedClientService` to access the OAuth2AuthorizedClient after successful auth
  - Extract access token, refresh token, and expiry from the authorized client
  - Call `OAuth2TokenService.persistTokens()` after find-or-create user
- [ ] Update `SecurityConfig.java` if needed: ensure OAuth2AuthorizedClientService/Repository beans are available
- [ ] Add `application-test.yml` config for test OAuth2 scopes

**Commit:** `feat(sprint002): P3.1 — persist Google OAuth tokens and add Gmail scope`

---

## Execution Order

1. P3.1 — Single task (depends on P2.2 for User entity token fields)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint002): P3.1 — persist Google OAuth tokens and add Gmail scope` | P3.1 |

## Manual Testing

1. Clear existing Google OAuth consent (revoke access at https://myaccount.google.com/permissions)
2. Start backend and login via Google
3. Consent screen should show "Read your Gmail" permission
4. After login, check DB: `SELECT google_access_token, google_refresh_token, google_token_expiry FROM users WHERE email='...'`
5. All three fields should be populated
6. Login again — tokens should be updated (access token may change, refresh token may be null on re-login)

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] Google OAuth consent shows Gmail read scope
- [ ] Access token and refresh token persisted in users table
- [ ] Token expiry tracked
- [ ] Re-login updates tokens without creating duplicate user
- [ ] No compilation warnings
