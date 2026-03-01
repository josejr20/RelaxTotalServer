package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.util.Optional;

import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dao.IWorkerAvailabilityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WorkAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IWorkerAvailabilityRepository availabilityRepository;

    @MockBean
    private IUserRepository userRepository;

    @Test
    void shouldReturnOkForGetByWorker() throws Exception {
        when(availabilityRepository.findByWorkerId(1L)).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/availability/1"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForCreateAndDelete() throws Exception {
        // user repository and availability repository stubs
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(new com.andreutp.centromasajes.model.UserModel()));
        when(availabilityRepository.save(null)).thenReturn(null);
        mockMvc.perform(post("/availability/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(delete("/availability/1"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientErrorOnBadUrl() throws Exception {
        mockMvc.perform(get("/availability/invalid"))
               .andExpect(status().is4xxClientError());
    }
}
