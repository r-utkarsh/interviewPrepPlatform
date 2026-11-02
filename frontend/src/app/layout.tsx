/**
 * FILE: frontend/src/app/layout.tsx
 *
 * WHAT IT DOES:
 *   Defines the shared HTML document, metadata, and global stylesheet for the app.
 *
 * ROLE IN THE PROJECT:
 *   Wraps every page in the Next.js App Router.
 *
 * IF YOU REMOVE THIS FILE:
 *   Next.js has no root document or shared layout for the frontend.
 *
 * DEPENDS ON: Next.js metadata types and frontend/src/app/globals.css.
 * USED BY: Every route under frontend/src/app.
 *
 * KEY FUNCTIONS:
 *   - RootLayout({ children }): renders the shared document around the active page.
 */
import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Interview Prep Platform",
  description: "A guided workspace for practicing technical interviews.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
