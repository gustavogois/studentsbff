# StudentsBFF

**StudentsBFF** (Best Friend Forever) is a study companion platform for middle school students (grades 6-9, ages 11-14). It integrates study planning, AI-powered tutoring, spaced-repetition review, and a parent dashboard into a single connected experience.

## Vision

Students today use fragmented tools (ChatGPT for questions, Quizlet for flashcards, Edraw for mind maps) with no integration between them. StudentsBFF replaces that fragmented workflow with an intelligent, context-aware platform that knows the student's school schedule, subjects, and progress — and uses AI to create personalized study plans.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 21 + Spring Boot 3 + Maven |
| **Frontend** | React 19 + TypeScript + Vite + Tailwind CSS + PWA |
| **Database** | PostgreSQL 16 |
| **AI (initial)** | OpenAI API (GPT-4o) |
| **AI (future)** | Abstraction layer for multiple providers (Anthropic Claude, etc.) |
| **Hosting** | Railway (frontend + backend + PostgreSQL) |

## Prerequisites

- Java 21 (JDK)
- Node.js 22+
- Docker (for local PostgreSQL)

## Local Development

```bash
# 1. Start the database
docker compose up -d

# 2. Start the backend (port 8080)
cd backend && ./mvnw spring-boot:run

# 3. Start the frontend (port 5173)
cd frontend && npm run dev
```

## Testing

```bash
# Backend tests
cd backend && ./mvnw verify

# Frontend tests
cd frontend && npm run build && npm test
```

## Project Structure

```
studentsbff/
├── backend/           # Spring Boot REST API
├── frontend/          # React + Vite SPA (PWA)
├── docs/              # Project documentation
│   ├── SPECIFICATION.md   # Product spec, backlog, requirements
│   ├── DATA_MODEL.md      # Entity definitions, ER diagram
│   ├── DEPLOYMENT.md      # Production deployment guide
│   ├── TODO.md            # Current sprint tasks
│   └── plan/
│       ├── releases.md    # Release history
│       ├── tech-debt.md   # Technical debt tracker
│       └── sprints/       # Per-sprint planning docs
├── scripts/           # Automation scripts
├── CLAUDE.md          # AI assistant instructions
├── PLAN.md            # Implementation plan
├── context.md         # Product context and user research
└── CHANGELOG.md       # Release changelog
```

## Documentation

| Document | Purpose |
|----------|---------|
| [CLAUDE.md](CLAUDE.md) | AI assistant conventions and project rules |
| [context.md](context.md) | Product context, user research, stakeholder decisions |
| [PLAN.md](PLAN.md) | Implementation plan with architecture and entities |
| [docs/SPECIFICATION.md](docs/SPECIFICATION.md) | Product spec, functional/non-functional requirements, backlog |
| [docs/DATA_MODEL.md](docs/DATA_MODEL.md) | Entity definitions, relationships, Flyway migrations |
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | Production deployment guide (Railway) |
| [docs/plan/releases.md](docs/plan/releases.md) | Release history and version log |
| [docs/ClaudeCodeWorkflow.md](docs/ClaudeCodeWorkflow.md) | End-to-end development lifecycle |
