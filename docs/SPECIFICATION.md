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
| — | _No features implemented yet_ | — | — |

### 2.2 Backlog

> **Note:** These are initial proposals derived from the implementation plan and user research. Priorities and scope will be validated during the first `/refinement` session.

| ID | Feature | Priority | Sprint | Status |
|----|---------|----------|--------|--------|
| B01 | Project scaffolding (Spring Boot + React + Docker Compose) | Must | — | Proposed |
| B02 | Database schema V1 (users, students, parents, subjects, topics) | Must | — | Proposed |
| B03 | JWT authentication (register + login) | Must | — | Proposed |
| B04 | CRUD subjects and topics | Must | — | Proposed |
| B05 | Parent-student linking and parent read-only view | Must | — | Proposed |
| B06 | Frontend: login, register, dashboard, subjects pages | Must | — | Proposed |
| B07 | Protected routes and auth context | Must | — | Proposed |
| B08 | Student profile management (grade, school) | Should | — | Proposed |
| B09 | AI-powered study plan generation (OpenAI GPT-4o) | Must | — | Proposed |
| B10 | Study plan calendar view (daily/weekly) | Should | — | Proposed |
| B11 | Manual data entry (exams, assignments, deadlines) | Must | — | Proposed |
| B12 | Spaced repetition review system | Should | — | Proposed |
| B13 | AI chat tutor (contextual Q&A per subject/topic) | Should | — | Proposed |
| B14 | Mind map generation for topics | Could | — | Proposed |
| B15 | Parent dashboard (progress, grades, study time) | Should | — | Proposed |
| B16 | Notification system (study reminders, parent alerts) | Could | — | Proposed |
| B17 | Gamification (points, streaks, achievements) | Could | — | Proposed |
| B18 | PWA offline mode | Could | — | Proposed |
| B19 | Photo/OCR data capture (agenda, notebook) | Could | — | Proposed |
| B20 | Gmail integration for school email parsing | Could | — | Proposed |

---

## 3. Non-Functional Requirements

| ID | Requirement | Target |
|----|------------|--------|
| NF01 | Response time for API calls | < 500ms (p95) |
| NF02 | AI response time | < 5s for study plan generation |
| NF03 | Uptime | 99.5% (Railway SLA) |
| NF04 | Mobile responsiveness | PWA works on screens >= 320px |
| NF05 | Security | OWASP Top 10 compliance, bcrypt passwords, JWT tokens |
| NF06 | Data privacy | Student data encrypted at rest (PostgreSQL), HTTPS in transit |
| NF07 | Browser support | Chrome, Safari, Firefox (latest 2 versions) |
| NF08 | Accessibility | WCAG 2.1 AA for core flows |
| NF09 | Test coverage | >= 80% backend line coverage |
| NF10 | Database migrations | Forward-only Flyway, backwards-compatible |

---

## 4. Release Roadmap

| Sprint | Version | Scope | Status |
|--------|---------|-------|--------|
| — | _No releases yet_ | — | — |
