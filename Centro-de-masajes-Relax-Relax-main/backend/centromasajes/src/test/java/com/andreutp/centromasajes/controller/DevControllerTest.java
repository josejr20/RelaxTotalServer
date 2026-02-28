package com.andreutp.centromasajes.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.andreutp.centromasajes.utils.EmailService;

class DevControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DevController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEmail_invalidTo_returnsBadRequest() {
        ResponseEntity<String> resp = controller.testEmail(null);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void testEmail_validTo_sendsMessage() {
        doNothing().when(emailService).enviarCorreoConAdjunto(
                anyString(), anyString(), anyString(), any(byte[].class), anyString());
        ResponseEntity<String> resp = controller.testEmail("foo@bar.com");
        assertEquals(200, resp.getStatusCode().value());
        assertEquals("Correo enviado correctamente!", resp.getBody());
    }

    @Test
    void testExcel_invalidTo_returnsBadRequest() {
        ResponseEntity<String> resp = controller.testExcel("");
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void testExcel_validTo_sendsMessage() {
        doNothing().when(emailService).enviarCorreoConAdjunto(
                anyString(), anyString(), anyString(), any(byte[].class), anyString());
        ResponseEntity<String> resp = controller.testExcel("foo@bar.com");
        assertEquals(200, resp.getStatusCode().value());
        assertEquals("Correo con Excel enviado!", resp.getBody());
    }
}
