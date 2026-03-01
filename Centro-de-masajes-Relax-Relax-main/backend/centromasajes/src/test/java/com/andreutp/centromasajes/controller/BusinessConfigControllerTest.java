package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andreutp.centromasajes.model.BusinessConfigModel;
import org.springframework.http.MediaType;
import com.andreutp.centromasajes.service.BusinessConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessConfigService service;

    @Test
    void shouldReturnOkWhenGetConfig() throws Exception {
        when(service.getConfig()).thenReturn(new BusinessConfigModel());
        mockMvc.perform(get("/config"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenUpdateConfig() throws Exception {
        when(service.updateConfig(new BusinessConfigModel())).thenReturn(new BusinessConfigModel());
        mockMvc.perform(put("/config").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientErrorWhenInvalidUrl() throws Exception {
        mockMvc.perform(get("/config/unknown"))
               .andExpect(status().is4xxClientError());
    }
}
