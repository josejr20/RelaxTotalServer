package com.andreutp.centromasajes.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.andreutp.centromasajes.service.ReportService;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void descargarClientes_shouldReturnExcelWithHeaders() {
        byte[] dummy = new byte[]{1,2,3};
        when(reportService.generarExcelClientes()).thenReturn(dummy);

        ResponseEntity<byte[]> resp = controller.descargarReporteClientes();
        assertEquals(200, resp.getStatusCode().value());
        assertArrayEquals(dummy, resp.getBody());
        assertNotNull(resp.getHeaders().getFirst("Content-Disposition"));
        assertTrue(resp.getHeaders().getFirst("Content-Disposition").contains("ReporteClientes.xlsx"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                resp.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void descargarBoletaDemo_shouldReturnPdf() {
        // PdfGenerator is static, we don't need to mock for this simple invocation
        ResponseEntity<byte[]> resp = controller.descargarBoletaDemo();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals("application/pdf", resp.getHeaders().getFirst("Content-Type"));
        assertNotNull(resp.getHeaders().getFirst("Content-Disposition"));
        assertTrue(resp.getHeaders().getFirst("Content-Disposition").contains("BoletaDemo.pdf"));
    }
}
