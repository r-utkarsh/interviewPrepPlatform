# File Index

This index describes files that cannot carry the required comment header, plus generated files that are grouped here. Code and configuration files with comments carry their own headers.

## Table of contents

- [Frontend JSON files](#frontend-json-files)
- [Maven Wrapper files](#maven-wrapper-files)
- [Generated files](#generated-files)

## Frontend JSON files

### `frontend/package.json`

- **WHAT IT DOES:** Pins frontend dependencies and defines development, build, start, and lint commands.
- **ROLE IN THE PROJECT:** Reproducibly installs and runs the Next.js frontend.
- **IF YOU REMOVE THIS FILE:** npm cannot install dependencies or run frontend scripts.
- **DEPENDS ON:** npm package registry and the project source.
- **USED BY:** npm and the frontend developer.
- **KEY FUNCTIONS:** `dev` runs locally; `build` creates a production build; `start` serves it; `lint` checks source.

### `frontend/tsconfig.json`

- **WHAT IT DOES:** Enables strict TypeScript checks and configures module resolution and the `@/*` alias.
- **ROLE IN THE PROJECT:** Keeps frontend code type-safe and imports readable.
- **IF YOU REMOVE THIS FILE:** Next.js loses the project's TypeScript rules and alias configuration.
- **DEPENDS ON:** TypeScript and Next.js.
- **USED BY:** Next.js and TypeScript tooling.
- **KEY FUNCTIONS:** Compiler options control type checking; include and exclude lists select source files.

## Maven Wrapper files

### `mvnw` and `mvnw.cmd`

- **WHAT THEY DO:** Download and verify Maven 3.9.12 on first use, cache it, and run the requested Maven command on POSIX or Windows.
- **ROLE IN THE PROJECT:** Let the owner build with the same Maven version without installing Maven globally.
- **IF YOU REMOVE THESE FILES:** The owner needs a system Maven installation to run the backend build.
- **DEPENDS ON:** Java, network access for the first run, and `.mvn/wrapper/maven-wrapper.properties`.
- **USED BY:** Backend developers and CI jobs on matching operating systems.
- **KEY FUNCTIONS:** Each script locates the configured Maven distribution, checks its SHA-256 checksum, and invokes it.

### `.mvn/wrapper/maven-wrapper.properties`

- **WHAT IT DOES:** Pins Maven Wrapper 3.3.4 and Maven 3.9.12 and provides the distribution checksum.
- **ROLE IN THE PROJECT:** Makes wrapper downloads reproducible and verifiable.
- **IF YOU REMOVE THIS FILE:** The wrapper cannot determine which Maven distribution to download.
- **DEPENDS ON:** Apache Maven Central.
- **USED BY:** `mvnw` and `mvnw.cmd`.
- **KEY FUNCTIONS:** `distributionUrl` selects Maven and `distributionSha256Sum` validates the archive.

## Generated files

- `frontend/package-lock.json`: npm's exact dependency resolution; generated from `frontend/package.json` by `npm install` and consumed by `npm ci`.
- `frontend/next-env.d.ts`, `frontend/.next/`, `frontend/node_modules/`, `frontend/coverage/`, `backend/target/`, `.mvn/wrapper/dists/`: generated type declarations, build output, package caches, test output, compiled backend files, and the Maven distribution cache. These are ignored by Git and must not be edited by hand.
