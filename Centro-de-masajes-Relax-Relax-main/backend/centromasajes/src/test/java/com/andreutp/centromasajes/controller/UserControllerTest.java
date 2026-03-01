package com.andreutp.centromasajes.controller;

import com.andreutp.centromasajes.dao.IRoleRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dao.IWorkerAvailabilityRepository;
import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.model.UserModel;
import com.andreutp.centromasajes.model.RoleModel;
import com.andreutp.centromasajes.security.CustomUserDetailsService;
import com.andreutp.centromasajes.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private IRoleRepository roleRepository;

    @MockitoBean
    private IWorkerAvailabilityRepository availabilityRepository;

    @MockitoBean
    private IUserRepository userRepository;

    @MockitoBean
    private IAppointmentRepository appointmentRepository;

    @Test
    @WithMockUser
    void testGetUserById() throws Exception {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setUsername("andre");

        when(userService.getById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("andre"));
    }

    @Test
    @WithMockUser
    void testGetUsers_Success() throws Exception {
        UserModel u1 = new UserModel(); u1.setId(1L);
        UserModel u2 = new UserModel(); u2.setId(2L);
        when(userService.getUsers()).thenReturn(java.util.List.of(u1, u2));

        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void testSaveUser_Success() throws Exception {
        UserModel u = new UserModel(); u.setId(5L); u.setUsername("foo");
        u.setEmail("foo@bar.com");
        u.setPassword("pwd123");
        u.setPhone("123456789");
        u.setDni("87654321");
        when(userService.saveUser(any(UserModel.class))).thenReturn(u);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @WithMockUser
    void testUpdateUserById_Success() throws Exception {
        UserModel u = new UserModel(); u.setUsername("bar");
        u.setEmail("bar@baz.com");
        u.setPassword("pwd123");
        u.setPhone("987654321");
        u.setDni("12344321");
        when(userService.updateById(any(UserModel.class), eq(1L))).thenReturn(u);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bar"));
    }

    @Test
    @WithMockUser
    void testDeleteById_results() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);
        mockMvc.perform(delete("/user/1")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("se elimino")));

        when(userService.deleteUser(2L)).thenReturn(false);
        mockMvc.perform(delete("/user/2")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("no se elimino")));
    }

    @Test
    @WithMockUser
    void testGetClientsAndWorkers() throws Exception {
        when(userService.getClients()).thenReturn(java.util.List.of(new com.andreutp.centromasajes.dto.UserClientDTO()));
        when(userService.getWorkers()).thenReturn(java.util.List.of(new com.andreutp.centromasajes.dto.UserWorkerDTO()));

        mockMvc.perform(get("/user/clients")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/user/workers")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void testSaveWorker_andAvailabilitySlots() throws Exception {
        RoleModel role = new RoleModel(); role.setName("WORKER");
        when(roleRepository.findByName("WORKER")).thenReturn(role);
        UserModel saved = new UserModel(); saved.setId(10L);
        when(userService.saveUser(any(UserModel.class))).thenReturn(saved);

        UserModel worker = new UserModel(); worker.setUsername("w");
        mockMvc.perform(post("/user/worker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(worker)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        // availability by day empty when user not found
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/user/worker/99/availability/MARTES").param("durationMinutes","30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void testSaveWorkerAvailability_endpoints() throws Exception {
        // success
        doNothing().when(userService).saveWorkerAvailability(eq(1L), anyList());
        mockMvc.perform(post("/user/worker/1/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        // failure leads to bad request
        doThrow(new RuntimeException("fail")).when(userService).saveWorkerAvailability(eq(2L), anyList());
        mockMvc.perform(post("/user/worker/2/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }

}
