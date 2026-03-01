package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import com.andreutp.centromasajes.service.PlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @Test
    void shouldReturnOkWhenGetAllPlans() throws Exception {
        when(planService.getAllPlans()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/plans"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForPlanByIdAndModify() throws Exception {
        when(planService.getPlanById(1L)).thenReturn(null);
        when(planService.savePlan(null)).thenReturn(null);
        when(planService.updatePlan(1L, null)).thenReturn(null);
        mockMvc.perform(get("/plans/1"))
               .andExpect(status().isOk());
        mockMvc.perform(post("/plans").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(put("/plans/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(delete("/plans/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnClientErrorOnBadEndpoint() throws Exception {
        mockMvc.perform(get("/plans/unknown"))
               .andExpect(status().is4xxClientError());
    }
}
