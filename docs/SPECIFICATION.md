# StudentsBFF — Product Specification

## 1. Vision

StudentsBFF is a study companion platform for middle school students (grades 6-9, ages 11-14). It replaces the fragmented workflow of using separate tools (ChatGPT, Quizlet, Edraw) with an integrated, context-aware study experience powered by AI.

**Core problem:** Students lack an integrated tool that understands their school context and connects study planning, AI tutoring, review, and parent visibility into one flow.

**MVP focus:** Intelligent study planner that organizes what to study per day/week based on the school calendar, exams, and subjects.

---

## 2. Features

### 2.1 Implemented Features

| ID | Feature | Version | Sprint |
|----|---------|---------|--------|
| B01 | Project scaffolding (Spring Boot + React + Docker Compose) | 0.1.0 | 001 |
| B02 | Database schema V1 (users, students, parents, subjects, topics) | 0.1.0 | 001 |
| B03 | Google OAuth2 authentication (login with Google, JWT session) | 0.1.0 | 001 |
| B04 | CRUD subjects and topics | 0.1.0 | 001 |
| B06 | Frontend: Google login, dashboard, subjects pages | 0.1.0 | 001 |
| B07 | Protected routes and auth context | 0.1.0 | 001 |

### 2.2 Backlog

> Validated during refinement session on 2026-02-22.

| ID | Feature | Priority | Sprint | Status |
|----|---------|----------|--------|--------|
| B01 | Project scaffolding (Spring Boot + React + Docker Compose) | Must | 001 | Done |
| B02 | Database schema V1 (users, students, parents, subjects, topics) | Must | 001 | Done |
| B03 | Google OAuth2 authentication (login with Google, JWT session) | Must | 001 | Done |
| B04 | CRUD subjects and topics | Must | 001 | Done |
| B05 | Parent-student linking and parent read-only view | Must | 002 | Backlog |
| B06 | Frontend: Google login, dashboard, subjects pages | Must | 001 | Done |
| B07 | Protected routes and auth context | Must | 001 | Done |
| B08 | Student profile management (grade, school) | Should | — | Backlog |
| B09 | AI-powered study plan generation (OpenAI GPT-4o) | Must | — | Backlog |
| B10 | Study plan calendar view (daily/weekly) | Should | — | Backlog |
| B11 | Manual data entry (exams, assignments, deadlines) | Must | — | Backlog |
| B12 | Spaced repetition review system | Should | — | Backlog |
| B13 | AI chat tutor (contextual Q&A per subject/topic) | Should | — | Backlog |
| B14 | Mind map generation for topics | Could | — | Backlog |
| B15 | Parent dashboard (progress, grades, study time) | Should | — | Backlog |
| B16 | Notification system (study reminders, parent alerts) | Could | — | Backlog |
| B17 | Gamification (points, streaks, achievements) | Could | — | Backlog |
| B18 | PWA offline mode | Could | — | Backlog |
| B19 | Photo/OCR data capture (agenda, notebook) | Could | — | Backlog |
| B20 | Gmail integration for school email parsing | Could | — | Backlog |

---

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|------------|--------|
| NF01 | Response time for API calls | < 500ms (p95) |
| NF02 | AI response time | < 5s for study plan generation |
| NF03 | Uptime | 99.5% (Railway SLA) |
| NF04 | Mobile responsiveness | PWA works on screens >= 320px |
| NF05 | Security | OWASP Top 10 compliance, Google OAuth2, JWT session tokens |
| NF06 | Data privacy | Student data encrypted at rest (PostgreSQL), HTTPS in transit |
| NF07 | Browser support | Chrome, Safari, Firefox (latest 2 versions) |
| NF08 | Accessibility | WCAG 2.1 AA for core flows |
| NF09 | Test coverage | >= 80% backend line coverage |
| NF10 | Database migrations | Forward-only Flyway, backwards-compatible |

---

## 4. Release Roadmap

| Sprint | Version | Scope | Status |
|--------|---------|-------|--------|
| 001 | 0.1.0 | Walking skeleton: scaffolding, DB, auth, subjects CRUD, frontend | Done |
| 002 | 0.2.0 | Parent features (B05), student profile (B08) | Planned |
