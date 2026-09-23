# Deploy — Task & Time Tracker

## Backend (Docker)

```bash
docker build -t task-time-tracker .
docker run -d --name ttt -p 8080:8080 \
  -e JWT_SECRET='<мінімум 32 символи, згенеруй: openssl rand -base64 48>' \
  -e SPRING_DATASOURCE_URL='jdbc:postgresql://<host>:5432/<db>' \
  -e SPRING_DATASOURCE_USERNAME='<user>' \
  -e SPRING_DATASOURCE_PASSWORD='<password>' \
  -e CORS_ALLOWED_ORIGINS='https://твій-фронт.example.com' \
  task-time-tracker
```

Liquibase-міграції (001→014) застосуються самі на чистій БД.
Swagger: `http://<host>:8080/swagger-ui/index.html`
(вимкнути в проді: `-e SPRINGDOC_API_DOCS_ENABLED=false`
та `-e SPRINGDOC_SWAGGER_UI_ENABLED=false`).

Обов'язково:
- `JWT_SECRET` — свій, довжиною ≥ 32 символи. Інакше застосунок
  не стартує (fail-fast з зрозумілою помилкою). Нікому його не пересилай.
- Змінні БД вказують на продовий Postgres 18.

## Frontend (`frontend/` — статика, збірка не потрібна)

Будь-який статичний хостинг (Nginx, GitHub Pages, Netlify, S3…).
URL бекенда вводиться на екрані входу і зберігається в браузері,
тому окремої конфігурації не треба — просто вкажи продовий URL API.

## CI

`.github/workflows/ci-cd.yml`: тести на кожен push/PR (postgres:18),
збірка JAR на push у main/master.
