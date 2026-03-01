package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import org.springframework.http.MediaType;

import com.andreutp.centromasajes.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    void shouldReturnOkWhenGetAllAppointments() throws Exception {
        when(appointmentService.getAllAppointments()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/appointments"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldHandleOtherEndpoints() throws Exception {
        // stub service methods for various paths
        when(appointmentService.getAppointmentsByUser(1L)).thenReturn(Collections.emptyList());
        when(appointmentService.getAppointmentsByWorker(2L)).thenReturn(Collections.emptyList());
        when(appointmentService.getAppointmentById(3L)).thenReturn(null);
        when(appointmentService.updateAppointmentStatus(3L, "DONE")).thenReturn(null);
        when(appointmentService.updateAppointment(3L, null)).thenReturn(null);

        mockMvc.perform(get("/appointments/my").param("userId", "1"))
               .andExpect(status().isOk());
        mockMvc.perform(get("/appointments/worker/2"))
               .andExpect(status().isOk());
        mockMvc.perform(get("/appointments/3"))
               .andExpect(status().isOk());
        mockMvc.perform(patch("/appointments/3/status").param("status", "DONE"))
               .andExpect(status().isOk());
        mockMvc.perform(put("/appointments/3").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(delete("/appointments/3"))
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnClientErrorWhenInvalidEndpoint() throws Exception {
        mockMvc.perform(get("/appointments/nonexistent"))
               .andExpect(status().is4xxClientError());
    }
}
