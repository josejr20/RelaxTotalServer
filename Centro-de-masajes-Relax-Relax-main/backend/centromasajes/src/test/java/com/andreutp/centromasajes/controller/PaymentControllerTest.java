package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andreutp.centromasajes.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void shouldReturnOkWhenListPayments() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/payments"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkForUserPayments() throws Exception {
        when(paymentService.getPaymentsByUser(1L)).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/payments/my").param("userId", "1"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldErrorForBadUrl() throws Exception {
        mockMvc.perform(get("/payments/unknown"))
               .andExpect(status().is4xxClientError());
    }
}
