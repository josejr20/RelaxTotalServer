package com.andreutp.centromasajes.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.model.UserModel;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IUserRepository userRepository;

    private JwtUtil jwtUtil;
    private UserModel testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        
        RoleModel role = new RoleModel();
        role.setId(1L);
        role.setName("USER");

        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setRole(role);
    }

    @Test
    void testJwtAuthenticationFilter_TokenGeneration() {
        String validToken = jwtUtil.generateToken(testUser);
        assertNotNull(validToken);
        assertFalse(validToken.isEmpty());
    }

    @Test
    void testJwtAuthenticationFilter_ExtractUsername() {
        String token = jwtUtil.generateToken(testUser);
        String username = jwtUtil.extractUsername(token);
        assertNotNull(username);
    }

    @Test
    void testJwtAuthenticationFilter_IsTokenValid() {
        String token = jwtUtil.generateToken(testUser);
        boolean isValid = jwtUtil.isTokenValid(token);
        assertTrue(isValid);
    }

    @Test
    void testJwtAuthenticationFilter_ExtractUserId() {
        String token = jwtUtil.generateToken(testUser);
        Long userId = jwtUtil.extractUserId(token);
        assertNotNull(userId);
    }
}
