package com.andreutp.centromasajes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.andreutp.centromasajes.dao.IRoleRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.model.UserModel;

@DataJpaTest
@ActiveProfiles("test")
class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Test
    void testSaveUser() {

        RoleModel role = new RoleModel();
        role.setName("USER");
        RoleModel savedRole = roleRepository.save(role);

        UserModel user = new UserModel();
        user.setUsername("testuser");
        user.setPassword("123456");
        user.setEmail("test@test.com");
        user.setEnabled(true);
        user.setDni("12345678"); // obligatorio
        user.setPhone("987654321"); // obligatorio
        user.setRole(savedRole);

        UserModel savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }
}