# AI Interview Prep Platform

This repository contains a local interview-practice application. Phase 1 provides the backend API; the frontend is currently a runnable placeholder until Checkpoint A is accepted.

## Requirements

- Java 21 or newer (the build targets Java 21)
- Node.js 20.9 or newer and npm
- Docker Desktop or another Docker Engine for PostgreSQL and Testcontainers
- A free OpenAI-compatible API key is optional until you use question generation or grading

The backend uses Java 21, Spring Boot 4.1.2, Spring AI 2.0.2, Maven 3.9.12 via the Maven Wrapper, and PostgreSQL 18.6. The frontend uses Next.js 16.4.4, React 19.3.1, TypeScript, and Tailwind CSS.

## Configure local environment

From the repository root, copy `.env.example` to `.env` and replace the local database password placeholders with your own values. Keep `.env` private; it is ignored by Git. If you choose an AI provider, set `LLM_BASE_URL`, `LLM_API_KEY`, and `LLM_MODEL` in `.env`. The key is used only by the backend.

Start PostgreSQL:

```powershell
docker compose up -d db
```

The database listens only on `127.0.0.1`. The backend uses a separate database role with access to the application schema.

## Run the backend

From the repository root:

```powershell
.\mvnw.cmd -f backend\pom.xml spring-boot:run
```

The backend binds to `127.0.0.1:8080`. It applies Flyway migrations on startup and validates the entity mappings against them. Without an AI key, health and non-AI startup still work; AI endpoints return a safe configuration error.

## Run the frontend

From `frontend/`:

```powershell
npm ci
npm run dev
```

Open `http://127.0.0.1:3000`. The current page is a Phase 0 placeholder; the interview screens are part of Phase 2.

## Run checks

Backend tests use JUnit, Mockito, and Testcontainers PostgreSQL:

```powershell
.\mvnw.cmd -f backend\pom.xml test
```

Frontend checks:

```powershell
Set-Location frontend
npm run lint
npm run build
npm audit
```

The Testcontainers integration tests need Docker running. Tests never contact an external AI provider.

## API outline

- `POST /api/sessions` creates a session and generates 5–8 questions.
- `GET /api/sessions/{id}` returns a session, questions, and any submitted answers.
- `POST /api/questions/{id}/answer` grades one answer; a question accepts one answer.
- `GET /api/sessions/{id}/summary` returns the computed score and weak topics.

The MVP has no accounts. Anyone who obtains a session UUID can access that session, so the service binds to loopback and is not ready for public deployment. Job descriptions and answers are sent to the configured AI provider when AI is enabled; the Phase 2 interface will make that clear before sending.
