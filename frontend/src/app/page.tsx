/**
 * FILE: frontend/src/app/page.tsx
 *
 * WHAT IT DOES:
 *   Shows a small placeholder page while the backend is built.
 *
 * ROLE IN THE PROJECT:
 *   Confirms that the Next.js shell runs; the interview workflow is added in Phase 2.
 *
 * IF YOU REMOVE THIS FILE:
 *   The frontend has no home page at the root URL.
 *
 * DEPENDS ON: React and Tailwind CSS.
 * USED BY: The browser at the frontend root URL.
 *
 * KEY FUNCTIONS:
 *   - HomePage(): renders the Phase 0 frontend placeholder.
 */
export default function HomePage() {
  return (
    <main className="mx-auto flex min-h-screen max-w-4xl flex-col justify-center px-6 py-16">
      <p className="text-sm font-semibold uppercase tracking-[0.2em] text-indigo-700">
        Interview Prep Platform
      </p>
      <h1 className="mt-4 text-4xl font-bold tracking-tight sm:text-5xl">
        Practice with questions shaped around your next role.
      </h1>
      <p className="mt-6 max-w-2xl text-lg leading-8 text-slate-600">
        The project foundation is ready. The interview flow will arrive in Phase 2 after the backend checkpoint.
      </p>
    </main>
  );
}
