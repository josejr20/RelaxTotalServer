package com.andreutp.centromasajes.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
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

    @Test
    void enviarReportesEmail_shouldReturnOkMessage() {
        doNothing().when(reportService).enviarReporteClientes(anyString());
        doNothing().when(reportService).enviarReporteTrabajadores(anyString());
        doNothing().when(reportService).enviarReporteServicios(anyString());
        doNothing().when(reportService).enviarReporteReservas(anyString());
        doNothing().when(reportService).enviarReportePagosUsuario(eq(1L), anyString());

        ResponseEntity<String> r1 = controller.enviarReporteClientes("a@b.com");
        assertEquals("Reporte de clientes enviado al correo: a@b.com", r1.getBody());

        ResponseEntity<String> r2 = controller.enviarReporteTrabajadores("x@x.com");
        assertEquals("Reporte de trabajadores enviado al correo: x@x.com", r2.getBody());

        ResponseEntity<String> r3 = controller.enviarReporteServicios("y@y.com");
        assertEquals("Reporte de servicios enviado al correo: y@y.com", r3.getBody());

        ResponseEntity<String> r4 = controller.enviarReporteReservas("z@z.com");
        assertEquals("Reporte de reservas enviado al correo: z@z.com", r4.getBody());

        ResponseEntity<String> r5 = controller.enviarReporte(1L, "p@p.com");
        assertEquals("Reporte enviado al correo: p@p.com", r5.getBody());
    }

    @Test
    void enviarFacturaEmail_simpleAndMultipleItems() {
        ReportController.FacturaEmailRequest req = new ReportController.FacturaEmailRequest();
        req.setCorreo("foo@bar.com");
        req.setCliente("cliente");
        req.setDescripcion("desc");
        req.setTotal(100.0);
        req.setCantidad(1);
        req.setMetodoPago("tarjeta");
        req.setNumeroPedido("P123");
        req.setNumeroFactura("F123");

        doNothing().when(reportService).enviarFacturaPdf(anyString(), anyString(), anyString(), anyDouble(), anyString(), anyInt(), anyString());
        // no return value; we just ensure it is callable
        

        ResponseEntity<String> resp = controller.enviarFacturaEmail(req);
        assertEquals(202, resp.getStatusCode().value());
        assertEquals("Factura enviada a: foo@bar.com", resp.getBody());

        // now test multi-item version
        req = new ReportController.FacturaEmailRequest();
        req.setCorreo("multi@bar.com");
        req.setItems(java.util.List.of(new ReportController.ItemFacturaDTO()));
        doNothing().when(reportService).enviarFacturaPdfMultiple(anyString(), anyString(), anyString(), anyString(), anyString(), anyList());

        ResponseEntity<String> resp2 = controller.enviarFacturaEmail(req);
        assertEquals(202, resp2.getStatusCode().value());
        assertEquals("Factura enviada a: multi@bar.com", resp2.getBody());
    }
}
