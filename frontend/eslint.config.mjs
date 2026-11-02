/**
 * FILE: frontend/eslint.config.mjs
 *
 * WHAT IT DOES:
 *   Enables the Next.js Core Web Vitals lint rules.
 *
 * ROLE IN THE PROJECT:
 *   Catches common frontend bugs and unsafe patterns during development.
 *
 * IF YOU REMOVE THIS FILE:
 *   The frontend lint command has no Next.js project rules to apply.
 *
 * DEPENDS ON: eslint and eslint-config-next.
 * USED BY: The frontend npm run lint command.
 *
 * KEY FUNCTIONS:
 *   - Default export: supplies the flat ESLint configuration.
 */
import nextVitals from "eslint-config-next/core-web-vitals";

const config = [...nextVitals];

export default config;
