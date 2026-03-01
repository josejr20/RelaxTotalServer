package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import com.andreutp.centromasajes.service.PromotionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromotionService promotionService;

    @Test
    void shouldReturnOkForListPromotions() throws Exception {
        when(promotionService.getAllPromotions()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/promotions"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldAllowCrudOperations() throws Exception {
        when(promotionService.createPromotion(null)).thenReturn(null);
        when(promotionService.getPromotionById(1L)).thenReturn(null);
        when(promotionService.updatePromotion(1L, null)).thenReturn(null);
        mockMvc.perform(post("/promotions").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(get("/promotions/1"))
               .andExpect(status().isOk());
        mockMvc.perform(put("/promotions/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
        mockMvc.perform(delete("/promotions/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnClientErrorForInvalid() throws Exception {
        mockMvc.perform(get("/promotions/invalid"))
               .andExpect(status().is4xxClientError());
    }
}
