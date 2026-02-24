# B21 — Internationalisation (i18n): Implementation Plan

**Sprint:** 003
**Status:** Not Started
**Backlog Item:** B21
**Goal:** Set up `react-i18next` with English (default), Brazilian Portuguese, and European Portuguese. Extract all existing hardcoded UI strings into translation files so every subsequent feature uses translation keys from the start.

---

## Context

The app currently has all UI text hardcoded in English across ~10 frontend files (6 pages + 4 components). Before building new features, we set up the i18n infrastructure so every new string is translatable from the start. This follows the same approach used in the pantry2026 project.

---

## Tasks

### B21.1 — Install deps + create i18n config + translation files

**Tests (write first):**
- [ ] `i18n.test.ts#shouldInitialiseWithEnglishDefault` — import i18n, verify `i18n.language` defaults to `en`
- [ ] `i18n.test.ts#shouldFallbackToEnglishForUnknownLocale` — set language to `xx`, verify fallback to `en`
- [ ] `i18n.test.ts#shouldFallbackPtToPtBR` — set language to `pt`, verify resolves to `pt-BR`

**Implementation:**
- [ ] `npm install i18next react-i18next i18next-browser-languagedetector`
- [ ] Create `frontend/src/i18n/index.ts` with i18next init, LanguageDetector plugin, fallback chain (`pt` → `pt-BR`, default → `en`)
- [ ] Create `en.json`, `pt-BR.json`, `pt-PT.json` with namespaced keys:
  - `common` — Loading, cancel, save, delete, confirm, error, success
  - `nav` — Dashboard, Subjects, Events, Calendar, Profile, Logout
  - `login` — Title, subtitle, Google sign-in button
  - `dashboard` — Welcome message, quick actions
  - `subjects` — Title, create form, empty state, card labels
  - `subjectDetail` — Topics section, add topic form, difficulty labels
  - `profile` — Title, form labels, save button, language section
  - `auth` — Redirecting, session expired messages
- [ ] Import `./i18n` in `main.tsx` before React app renders

**Commit:** `feat(sprint003): B21.1 — add i18n infrastructure with 3 locales`

---

### B21.2 — Extract hardcoded strings from all pages and components

**Tests (write first):**
- [ ] Mock `react-i18next` in test setup so `t(key)` returns the key — avoids brittle text assertions
- [ ] Update all existing test assertions that match on hardcoded English text to use translation keys or regex patterns

**Implementation:**
- [ ] Add i18n test mock to `frontend/src/setupTests.ts` or a `__mocks__` file
- [ ] Convert 6 pages: `LoginPage`, `DashboardPage`, `SubjectsPage`, `SubjectDetailPage`, `ProfilePage`, `OAuthCallback`
- [ ] Convert 4 components: `Layout`, `SubjectCard`, `TopicList`, `ProtectedRoute`
- [ ] Pattern: `const { t } = useTranslation();` then `t('namespace.key')`

**Commit:** `feat(sprint003): B21.2 — extract all hardcoded strings to i18n translation keys`

---

### B21.3 — Language switcher in Profile page

**Tests (write first):**
- [ ] `ProfilePage.test.tsx#shouldRenderLanguageSwitcher` — verify 3 language buttons rendered
- [ ] `ProfilePage.test.tsx#shouldHighlightActiveLanguage` — verify current language button has active styling
- [ ] `ProfilePage.test.tsx#shouldChangeLanguage` — click pt-BR button, verify `i18n.changeLanguage` called

**Implementation:**
- [ ] Add language section to `ProfilePage.tsx` with 3 buttons: English, Portugues (Brasil), Portugues (Portugal)
- [ ] Highlight active language using `i18n.resolvedLanguage`
- [ ] Language choice persisted via `localStorage` by LanguageDetector

**Commit:** `feat(sprint003): B21.3 — add language switcher to Profile page`

---

## Execution Order

1. B21.1 — Install + config + translation files + test mock
2. B21.2 — Extract hardcoded strings (depends on B21.1 for i18n setup)
3. B21.3 — Language switcher (depends on B21.2 for i18n in ProfilePage)

## Commit Plan

| Order | Commit message | Tasks covered |
|-------|---------------|---------------|
| 1 | `feat(sprint003): B21.1 — add i18n infrastructure with 3 locales` | B21.1 |
| 2 | `feat(sprint003): B21.2 — extract all hardcoded strings to i18n translation keys` | B21.2 |
| 3 | `feat(sprint003): B21.3 — add language switcher to Profile page` | B21.3 |

## Manual Testing

1. Load app → all text in English by default
2. Change browser language to `pt-BR` → clear localStorage → reload → text in Portuguese
3. Go to Profile → language switcher visible with 3 buttons
4. Click "Portugues (Brasil)" → all UI text changes to pt-BR
5. Reload page → language persisted (still pt-BR)
6. Click "English" → text reverts to English

## Definition of Done

- [ ] `i18next`, `react-i18next`, and `i18next-browser-languagedetector` installed
- [ ] `en.json`, `pt-BR.json`, and `pt-PT.json` contain all UI strings
- [ ] Every page and component uses `t()` — no hardcoded user-facing strings remain
- [ ] Language switcher in Profile page works and persists choice
- [ ] Browser language auto-detection works (falls back to EN)
- [ ] `npm run build` succeeds without errors
- [ ] `npm test` passes
