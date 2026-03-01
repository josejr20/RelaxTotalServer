package com.andreutp.centromasajes.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityFilterChainBean() {
        SecurityFilterChain securityFilterChain = applicationContext.getBean(SecurityFilterChain.class);
        assertNotNull(securityFilterChain);
    }

    @Test
    void testPasswordEncoderBean() {
        BCryptPasswordEncoder passwordEncoder = applicationContext.getBean(BCryptPasswordEncoder.class);
        assertNotNull(passwordEncoder);

        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
    }

    @Test
    void testAuthenticationManagerBean() {
        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        assertNotNull(authenticationManager);
    }

    @Test
    void testPasswordEncoding() {
        BCryptPasswordEncoder encoder = applicationContext.getBean(BCryptPasswordEncoder.class);

        String password = "myPassword123";
        String encoded = encoder.encode(password);
        boolean matches = encoder.matches(password, encoded);

        assertNotNull(encoded);
    }
}
