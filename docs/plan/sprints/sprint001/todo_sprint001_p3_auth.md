# P3 — Google OAuth2 Authentication: Implementation Plan

**Sprint:** 001
**Status:** Not Started
**Backlog item:** B03
**Goal:** User can login with Google; backend issues JWT session token; first-time login auto-creates user + student profile.

---

## Auth Flow (for reference)

```
Browser                     Backend                         Google
  │                            │                               │
  ├─ GET /oauth2/authorize ───►│                               │
  │  /google                   ├─ redirect ───────────────────►│
  │                            │                               │
  │◄─────────────── redirect ──┤◄── auth code ─────────────────┤
  │  to /login/oauth2/code     │                               │
  │  /google                   ├─ exchange code for tokens ───►│
  │                            │◄── access_token + id_token ───┤
  │                            │                               │
  │                            │ (extract email, name, picture)│
  │                            │ (find-or-create User+Student) │
  │                            │ (issue JWT session token)     │
  │                            │                               │
  │◄── redirect to frontend ───┤                               │
  │    with ?token=JWT         │                               │
```

---

## Tasks

### P3.1 — JWT service (issue + validate tokens)

**Tests (write first):**
- [ ] `JwtServiceTest#shouldGenerateValidToken` — generate token for a user, verify it's not null and not expired
- [ ] `JwtServiceTest#shouldExtractUserIdFromToken` — generate token, extract userId, verify it matches
- [ ] `JwtServiceTest#shouldExtractEmailFromToken` — generate token, extract email, verify it matches
- [ ] `JwtServiceTest#shouldRejectExpiredToken` — generate token with -1ms expiry, verify validation fails
- [ ] `JwtServiceTest#shouldRejectTamperedToken` — modify token payload, verify validation fails

**Implementation:**
- [ ] Create `config/JwtProperties.java` — `@ConfigurationProperties("jwt")` with `secret` and `expiration` fields
- [ ] Create `service/JwtService.java` — `generateToken(User user)`, `extractUserId(String token)`, `extractEmail(String token)`, `isTokenValid(String token)`
- [ ] Uses `io.jsonwebtoken` (jjwt) library
- [ ] JWT payload: `sub` = userId, `email`, `role`, `iat`, `exp`

**Commit:** `feat(sprint001): P3.1 — add JWT service`

---

### P3.2 — JWT authentication filter

**Tests (write first):**
- [ ] `JwtAuthenticationFilterTest#shouldPassThroughWhenNoAuthHeader` — request without Authorization header passes through filter chain
- [ ] `JwtAuthenticationFilterTest#shouldAuthenticateWithValidToken` — request with valid Bearer token sets SecurityContext
- [ ] `JwtAuthenticationFilterTest#shouldRejectInvalidToken` — request with invalid Bearer token does not set SecurityContext

**Implementation:**
- [ ] Create `config/JwtAuthenticationFilter.java` — extends `OncePerRequestFilter`
- [ ] Extracts Bearer token from `Authorization` header
- [ ] Validates via `JwtService`, loads user from DB, sets `UsernamePasswordAuthenticationToken` in SecurityContext
- [ ] Skips filter for OAuth2 endpoints

**Commit:** `feat(sprint001): P3.2 — add JWT authentication filter`

---

### P3.3 — OAuth2 success handler and Security config

**Tests (write first):**
- [ ] `OAuth2AuthenticationSuccessHandlerTest#shouldCreateUserOnFirstLogin` — mock OAuth2User with email/name, verify User + Student created in DB
- [ ] `OAuth2AuthenticationSuccessHandlerTest#shouldReuseExistingUser` — pre-create user with same email, verify no duplicate created
- [ ] `OAuth2AuthenticationSuccessHandlerTest#shouldRedirectWithJwtToken` — verify redirect URL contains `?token=` parameter
- [ ] `SecurityConfigTest#publicEndpointsShouldBeAccessible` — `@WebMvcTest`, verify `/actuator/health` returns 200 without auth
- [ ] `SecurityConfigTest#protectedEndpointsShouldReturn401` — `@WebMvcTest`, verify `/api/subjects` returns 401 without auth

**Implementation:**
- [ ] Create `config/OAuth2AuthenticationSuccessHandler.java` — implements `AuthenticationSuccessHandler`
  - Extracts email, name, picture from OAuth2User attributes
  - Calls `UserService.findOrCreateOAuthUser(email, name, avatarUrl)`
  - Auto-creates Student profile with null grade/school on first login
  - Generates JWT via `JwtService`
  - Redirects to `${frontend.url}/oauth/callback?token={jwt}`
- [ ] Create `service/UserService.java` — `findOrCreateOAuthUser(String email, String name, String avatarUrl)` that creates User with role=STUDENT + Student profile if not exists
- [ ] Create `config/SecurityConfig.java` — Spring Security filter chain:
  - Public: `/actuator/health`, `/oauth2/**`, `/login/oauth2/**`
  - All other requests require authentication
  - OAuth2 login with Google provider + custom success handler
  - JWT filter added before `UsernamePasswordAuthenticationFilter`
  - CORS configured from `frontend.url` property
  - CSRF disabled (stateless JWT)
- [ ] Add `application-dev.yml` Google OAuth2 config (client-id/secret from env vars)
- [ ] Add `frontend.url` property for CORS + redirect

**Commit:** `feat(sprint001): P3.3 — add OAuth2 success handler and Security config`

---

### P3.4 — GET /api/users/me endpoint

**Tests (write first):**
- [ ] `UserControllerTest#shouldReturnCurrentUser` — `@WebMvcTest`, mock authenticated user, verify 200 with user data
- [ ] `UserControllerTest#shouldReturn401WhenNotAuthenticated` — `@WebMvcTest`, no auth, verify 401

**Implementation:**
- [ ] Create `dto/UserResponse.java` — Java Record: `id`, `name`, `email`, `role`, `avatarUrl`
- [ ] Create `mapper/UserMapper.java` — MapStruct mapper: `User` → `UserResponse`
- [ ] Create `controller/UserController.java` — `GET /api/users/me`, extracts user from SecurityContext, returns UserResponse

**Commit:** `feat(sprint001): P3.4 — add GET /api/users/me endpoint`

---

## Execution Order

1. P3.1 — JWT service (standalone, no dependencies)
2. P3.2 — JWT filter (depends on P3.1)
3. P3.3 — OAuth2 handler + SecurityConfig (depends on P3.1, P3.2, P2.2 entities)
4. P3.4 — /api/users/me (depends on P3.3 for auth)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P3.1 — add JWT service` | P3.1 |
| 2 | `feat(sprint001): P3.2 — add JWT authentication filter` | P3.2 |
| 3 | `feat(sprint001): P3.3 — add OAuth2 success handler and Security config` | P3.3 |
| 4 | `feat(sprint001): P3.4 — add GET /api/users/me endpoint` | P3.4 |

## Manual Testing

1. Start backend: `cd backend && ./mvnw spring-boot:run` (with Google OAuth client-id/secret in env)
2. Open browser: `http://localhost:8080/oauth2/authorization/google`
3. Login with Google account
4. Should redirect to `http://localhost:5173/oauth/callback?token=<JWT>`
5. Copy the JWT token, call: `curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/users/me`
6. Should return user JSON with name, email, role

## Definition of Done

- [ ] All tests pass (`./mvnw verify`)
- [ ] Google OAuth2 login flow works end-to-end
- [ ] First login creates User + Student in DB
- [ ] JWT contains userId, email, role
- [ ] /api/users/me returns authenticated user data
- [ ] Unauthenticated requests to /api/* return 401
- [ ] No compilation warnings
