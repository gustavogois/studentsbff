# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] — 2026-02-22

### Added
- Project scaffolding: Spring Boot 3 + Java 21 + Maven backend, React 19 + TypeScript + Vite + Tailwind CSS frontend
- Docker Compose for local PostgreSQL 16
- Flyway V1 migration: users, students, parent_students, subjects, topics tables
- JPA entities and repositories for all tables
- Google OAuth2 login with JWT session tokens
- Auto-creation of user + student profile on first Google login
- JWT authentication filter for API endpoints
- `GET /api/users/me` endpoint
- CRUD endpoints for subjects (`/api/subjects`)
- CRUD endpoints for topics (`/api/subjects/{id}/topics`)
- Ownership validation on all subject/topic endpoints
- Frontend Login page with "Login with Google" button
- Frontend OAuth callback handler
- AuthContext with JWT localStorage persistence and Axios interceptor
- ProtectedRoute component redirecting unauthenticated users to login
- Dashboard page showing user's subjects
- Subjects page with create, edit, and delete
- Subject detail page with topics CRUD
- 41 backend tests, 25 frontend tests
