package com.andreutp.centromasajes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andreutp.centromasajes.dto.InvoiceRequest;
import com.andreutp.centromasajes.model.InvoiceModel;
import com.andreutp.centromasajes.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Test
    void shouldReturnOkWhenGettingAll() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/invoices"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenCreateInvoice() throws Exception {
        when(invoiceService.createInvoice(null)).thenReturn(new InvoiceModel());
        mockMvc.perform(post("/invoices").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientErrorForUnknown() throws Exception {
        mockMvc.perform(get("/invoices/unknown"))
               .andExpect(status().is4xxClientError());
    }
}
