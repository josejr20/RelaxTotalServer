package com.andreutp.centromasajes.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class UserModelTest {

    @Test
    void fullConstructorAndAccessors() {
        RoleModel role = new RoleModel();
        role.setId(1L);
        role.setName("USER");

        UserModel user = new UserModel(
                42L,
                "user1",
                "pass",
                "123456789",
                "a@b.com",
                true,
                "87654321",
                role,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptyList(),
                "especial",
                "activo",
                5
        );

        assertEquals(42L, user.getId());
        assertEquals("user1", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals("123456789", user.getPhone());
        assertEquals("a@b.com", user.getEmail());
        assertEquals(true, user.getEnabled());
        assertEquals("87654321", user.getDni());
        assertEquals(role, user.getRole());
        assertEquals("activo", user.getEstado());
        assertEquals(5, user.getExperiencia());

        // setters
        user.setEstado("inactivo");
        assertEquals("inactivo", user.getEstado());
    }
}
