/**
 * FILE: frontend/postcss.config.mjs
 *
 * WHAT IT DOES:
 *   Connects Tailwind CSS to the Next.js stylesheet build.
 *
 * ROLE IN THE PROJECT:
 *   Turns Tailwind utility classes into browser CSS.
 *
 * IF YOU REMOVE THIS FILE:
 *   Tailwind styles in frontend/src/app/globals.css stop being generated.
 *
 * DEPENDS ON: @tailwindcss/postcss.
 * USED BY: Next.js when it compiles frontend CSS.
 *
 * KEY FUNCTIONS:
 *   - Default export: names the PostCSS plugins used by Next.js.
 */
const config = {
  plugins: {
    "@tailwindcss/postcss": {},
  },
};

export default config;
