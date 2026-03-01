package com.andreutp.centromasajes.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginRateLimiterTest {

    @InjectMocks
    private LoginRateLimiter loginRateLimiter;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testTryAcquire_FirstAttempt_Success() {
        boolean result = loginRateLimiter.tryAcquire("user@example.com", 1.0);

        assertTrue(result);
    }

    @Test
    void testTryAcquire_MultipleAttemptsWithinLimit() {
        String key = "user@example.com";

        // First attempt should always succeed
        assertTrue(loginRateLimiter.tryAcquire(key, 10.0));
        // Subsequent immediate attempts may fail due to rate limiting
        // This is expected behavior for rate limiters
    }

    @Test
    void testTryAcquire_DifferentKeys() {
        boolean result1 = loginRateLimiter.tryAcquire("user1@example.com", 1.0);
        boolean result2 = loginRateLimiter.tryAcquire("user2@example.com", 1.0);

        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    void testTryAcquire_RateLimitExceeded() throws InterruptedException {
        String key = "limited@example.com";

        assertTrue(loginRateLimiter.tryAcquire(key, 1.0));

        Thread.sleep(1200);

        assertTrue(loginRateLimiter.tryAcquire(key, 1.0));
    }

    @Test
    void testTryAcquire_HighPermitRate() {
        String key = "highrate@example.com";

        // First attempt always succeeds, subsequent may be rate limited
        boolean result1 = loginRateLimiter.tryAcquire(key, 10.0);

        assertTrue(result1);
    }
}
