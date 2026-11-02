#!/usr/bin/env bash
# FILE: db/init/10-create-app-role.sh
# WHAT IT DOES: Creates the backend database role and grants access only to the app database schema.
# ROLE IN THE PROJECT: Keeps the backend from connecting as PostgreSQL's administrative user.
# IF YOU REMOVE THIS FILE: A fresh database has no restricted account for the backend to use.
# DEPENDS ON: PostgreSQL's initialization environment variables from docker-compose.yml.
# USED BY: The official PostgreSQL image during first database initialization.
# KEY FUNCTIONS: The SQL block creates the login role and grants CONNECT, USAGE, and CREATE.

set -eu

psql \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  --set=ON_ERROR_STOP=1 \
  --set=app_user="$POSTGRES_APP_USER" \
  --set=app_password="$POSTGRES_APP_PASSWORD" \
  --set=app_database="$POSTGRES_DB" <<'SQL'
CREATE ROLE :"app_user" LOGIN PASSWORD :'app_password';
GRANT CONNECT ON DATABASE :"app_database" TO :"app_user";
GRANT USAGE, CREATE ON SCHEMA public TO :"app_user";
SQL
