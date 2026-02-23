package com.andreutp.centromasajes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.andreutp.centromasajes.dao.IRoleRepository;
import com.andreutp.centromasajes.model.RoleModel;

@DataJpaTest
@ActiveProfiles("test")
class IRoleRepositoryTest {

    @Autowired
    private IRoleRepository roleRepository;

    @Test
    void testSaveRole() {
        RoleModel role = new RoleModel();
        role.setName("ADMIN");

        RoleModel saved = roleRepository.save(role);

        assertNotNull(saved.getId());
        assertEquals("ADMIN", saved.getName());
    }
}