package com.andreutp.centromasajes.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOkForValidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

        mockMvc.perform(multipart("/upload/promotion-image").file(file))
               .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientErrorWhenMissingParam() throws Exception {
        mockMvc.perform(multipart("/upload/promotion-image"))
               .andExpect(status().is4xxClientError());
    }
}
