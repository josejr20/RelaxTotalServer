package com.andreutp.centromasajes.utils;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.exception.BusinessException;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.ServiceModel;
import com.andreutp.centromasajes.model.UserModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ExcelReportGeneratorTest {

    /* Helper to open workbook bytes and return sheet by name */
    private Sheet sheetFromBytes(byte[] bytes, String sheetName) throws Exception {
        File tempFile = File.createTempFile("excel_", ".xlsx");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), bytes);
        try (FileInputStream fis = new FileInputStream(tempFile);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            return wb.getSheet(sheetName);
        }
    }

    // ---------------- pagos ----------------
    @Test
    void pagos_emptyList_producesPlaceholderRow() throws Exception {
        byte[] out = ExcelReportGenerator.generarReportePagos(Collections.emptyList());
        assertNotNull(out);
        assertTrue(out.length > 0);

        Sheet s = sheetFromBytes(out, "Pagos");
        // header exists
        assertNotNull(s.getRow(0));
        // no data rows -> size 1 (header only)
        assertEquals(1, s.getPhysicalNumberOfRows());
    }

    @Test
    void pagos_withData_and_nullFields_and_rowAlternation() throws Exception {
        UserModel user1 = new UserModel(1L, "cliente1", "p", "999999999", "c1@test", true, "12345678", null, null, null, null, null, null, 2);
        PaymentModel p1 = PaymentModel.builder()
                .id(10L)
                .user(user1)
                .amount(150.0)
                .method("CASH")
                .createdAt(LocalDateTime.of(2023,1,1,10,0))
                .build();

        UserModel user2 = new UserModel(2L, "cliente2", "p", "999999998", "c2@test", true, "87654321", null, null, null, null, null, null, 5);
        PaymentModel p2 = PaymentModel.builder()
                .id(11L)
                .user(user2)
                .amount(200.0)
                .method("CARD")
                .createdAt(null) // exercise ternary
                .build();

        List<PaymentModel> list = Arrays.asList(p1, p2);
        byte[] out = ExcelReportGenerator.generarReportePagos(list);
        assertNotNull(out);
        assertTrue(out.length > 0);

        Sheet s = sheetFromBytes(out, "Pagos");
        // header + 2 rows
        assertEquals(3, s.getPhysicalNumberOfRows());
        Row r1 = s.getRow(1);
        assertEquals("cliente1", r1.getCell(1).getStringCellValue());
        assertTrue(r1.getCell(4).getStringCellValue().contains("2023"));
        Row r2 = s.getRow(2);
        assertEquals("cliente2", r2.getCell(1).getStringCellValue());
        // createdAt null should produce empty string
        assertEquals("", r2.getCell(4).getStringCellValue());
    }

            @Test
            void pagos_exception_wrappedAsBusinessException() {
                // cause NullPointerException during generation by supplying a payment with null user
                PaymentModel bad = PaymentModel.builder().id(99L).user(null).amount(10.0).method("X").build();
                List<PaymentModel> list = Collections.singletonList(bad);
                assertThrows(BusinessException.class, () -> ExcelReportGenerator.generarReportePagos(list));
            }

    // ---------------- clientes ----------------
    @Test
    void clientes_emptyList_repoReturnsEmpty_placeholders() throws Exception {
        IAppointmentRepository repo = Mockito.mock(IAppointmentRepository.class);
        when(repo.findByUserIdOrderByAppointmentStartDesc(anyLong())).thenReturn(Collections.emptyList());

        UserModel c = new UserModel(1L, "cli", "p","999999999","cli@test",true,"11111111",null,null,null,null,null,null,0);
        byte[] out = ExcelReportGenerator.generarReporteClientes(Collections.singletonList(c), repo);
        assertNotNull(out);
        assertTrue(out.length > 0);

        Sheet s = sheetFromBytes(out, "Clientes");
        Row r = s.getRow(1);
        assertEquals("-", r.getCell(4).getStringCellValue());
        assertEquals(0.0, r.getCell(5).getNumericCellValue());
        assertEquals("-", r.getCell(6).getStringCellValue());
    }

    @Test
    void clientes_withData_and_nulls_and_exceptionHandling() throws Exception {
        IAppointmentRepository repo = Mockito.mock(IAppointmentRepository.class);

        UserModel client = new UserModel(2L, "cli2", "p","999999990","c2@test",true,"22222222",null,null,null,null,null,null,1);
        ServiceModel svc = ServiceModel.builder().id(5L).name("Relaj").baseprice(50.0).durationMin(60).build();
        AppointmentModel ap = AppointmentModel.builder().id(7L).user(client).service(svc).appointmentStart(LocalDateTime.of(2024,5,1,9,0)).build();
        when(repo.findByUserIdOrderByAppointmentStartDesc(2L)).thenReturn(Collections.singletonList(ap));

        byte[] out = ExcelReportGenerator.generarReporteClientes(Collections.singletonList(client), repo);
        assertNotNull(out);
        assertTrue(out.length > 0);

        Sheet s = sheetFromBytes(out, "Clientes");
        Row r = s.getRow(1);
        assertTrue(r.getCell(4).getStringCellValue().contains("2024"));
        assertEquals(1.0, r.getCell(5).getNumericCellValue());
        assertEquals("Relaj", r.getCell(6).getStringCellValue());
    }

    @Test
    void clientes_exception_wrapped() {
        IAppointmentRepository repo = Mockito.mock(IAppointmentRepository.class);
        when(repo.findByUserIdOrderByAppointmentStartDesc(anyLong())).thenThrow(new RuntimeException("boom"));
        UserModel c = new UserModel(3L, "x","p","999","x@test",true,"33333333",null,null,null,null,null,null,0);
        assertThrows(BusinessException.class, () -> ExcelReportGenerator.generarReporteClientes(Collections.singletonList(c), repo));
    }

    // ---------------- trabajadores ----------------
    @Test
    void trabajadores_empty_and_nullExperiencia_and_exception() throws Exception {
        UserModel w1 = new UserModel(10L, "w1","p","999","w1@test",true,"44444444",null,null,null,null,null,null,null);
        byte[] out = ExcelReportGenerator.generarReporteTrabajadores(Collections.singletonList(w1));
        Sheet s = sheetFromBytes(out, "Trabajadores");
        Row r = s.getRow(1);
        assertEquals(0.0, r.getCell(7).getNumericCellValue());

        UserModel bad = new UserModel(11L, null,"p","999","b@test",true,"55555555",null,null,null,null,null,null,null);
        assertThrows(BusinessException.class, () -> ExcelReportGenerator.generarReporteTrabajadores(Collections.singletonList(bad)));
    }

    // ---------------- servicios ----------------
    @Test
    void servicios_empty_and_withData() throws Exception {
        byte[] empty = ExcelReportGenerator.generarReporteServicios(Collections.emptyList());
        assertNotNull(empty);

        ServiceModel s1 = ServiceModel.builder().id(1L).name("S1").baseprice(30.0).durationMin(30).build();
        byte[] out = ExcelReportGenerator.generarReporteServicios(Collections.singletonList(s1));
        Sheet sh = sheetFromBytes(out, "Servicios");
        Row r = sh.getRow(1);
        assertEquals("S1", r.getCell(1).getStringCellValue());
    }

    // ---------------- reservas ----------------
    @Test
    void reservas_empty_and_withData_and_alternation_and_exception() throws Exception {
        byte[] empty = ExcelReportGenerator.generarReporteReservas(Collections.emptyList());
        assertNotNull(empty);

        UserModel u = new UserModel(21L, "cli","p","999","u@test",true,"66666666",null,null,null,null,null,null,0);
        UserModel w = new UserModel(22L, "work","p","999","w@test",true,"77777777",null,null,null,null,null,null,0);
        ServiceModel svc = ServiceModel.builder().id(3L).name("Svc").baseprice(10.0).durationMin(20).build();
        AppointmentModel a1 = AppointmentModel.builder().id(100L).user(u).worker(w).service(svc).appointmentStart(LocalDateTime.of(2025,2,2,12,0)).status(AppointmentModel.Status.CONFIRMED).build();
        AppointmentModel a2 = AppointmentModel.builder().id(101L).user(u).worker(w).service(svc).appointmentStart(LocalDateTime.of(2025,2,3,12,0)).status(AppointmentModel.Status.COMPLETED).build();
        byte[] out = ExcelReportGenerator.generarReporteReservas(Arrays.asList(a1, a2));
        Sheet sh = sheetFromBytes(out, "Reservas");
        assertEquals(3, sh.getPhysicalNumberOfRows());
        assertEquals("cli", sh.getRow(1).getCell(1).getStringCellValue());
        assertEquals("cli", sh.getRow(2).getCell(1).getStringCellValue());

        AppointmentModel bad = AppointmentModel.builder().id(200L).user(null).worker(w).service(svc).appointmentStart(LocalDateTime.now()).build();
        assertThrows(BusinessException.class, () -> ExcelReportGenerator.generarReporteReservas(Collections.singletonList(bad)));
    }
}
