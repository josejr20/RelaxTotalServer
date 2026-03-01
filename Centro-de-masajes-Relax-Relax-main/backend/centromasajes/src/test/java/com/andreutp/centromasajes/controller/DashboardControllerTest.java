package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andreutp.centromasajes.dto.DashboardStatsDTO;
import com.andreutp.centromasajes.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void shouldReturnOkForStats() throws Exception {
        when(dashboardService.getDashboardStats()).thenReturn(new DashboardStatsDTO());
        mockMvc.perform(get("/api/dashboard/stats"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForMonthlyRevenue() throws Exception {
        when(dashboardService.getMonthlyRevenue(6)).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/api/dashboard/revenue/monthly"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientErrorForUnknownEndpoint() throws Exception {
        mockMvc.perform(get("/api/dashboard/foo"))
               .andExpect(status().is4xxClientError());
    }
}
