# Deploy — Task & Time Tracker

Single Docker image: backend + frontend (static files are copied into the JAR at build time).

## Environment variables (required)

| Variable | Value |
|---|---|
| `JWT_SECRET` | Random string, ≥32 chars. Generate: `openssl rand -base64 48`. App fails fast without it. Never share it. |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<host>:5432/<db>?sslmode=require` (lowercase DB name) |
| `SPRING_DATASOURCE_USERNAME` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `CORS_ALLOWED_ORIGINS` | Production frontend origin, e.g. `https://app.example.com` |
| `PORT` | Set automatically by the host; defaults to `8080` locally |

Liquibase migrations (001→018) apply automatically on an empty database.
Swagger: `http://<host>:<port>/swagger-ui/index.html`
(disable in prod: `SPRINGDOC_API_DOCS_ENABLED=false`, `SPRINGDOC_SWAGGER_UI_ENABLED=false`).

## Local Docker

```bash
docker build -t task-time-tracker .
docker run -d --name ttt -p 8080:8080 \
  -e JWT_SECRET='<secret>' \
  -e SPRING_DATASOURCE_URL='jdbc:postgresql://<host>:5432/<db>' \
  -e SPRING_DATASOURCE_USERNAME='<user>' \
  -e SPRING_DATASOURCE_PASSWORD='<password>' \
  -e CORS_ALLOWED_ORIGINS='https://<frontend-host>' \
  task-time-tracker
```

## Production (Render + Neon)

- Database: Neon Postgres 18, lowercase database name (e.g. `tasktracker`).
- Hosting: Render Web Service, Docker runtime, Free plan, same region as the database.
- Set the 5 variables above in Render Environment; `PORT` is injected by Render.
- Push to `main` triggers auto-deploy (~7 min). A failed deploy keeps the previous Live version.

## Frontend

No separate hosting or build: `frontend/` is copied into `target/classes/static` during `process-resources`
and served by the backend. The API base URL field on the login screen defaults to the same origin.
Bump `?v=N` in `index.html` after static changes to invalidate browser cache.

## CI

`.github/workflows/ci-cd.yml`: tests on every push/PR (PostgreSQL 18 service), JAR build on push to `main`.
