/**
 * FILE: backend/src/main/java/com/interviewprep/common/InMemoryRateLimiter.java
 *
 * WHAT IT DOES:
 *   Tracks a bounded one-minute AI-call window for each direct client address.
 *
 * ROLE IN THE PROJECT:
 *   Protects the free AI-provider quota for this single local backend process.
 *
 * IF YOU REMOVE THIS FILE:
 *   A client can make unlimited question-generation and grading calls until the provider stops them.
 *
 * DEPENDS ON: ApplicationProperties and Java collections.
 * USED BY: AiRateLimitInterceptor.
 *
 * KEY FUNCTIONS:
 *   - tryAcquire(clientAddress): records one call when the current window has capacity.
 */
package com.interviewprep.common;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.interviewprep.config.ApplicationProperties;
import org.springframework.stereotype.Component;

@Component
public class InMemoryRateLimiter {

    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final ApplicationProperties.RateLimitSettings settings;
    private final Map<String, ClientWindow> clients = new LinkedHashMap<>(16, 0.75F, true);

    public InMemoryRateLimiter(ApplicationProperties properties) {
        this.settings = properties.rateLimit();
    }

    public synchronized boolean tryAcquire(String clientAddress) {
        Instant now = Instant.now();
        removeExpiredClients(now);

        ClientWindow window = clients.get(clientAddress);
        if (window == null || window.startedAt().plus(WINDOW).isBefore(now)) {
            ensureCapacity();
            clients.put(clientAddress, new ClientWindow(now, 1));
            return true;
        }

        if (window.requestCount() >= settings.requestsPerMinute()) {
            return false;
        }

        clients.put(clientAddress, new ClientWindow(window.startedAt(), window.requestCount() + 1));
        return true;
    }

    private void removeExpiredClients(Instant now) {
        Iterator<Map.Entry<String, ClientWindow>> iterator = clients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClientWindow> entry = iterator.next();
            if (entry.getValue().startedAt().plus(WINDOW).isBefore(now)) {
                iterator.remove();
            }
        }
    }

    private void ensureCapacity() {
        if (clients.size() < settings.maxClients()) {
            return;
        }

        Iterator<String> iterator = clients.keySet().iterator();
        if (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    private record ClientWindow(Instant startedAt, int requestCount) {
    }
}
