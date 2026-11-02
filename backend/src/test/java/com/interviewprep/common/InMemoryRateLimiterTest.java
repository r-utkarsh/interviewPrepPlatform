/**
 * FILE: backend/src/test/java/com/interviewprep/common/InMemoryRateLimiterTest.java
 *
 * WHAT IT DOES:
 *   Verifies the local AI quota accepts calls up to its configured limit and rejects the next one.
 *
 * ROLE IN THE PROJECT:
 *   Protects the rate-limit rule that conserves a free AI provider quota without relying on a live web server.
 *
 * IF YOU REMOVE THIS FILE:
 *   A change that accidentally permits unlimited calls can pass the backend test suite unnoticed.
 *
 * DEPENDS ON: InMemoryRateLimiter, ApplicationProperties, JUnit 5, and AssertJ.
 * USED BY: Maven's backend test suite.
 *
 * KEY FUNCTIONS:
 *   - limitsCallsForOneClientInTheCurrentWindow(): proves the third call is denied when the limit is two.
 */
package com.interviewprep.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.interviewprep.config.ApplicationProperties;
import org.junit.jupiter.api.Test;

class InMemoryRateLimiterTest {

    @Test
    void limitsCallsForOneClientInTheCurrentWindow() {
        InMemoryRateLimiter limiter = new InMemoryRateLimiter(propertiesWithTwoRequestsPerMinute());

        assertThat(limiter.tryAcquire("127.0.0.1")).isTrue();
        assertThat(limiter.tryAcquire("127.0.0.1")).isTrue();
        assertThat(limiter.tryAcquire("127.0.0.1")).isFalse();
    }

    private ApplicationProperties propertiesWithTwoRequestsPerMinute() {
        return new ApplicationProperties(
                "http://127.0.0.1:3000",
                new ApplicationProperties.RequestSettings(131_072),
                new ApplicationProperties.RateLimitSettings(2, 100),
                new ApplicationProperties.AiSettings(2, 2, 1)
        );
    }
}
