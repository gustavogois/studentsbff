# StudentsBFF — Deployment Guide

## Environments

| Environment | Purpose | Deploy trigger |
|-------------|---------|----------------|
| **Staging (STG)** | Pre-production testing | Merge sprint PR to `main` → CI auto-deploys |
| **Production (PRD)** | Live users | Manual via `/deployPRD` skill (explicit `railway up`) |

## Hosting

All services are hosted on [Railway](https://railway.app):

| Service | Type | Notes |
|---------|------|-------|
| **backend** | Spring Boot JAR | Java 21, nixpacks |
| **frontend** | Static site (Vite build) | Node 22, nixpacks |
| **postgres** | PostgreSQL 16 | Managed by Railway |

## Required Environment Variables

### Backend (`application-prod.yml`)

| Variable | Description | Where to set |
|----------|-------------|--------------|
| `DATABASE_URL` | PostgreSQL connection URL | Railway (auto-provided) |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | Railway dashboard |
| `FRONTEND_URL` | Frontend domain for CORS | Railway dashboard |
| `GOOGLE_CLIENT_ID` | OAuth2 client ID | Railway dashboard |
| `GOOGLE_CLIENT_SECRET` | OAuth2 client secret | Railway dashboard |
| `OPENAI_API_KEY` | OpenAI API key | Railway dashboard |

### Frontend

| Variable | Description | Where to set |
|----------|-------------|--------------|
| `VITE_API_URL` | Backend API base URL | `frontend/.env.production` |

## Railway Configuration

### Backend (`backend/railway.toml`)

```toml
[build]
builder = "nixpacks"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 3
```

### Frontend (`frontend/railway.toml`)

```toml
[build]
builder = "nixpacks"

[deploy]
healthcheckPath = "/"
```

## CI/CD Pipeline

### Backend CI (`.github/workflows/backend-ci.yml`)

1. Checkout + Set up Java 21
2. Run tests: `./mvnw verify`
3. Upload test report
4. (On push to main) Deploy to staging via Railway CLI

### Frontend CI (`.github/workflows/frontend-ci.yml`)

1. Checkout + Set up Node 22
2. Install dependencies
3. Lint + test + build

## Pre-Deployment Validation

Always run the 10-point validation checklist before deploying:

```bash
./scripts/pre-deploy-validate.sh stg   # For staging
./scripts/pre-deploy-validate.sh prd   # For production
```

See CLAUDE.md "Pre-Deployment Validation" section for the full checklist.

## Rollback

Railway automatically rolls back if the health check fails after deploy. For manual rollback, use the Railway dashboard to redeploy a previous successful deployment.
