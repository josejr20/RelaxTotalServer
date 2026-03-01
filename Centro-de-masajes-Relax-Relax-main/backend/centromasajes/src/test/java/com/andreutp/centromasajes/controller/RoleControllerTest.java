package com.andreutp.centromasajes.controller;

import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.security.CustomUserDetailsService;
import com.andreutp.centromasajes.security.JwtAuthenticationFilter;
import com.andreutp.centromasajes.security.JwtUtil;
import com.andreutp.centromasajes.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RoleController.class)
public class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testGetRoleById() throws Exception {
        RoleModel role = new RoleModel(1L, "ADMIN");

        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));

        mockMvc.perform(get("/roles/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void testGetRoleById_NotFound() throws Exception {
        when(roleService.getRoleById(42L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/roles/42")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllRoles() throws Exception {
        RoleModel r1 = new RoleModel(1L, "ADMIN");
        RoleModel r2 = new RoleModel(2L, "USER");
        when(roleService.getAllRoles()).thenReturn(java.util.List.of(r1, r2));

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" ).value(2));
    }

    @Test
    void testSaveRole() throws Exception {
        RoleModel r = new RoleModel(null, "NEWROLE");
        RoleModel saved = new RoleModel(5L, "NEWROLE");
        when(roleService.saveRole(any(RoleModel.class))).thenReturn(saved);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NEWROLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("NEWROLE"));
    }

    @Test
    void testUpdateRole_Success() throws Exception {
        RoleModel updated = new RoleModel(1L, "UPDATED");
        when(roleService.updateRole(eq(1L), any(RoleModel.class))).thenReturn(updated);

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UPDATED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));
    }

    @Test
    void testUpdateRole_NotFound() throws Exception {
        when(roleService.updateRole(eq(99L), any(RoleModel.class)))
                .thenThrow(new RuntimeException("not found"));

        mockMvc.perform(put("/roles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRole() throws Exception {
        // delete returns void, just verify response
        mockMvc.perform(delete("/roles/7"))
                .andExpect(status().isNoContent());
    }
}
