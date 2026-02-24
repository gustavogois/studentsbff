# B21 — Internationalisation (i18n): Implementation Plan

**Sprint:** 003
**Priority:** High
**Status:** Planned
**Backlog Item:** B21
**Goal:** Set up `react-i18next` with English (default), Brazilian Portuguese, and European Portuguese. Extract all existing hardcoded UI strings into translation files so every subsequent feature uses translation keys from the start.

---

## Context

The app currently has all UI text hardcoded in English across ~10 frontend files (6 pages + 4 components). Before building new features, we set up the i18n infrastructure so every new string is translatable from the start. This follows the same approach used in the pantry2026 project.

---

## Scope

| Item | Description |
|------|-------------|
| Locales | EN (default/fallback), pt-BR, pt-PT |
| Library | `i18next` + `react-i18next` + `i18next-browser-languagedetector` |
| Fallback chain | `pt` → `pt-BR` → `en` |
| Persistence | `localStorage` via LanguageDetector |
| Language switcher | Profile page (3 buttons: English / Portugues Brasil / Portugues Portugal) |

---

## Files to create

| File | Description |
|------|-------------|
| `frontend/src/i18n/index.ts` | i18n initialisation (i18next + browser language detector) |
| `frontend/src/i18n/en.json` | English translations |
| `frontend/src/i18n/pt-BR.json` | Brazilian Portuguese translations |
| `frontend/src/i18n/pt-PT.json` | European Portuguese translations |

## Files to modify

| File | Changes |
|------|---------|
| `frontend/src/main.tsx` | Import `./i18n` to initialise i18n before app renders |
| `frontend/src/pages/LoginPage.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/pages/DashboardPage.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/pages/SubjectsPage.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/pages/SubjectDetailPage.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/pages/ProfilePage.tsx` | Replace hardcoded strings with `t()` calls; add language switcher section |
| `frontend/src/pages/OAuthCallback.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/components/Layout.tsx` | Replace nav labels with `t()` calls |
| `frontend/src/components/SubjectCard.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/components/TopicList.tsx` | Replace hardcoded strings with `t()` calls |
| `frontend/src/components/ProtectedRoute.tsx` | Replace hardcoded strings with `t()` calls (if any) |

---

## Implementation by task

### B21.1 — Install dependencies

```bash
cd frontend && npm install i18next react-i18next i18next-browser-languagedetector
```

### B21.2 — Create i18n config and translation files

- Create `frontend/src/i18n/index.ts` with i18next init, LanguageDetector plugin, fallback chain
- Create `en.json`, `pt-BR.json`, `pt-PT.json` with namespaced keys:
  - `common` — Loading, cancel, save, delete, confirm, error, success
  - `nav` — Dashboard, Subjects, Profile, Logout
  - `login` — Title, subtitle, Google sign-in button
  - `dashboard` — Welcome message, quick actions
  - `subjects` — Title, create form, empty state, card labels
  - `subjectDetail` — Topics section, add topic form, difficulty labels
  - `profile` — Title, form labels, save button, language section
  - `auth` — Redirecting, session expired messages

### B21.3 — Initialise in `main.tsx`

Single import: `import './i18n';` before React app renders.

### B21.4 — Extract hardcoded strings from all pages and components

Convert all 6 pages and 4 components to use `useTranslation()` hook with `t()` calls. For any class components, use `i18n.t()` directly.

### B21.5 — Language switcher in Profile page

Add a language section to ProfilePage with three buttons (English / Portugues Brasil / Portugues Portugal). Highlight active language using `i18n.resolvedLanguage`. Language choice persisted via `localStorage` by LanguageDetector.

---

## Definition of Done

- [ ] `i18next`, `react-i18next`, and `i18next-browser-languagedetector` installed
- [ ] `en.json`, `pt-BR.json`, and `pt-PT.json` contain all UI strings
- [ ] Every page and component uses `t()` — no hardcoded user-facing strings remain
- [ ] Language switcher in Profile page works and persists choice
- [ ] Browser language auto-detection works (falls back to EN)
- [ ] `npm run build` succeeds without errors
- [ ] `npm test` passes (update test assertions to match translation keys or mocked translations)
