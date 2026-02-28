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

    @Test
    void descargarOtherReports_shouldUseHelper() {
        byte[] dummy = new byte[]{4,5,6};
        when(reportService.generarExcelTrabajadores()).thenReturn(dummy);
        ResponseEntity<byte[]> resp1 = controller.descargarReporteTrabajadores();
        assertEquals(200, resp1.getStatusCode().value());
        assertArrayEquals(dummy, resp1.getBody());
        assertNotNull(resp1.getHeaders().getFirst("Content-Disposition"));

        when(reportService.generarExcelServicios()).thenReturn(dummy);
        ResponseEntity<byte[]> resp2 = controller.descargarReporteServicios();
        assertEquals(200, resp2.getStatusCode().value());
        assertArrayEquals(dummy, resp2.getBody());

        when(reportService.generarExcelReservas()).thenReturn(dummy);
        ResponseEntity<byte[]> resp3 = controller.descargarReporteReservas();
        assertEquals(200, resp3.getStatusCode().value());
        assertArrayEquals(dummy, resp3.getBody());
    }
}
