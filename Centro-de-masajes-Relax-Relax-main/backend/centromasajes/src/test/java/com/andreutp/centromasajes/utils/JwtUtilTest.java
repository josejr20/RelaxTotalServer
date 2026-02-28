package com.andreutp.centromasajes.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.model.UserModel;
import com.andreutp.centromasajes.security.JwtUtil;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(42L);
        user.setUsername("johndoe");
        RoleModel role = new RoleModel();
        role.setName("USER");
        user.setRole(role);
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtUtil.generateToken(user);
        assertNotNull(token);

        assertEquals("johndoe", jwtUtil.extractUsername(token));
        assertEquals(42L, jwtUtil.extractUserId(token));
        assertEquals("USER", jwtUtil.extractRole(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void invalidTokenReturnsFalse() {
        String token = jwtUtil.generateToken(user) + "garbage";
        assertFalse(jwtUtil.isTokenValid(token));
    }

    @Test
    void expiredToken() {
        // create a token that already expired
        String expired = io.jsonwebtoken.Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 1000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "laClaveSuperSecretaParaJWT_123456789xd".getBytes()),
                        io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
        assertFalse(jwtUtil.isTokenValid(expired));
    }
}
