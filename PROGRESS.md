# PROGRESS.md: Agent task board

Maintained by the coding agents (see `AGENTS.md`, section 4.4). The owner's personal list is `task.md`; agents never edit that file.

## Current phase
Phase 0 (scaffold) and Phase 1 (backend) are in progress. Stop at Checkpoint A and wait for the owner's "go" before Phase 2.

## Status

### Done
- Corrected malformed Markdown in `AGENTS.md` so its policy headings and Java header example render and copy correctly.

### In progress
- Phase 0 scaffold and Phase 1 backend implementation, following the dated plan below.

### Next
- Complete Phase 0 scaffold and runnable backend/frontend shells.
- Complete Phase 1 backend and tests, then stop at CHECKPOINT A.
- Phase 2: frontend (then CHECKPOINT B)
- Phase 3: hardening, end-to-end test, README
- Final: report "Ready to commit" (the owner commits once at the end)

## Decisions
### 2026-11-02: Phase 0 and Phase 1 plan
- Use Java 21, Spring Boot 4.1.2, Spring AI 2.0.2, Next.js 16.4.4, React 19.3.1, and PostgreSQL 18.6; keep versions pinned and use the Spring Boot parent plus Spring AI BOM.
- Scaffold a local-only backend and Next.js shell, PostgreSQL Compose service with a non-superuser application role, environment-only credentials, and required docs; use the Maven 3.9.12 distribution through the Maven Wrapper 3.3.4, and keep product UI work for Phase 2.
- Implement the stable API through controller/service/repository layers, Flyway-managed tables, strict request/model validation, provider-independent `LlmClient`, bounded per-client rate limiting, safe errors, and security headers.
- Keep provider calls outside database transactions; persist successful results transactionally. Accept one answer per question (`409` on repeats), compute answer/session averages in code, and mark topics below 3/5 as weak.
- Add unit and PostgreSQL Testcontainers coverage, never call a real provider in tests, document each user/API flow and file, and run the required tests, audits, and secret checks at Checkpoint A; then stop for the owner's "go".
- Use Spring Boot's official `spring-boot-webmvc-test` artifact only in test scope because Spring Boot 4 separates MockMvc test support from the base test starter.

## Assumptions
- LLM provider is not chosen yet; it is configured only through `LLM_BASE_URL`, `LLM_API_KEY`, `LLM_MODEL`.
- A session summary has a null overall score and no weak topics until at least one answer is graded; averages use two decimal places, and ties are ordered alphabetically by topic.
- The app remains local-only for this MVP. Session UUIDs are not authentication; a configured provider receives the job description and answers. Phase 2 must show that data-transfer notice before sending text.
- Rate limits use the direct client address, ignore forwarded headers, are bounded in memory, and protect one backend process only.
- If an answer already exists for a question, a repeat submission returns `409 Conflict`; concurrent requests may duplicate an LLM call, while the database unique constraint prevents duplicate rows.
- Spring AI calls use at most two transport attempts per model response and one additional response-format retry for invalid question JSON, capping question generation at four model calls per API request.

## Open questions for the owner
- (none; design choices above follow the simplest MVP interpretation and can be revisited after Checkpoint A.)

## Ideas (not approved, do not build)
- (none)

## Known issues
- Maven is not currently available as a system command; use the project Maven Wrapper so the repository remains buildable without a global Maven install.
