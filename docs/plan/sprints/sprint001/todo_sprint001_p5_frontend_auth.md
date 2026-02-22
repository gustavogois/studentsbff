# P5 — Frontend Foundation & Auth: Implementation Plan

**Sprint:** 001
**Status:** Done
**Backlog items:** B06 (partial), B07
**Goal:** Frontend has routing, API client with JWT, AuthContext, ProtectedRoute, and a working "Login with Google" flow.

---

## Tasks

### P5.1 — TypeScript types and API client

**Tests (write first):**
- [ ] `client.test.ts#shouldAttachAuthorizationHeader` — mock localStorage with token, verify Axios interceptor adds Bearer header
- [ ] `client.test.ts#shouldRedirectOn401` — mock 401 response, verify redirect to /login

**Implementation:**
- [ ] Create `frontend/src/types/index.ts` — interfaces: `User { id, name, email, role, avatarUrl }`, `Subject { id, name, createdAt, updatedAt }`, `Topic { id, name, difficulty, createdAt, updatedAt }`
- [ ] Create `frontend/src/services/client.ts` — Axios instance with `baseURL` from `VITE_API_URL`, request interceptor that reads JWT from localStorage, response interceptor that redirects to /login on 401
- [ ] Create `frontend/.env.development` with `VITE_API_URL=http://localhost:8080`

**Commit:** `feat(sprint001): P5.1 — add TypeScript types and API client`

---

### P5.2 — AuthContext and auth service

**Tests (write first):**
- [ ] `useAuth.test.ts#shouldReturnNullUserWhenNoToken` — render hook, verify user is null
- [ ] `useAuth.test.ts#shouldParseUserFromToken` — set token in localStorage, render hook, verify user is populated
- [ ] `useAuth.test.ts#shouldClearTokenOnLogout` — call logout, verify localStorage cleared and user is null

**Implementation:**
- [ ] Create `frontend/src/services/authService.ts` — `fetchCurrentUser()` calls `GET /api/users/me`
- [ ] Create `frontend/src/contexts/AuthContext.tsx` — provides `user`, `token`, `login(token)`, `logout()`, `isLoading`
  - On mount: checks localStorage for token, calls `/api/users/me` to validate, sets user
  - `login(token)`: stores in localStorage, fetches user
  - `logout()`: clears localStorage, sets user to null

**Commit:** `feat(sprint001): P5.2 — add AuthContext and auth service`

---

### P5.3 — ProtectedRoute component

**Tests (write first):**
- [ ] `ProtectedRoute.test.tsx#shouldRenderChildrenWhenAuthenticated` — wrap in AuthContext with user, verify children render
- [ ] `ProtectedRoute.test.tsx#shouldRedirectToLoginWhenNotAuthenticated` — wrap in AuthContext without user, verify redirect

**Implementation:**
- [ ] Create `frontend/src/components/ProtectedRoute.tsx` — checks AuthContext for user, redirects to `/login` if not authenticated, renders `<Outlet />` if authenticated
- [ ] Create `frontend/src/components/Layout.tsx` — basic page layout with nav header (app name, user avatar, logout button) and content area

**Commit:** `feat(sprint001): P5.3 — add ProtectedRoute and Layout components`

---

### P5.4 — Login page and OAuth callback

**Tests (write first):**
- [ ] `LoginPage.test.tsx#shouldRenderGoogleLoginButton` — verify "Login with Google" button renders
- [ ] `OAuthCallback.test.tsx#shouldExtractTokenFromURL` — mock URL with `?token=abc`, verify token stored and redirect to dashboard

**Implementation:**
- [ ] Create `frontend/src/pages/LoginPage.tsx` — centered card with app logo/title and "Login with Google" button that redirects to `${VITE_API_URL}/oauth2/authorization/google`
- [ ] Create `frontend/src/pages/OAuthCallback.tsx` — reads `?token=` from URL params, calls `auth.login(token)`, redirects to `/dashboard`
- [ ] Update `frontend/src/App.tsx` with routes:
  - `/login` → LoginPage (public)
  - `/oauth/callback` → OAuthCallback (public)
  - `/` → ProtectedRoute wrapper
    - `/dashboard` → DashboardPage (placeholder)
    - `/subjects` → SubjectsPage (placeholder)

**Commit:** `feat(sprint001): P5.4 — add Login page and OAuth callback`

---

## Execution Order

1. P5.1 — Types + API client (foundation)
2. P5.2 — AuthContext (depends on P5.1 client)
3. P5.3 — ProtectedRoute + Layout (depends on P5.2 context)
4. P5.4 — Login + OAuth callback + routing (depends on P5.2, P5.3)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint001): P5.1 — add TypeScript types and API client` | P5.1 |
| 2 | `feat(sprint001): P5.2 — add AuthContext and auth service` | P5.2 |
| 3 | `feat(sprint001): P5.3 — add ProtectedRoute and Layout components` | P5.3 |
| 4 | `feat(sprint001): P5.4 — add Login page and OAuth callback` | P5.4 |

## Manual Testing

1. Start backend + frontend locally
2. Visit `http://localhost:5173/dashboard` — should redirect to `/login`
3. Click "Login with Google" — should redirect to Google, then back to `/oauth/callback?token=...`
4. After callback, should redirect to `/dashboard`
5. Refresh page — should stay on `/dashboard` (token persisted in localStorage)
6. Click logout — should redirect to `/login`
7. Open DevTools Network tab — verify `/api/users/me` called on page load with Authorization header

## Definition of Done

- [ ] All tests pass (`npm test`)
- [ ] Frontend builds (`npm run build`)
- [ ] Login with Google flow works end-to-end
- [ ] Protected routes redirect unauthenticated users
- [ ] JWT persisted across page refreshes
- [ ] Logout clears token and redirects
- [ ] No TypeScript errors
