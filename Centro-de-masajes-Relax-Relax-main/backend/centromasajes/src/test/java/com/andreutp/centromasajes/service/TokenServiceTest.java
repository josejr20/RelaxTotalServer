package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testStoreToken() {
        String token = "test-token-123";
        String email = "test@example.com";

        tokenService.storeToken(token, email);
        String result = tokenService.validateToken(token);

        assertEquals(email, result);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = "valid-token";
        String email = "user@example.com";

        tokenService.storeToken(token, email);
        String result = tokenService.validateToken(token);

        assertNotNull(result);
        assertEquals(email, result);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String result = tokenService.validateToken("non-existent-token");

        assertEquals(null, result);
    }

    @Test
    void testRemoveToken() {
        String token = "test-token-to-remove";
        tokenService.storeToken(token, "test@example.com");

        tokenService.removeToken(token);
        String result = tokenService.validateToken(token);

        assertEquals(null, result);
    }

    @Test
    void testStoreMultipleTokens() {
        tokenService.storeToken("token1", "email1@example.com");
        tokenService.storeToken("token2", "email2@example.com");

        assertEquals("email1@example.com", tokenService.validateToken("token1"));
        assertEquals("email2@example.com", tokenService.validateToken("token2"));
    }
}
