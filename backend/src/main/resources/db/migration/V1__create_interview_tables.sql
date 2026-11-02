-- FILE: backend/src/main/resources/db/migration/V1__create_interview_tables.sql
-- WHAT IT DOES: Creates the interview session, question, and answer tables with their constraints.
-- ROLE IN THE PROJECT: Defines the first persistent schema; Flyway applies it on backend startup.
-- IF YOU REMOVE THIS FILE: Fresh databases lack the tables and existing databases cannot validate this migration history.
-- DEPENDS ON: PostgreSQL 18 and Flyway.
-- USED BY: Spring Boot Flyway auto-configuration and database integration tests.
-- KEY FUNCTIONS: The statements create tables, foreign keys, uniqueness, and score bounds.

CREATE TABLE interview_session (
    id UUID PRIMARY KEY,
    job_title VARCHAR(120) NOT NULL,
    job_description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    overall_score NUMERIC(3, 2)
);

CREATE TABLE question (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES interview_session (id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    topic VARCHAR(80) NOT NULL,
    difficulty VARCHAR(16) NOT NULL CHECK (difficulty IN ('easy', 'medium', 'hard')),
    order_index INTEGER NOT NULL CHECK (order_index >= 0),
    CONSTRAINT uq_question_session_order UNIQUE (session_id, order_index)
);

CREATE TABLE answer (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL UNIQUE REFERENCES question (id) ON DELETE CASCADE,
    user_text TEXT NOT NULL,
    score NUMERIC(3, 2) NOT NULL CHECK (score >= 1 AND score <= 5),
    feedback_json JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX ix_question_session_id ON question (session_id);
