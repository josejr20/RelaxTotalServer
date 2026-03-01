package com.andreutp.centromasajes.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private UserModel testUser;

    @org.junit.jupiter.api.BeforeEach
    void initUtil() {
        // provide a fixed secret for deterministic testing
        jwtUtil = new JwtUtil("testsecrettestsecrettestsecret12");
    }

    @BeforeEach
    void setUp() {
        RoleModel role = new RoleModel();
        role.setId(1L);
        role.setName("USER");

        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole(role);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(testUser);

        assertNotNull(token);
        assertNotNull(jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(testUser);
        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void testExtractUserId() {
        String token = jwtUtil.generateToken(testUser);
        Long userId = jwtUtil.extractUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    void testExtractRole() {
        String token = jwtUtil.generateToken(testUser);
        String role = jwtUtil.extractRole(token);

        assertEquals("USER", role);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtUtil.generateToken(testUser);

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        String secretKey = "testsecrettestsecrettestsecret12";
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1800000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        assertFalse(jwtUtil.isTokenValid(expiredToken));
    }

    @Test
    void testGenerateToken_ContainsClaims() {
        String token = jwtUtil.generateToken(testUser);

        Long userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        assertEquals(1L, userId);
        assertEquals("USER", role);
    }
}
