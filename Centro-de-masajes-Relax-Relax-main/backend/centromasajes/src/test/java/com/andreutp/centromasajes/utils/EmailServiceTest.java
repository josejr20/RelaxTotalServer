package com.andreutp.centromasajes.utils;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic; // for static mocks
import com.andreutp.centromasajes.exception.BusinessException;

public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
       when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testEnviarCorreoSimple() {
        emailService.enviarCorreoSimple("cliente@test.com", "Asunto", "Mensaje");

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testEnviarCorreoSimple_failure() {
        when(mailSender.send(mimeMessage)).thenThrow(new RuntimeException("bad"));
        BusinessException ex = assertThrows(BusinessException.class, () ->
                emailService.enviarCorreoSimple("cliente@test.com", "Asunto", "Mensaje"));
        assertTrue(ex.getMessage().contains("Error enviando correo"));
    }

    @Test
    void testEnviarBoletaConPDF() {
        // Verify normal flow and static PDF call
        try (MockedStatic<PdfGenerator> mocked = mockStatic(PdfGenerator.class)) {
            byte[] dummy = new byte[]{1,2,3};
            mocked.when(() -> PdfGenerator.generateInvoicePdf("Juan", "001", 150.0)).thenReturn(dummy);

            assertDoesNotThrow(() -> emailService.enviarBoletaConPDF("cliente@test.com",
                    "Boleta", "Juan", "001", 150.0));

            mocked.verify(() -> PdfGenerator.generateInvoicePdf("Juan", "001", 150.0));
            verify(mailSender, times(1)).createMimeMessage();
            verify(mailSender, times(1)).send(mimeMessage);
        }
    }

    @Test
    void testEnviarBoletaConPDF_failure() {
        // simulate send error
        when(mailSender.send(mimeMessage)).thenThrow(new RuntimeException("smtp failure"));
        BusinessException ex = assertThrows(BusinessException.class, () ->
                emailService.enviarBoletaConPDF("cliente@test.com", "Boleta", "Juan", "001", 150.0));
        assertTrue(ex.getMessage().contains("Error enviando correo"));
    }

    @Test
    void testEnviarCorreoConAdjunto() {
        byte[] archivo = "contenido".getBytes();

        assertDoesNotThrow(() -> emailService.enviarCorreoConAdjunto(
                "cliente@test.com", "Asunto", "Mensaje", archivo, "archivo.txt"));

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testEnviarCorreoConAdjunto_failure() {
        byte[] archivo = "contenido".getBytes();
        when(mailSender.send(mimeMessage)).thenThrow(new RuntimeException("fail"));
        BusinessException ex = assertThrows(BusinessException.class, () ->
                emailService.enviarCorreoConAdjunto("cliente@test.com", "Asunto",
                        "Mensaje", archivo, "archivo.txt"));
        assertTrue(ex.getMessage().contains("Error enviando correo"));
    }

    @Test
    void testEnviarFacturaA4ConPDF() {
        try (MockedStatic<PdfGenerator> mocked = mockStatic(PdfGenerator.class)) {
            byte[] fbytes = new byte[]{9,8,7};
            mocked.when(() -> PdfGenerator.generateInvoiceA4Pdf(
                    "Juan", "Factura001", "Masajes", 2, 300.0, "Visa", "PED123"))
                    .thenReturn(fbytes);

            assertDoesNotThrow(() -> emailService.enviarFacturaA4ConPDF(
                    "cliente@test.com", "Juan", "Masajes", 2, 300.0,
                    "Visa", "Factura001", "PED123"));

            mocked.verify(() -> PdfGenerator.generateInvoiceA4Pdf(
                    "Juan", "Factura001", "Masajes", 2, 300.0, "Visa", "PED123"));
            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void testEnviarFacturaA4ConPDF_failure() {
        when(mailSender.send(mimeMessage)).thenThrow(new RuntimeException("smtp"));
        BusinessException ex = assertThrows(BusinessException.class, () ->
                emailService.enviarFacturaA4ConPDF("cliente@test.com", "Juan", "Masajes", 2,
                        300.0, "Visa", "Factura001", "PED123"));
        assertTrue(ex.getMessage().contains("Error enviando factura"));
    }
}
