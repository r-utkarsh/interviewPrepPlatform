/**
 * FILE: frontend/next.config.ts
 *
 * WHAT IT DOES:
 *   Sets browser security headers for every frontend route.
 *
 * ROLE IN THE PROJECT:
 *   Applies the baseline browser protections required for the interview platform.
 *
 * IF YOU REMOVE THIS FILE:
 *   The frontend starts without the configured Content Security Policy and related headers.
 *
 * DEPENDS ON: Next.js configuration APIs.
 * USED BY: The Next.js development and production servers.
 *
 * KEY FUNCTIONS:
 *   - headers(): returns security headers for all routes.
 */
import type { NextConfig } from "next";

const contentSecurityPolicy = [
  "default-src 'self'",
  "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
  "style-src 'self' 'unsafe-inline'",
  "img-src 'self' data:",
  "font-src 'self' data:",
  "connect-src 'self' http://127.0.0.1:8080 ws://127.0.0.1:3000",
  "object-src 'none'",
  "base-uri 'self'",
  "form-action 'self'",
  "frame-ancestors 'none'",
].join("; ");

const nextConfig: NextConfig = {
  async headers() {
    return [
      {
        source: "/:path*",
        headers: [
          { key: "Content-Security-Policy", value: contentSecurityPolicy },
          { key: "X-Content-Type-Options", value: "nosniff" },
          { key: "X-Frame-Options", value: "DENY" },
          { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
          { key: "Permissions-Policy", value: "camera=(), microphone=(), geolocation=()" },
        ],
      },
    ];
  },
};

export default nextConfig;
