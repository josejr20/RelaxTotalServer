package com.andreutp.centromasajes.utils;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.ServiceModel;
import com.andreutp.centromasajes.model.UserModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for generating Excel reports using Apache POI.  All methods are
 * static; the class cannot be instantiated.  The previous implementation
 * contained a great deal of duplicated logic in each report method (workbook
 * creation, header row construction, auto‑sizing columns, exception handling).
 * That code has been centralised into helpers below to satisfy Sonar
 * duplication rules and make maintenance easier.  Methods also include
 * javadoc for improved documentation and coverage.
 */
public final class ExcelReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReportGenerator.class);

    // utility class, non-instantiable
    private ExcelReportGenerator() { }

    /* helper factories -------------------------------------------------- */

    /**
     * Create a new workbook + default styles (header/normal/alternate).
     */
    private static ExcelStyles initWorkbook(Workbook workbook) {
        return createStyles(workbook);
    }

    /**
     * Build header row with provided column labels.
     */
    private static void createHeaderRow(Sheet sheet, String[] headers, ExcelStyles styles) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.headerStyle);
        }
    }

    /**
     * Apply a style to every cell in a row.
     */
    private static void applyRowStyle(Row row, CellStyle style, int cols) {
        for (int i = 0; i < cols; i++) {
            row.getCell(i).setCellStyle(style);
        }
    }

    /**
     * Auto-size all columns in the sheet based on header length.
     */
    private static void autoSizeColumns(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Write workbook contents to byte array, handling IOExceptions.
     */
    private static byte[] toByteArray(Workbook workbook) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("Error writing workbook to bytes", e);
            throw new RuntimeException("Error generando Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a spreadsheet containing payment information.  Header and row
     * styling is applied uniformly; workbook writing and exception handling are
     * delegated to helpers to avoid repetition.
     */
    public static byte[] generarReportePagos(List<PaymentModel> pagos) {
        logger.info("Generando Excel para {} pagos", pagos.size());
        String[] columnas = {"ID", "Cliente", "Monto", "Método", "Fecha"};

        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = initWorkbook(workbook);
            Sheet sheet = workbook.createSheet("Pagos");
            createHeaderRow(sheet, columnas, styles);

            int rowNum = 1;
            for (PaymentModel p : pagos) {
                Row row = sheet.createRow(rowNum);
                CellStyle rowStyle = (rowNum % 2 == 0) ? styles.normalStyle : styles.alternateStyle;

                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getUser().getUsername());
                row.createCell(2).setCellValue(p.getAmount().doubleValue());
                row.createCell(3).setCellValue(p.getMethod().toString());
                row.createCell(4).setCellValue(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");

                applyRowStyle(row, rowStyle, columnas.length);
                rowNum++;
            }

            autoSizeColumns(sheet, columnas.length);
            return toByteArray(workbook);
        } catch (Exception e) {
            logger.error("Error generando reporte de pagos", e);
            throw new RuntimeException("Error generando Excel de pagos", e);
        }
    }


    /**
     * Generate a sheet listing client details; uses the appointment repository
     * to calculate the last visit, number of services and type of last service.
     * The repository is provided as a parameter so the method remains static
     * and testable.
     */
    public static byte[] generarReporteClientes(List<UserModel> clientes, IAppointmentRepository appointmentRepository) {
        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "Última Visita", "Servicios", "Tipo Masaje"};

        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = initWorkbook(workbook);
            Sheet sheet = workbook.createSheet("Clientes");
            createHeaderRow(sheet, columnas, styles);

            int rowNum = 1;
            for (UserModel cliente : clientes) {
                Row row = sheet.createRow(rowNum);
                CellStyle rowStyle = (rowNum % 2 == 0) ? styles.normalStyle : styles.alternateStyle;

                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getUsername());
                row.createCell(2).setCellValue(cliente.getEmail());
                row.createCell(3).setCellValue(cliente.getPhone());

                List<AppointmentModel> citas = appointmentRepository.findByUserIdOrderByAppointmentStartDesc(cliente.getId());
                if (!citas.isEmpty()) {
                    AppointmentModel last = citas.get(0);
                    row.createCell(4).setCellValue(last.getAppointmentStart().toString());
                    row.createCell(5).setCellValue(citas.size());
                    row.createCell(6).setCellValue(last.getService().getName());
                } else {
                    row.createCell(4).setCellValue("-");
                    row.createCell(5).setCellValue(0);
                    row.createCell(6).setCellValue("-");
                }

                applyRowStyle(row, rowStyle, columnas.length);
                rowNum++;
            }

            autoSizeColumns(sheet, columnas.length);
            return toByteArray(workbook);
        } catch (Exception e) {
            logger.error("Error generando reporte de clientes", e);
            throw new RuntimeException("Error generando Excel de clientes", e);
        }
    }



    // Trabajadores
    public static byte[] generarReporteTrabajadores(List<UserModel> trabajadores) {
        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "DNI", "Especialidad", "Estado", "Experiencia"};
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = initWorkbook(workbook);
            Sheet sheet = workbook.createSheet("Trabajadores");
            createHeaderRow(sheet, columnas, styles);

            int rowNum = 1;
            for (UserModel w : trabajadores) {
                Row row = sheet.createRow(rowNum);
                CellStyle rowStyle = (rowNum % 2 == 0) ? styles.normalStyle : styles.alternateStyle;

                row.createCell(0).setCellValue(w.getId());
                row.createCell(1).setCellValue(w.getUsername());
                row.createCell(2).setCellValue(w.getEmail());
                row.createCell(3).setCellValue(w.getPhone());
                row.createCell(4).setCellValue(w.getDni());
                row.createCell(5).setCellValue(w.getEspecialidad());
                row.createCell(6).setCellValue(w.getEstado());
                row.createCell(7).setCellValue(w.getExperiencia() != null ? w.getExperiencia() : 0);

                applyRowStyle(row, rowStyle, columnas.length);
                rowNum++;
            }

            autoSizeColumns(sheet, columnas.length);
            return toByteArray(workbook);
        } catch (Exception e) {
            logger.error("Error generando reporte de trabajadores", e);
            throw new RuntimeException("Error generando Excel de trabajadores", e);
        }
    }

    // Servicios
    public static byte[] generarReporteServicios(List<ServiceModel> servicios) {
        String[] columnas = {"ID", "Nombre", "Precio", "Duración"};
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = initWorkbook(workbook);
            Sheet sheet = workbook.createSheet("Servicios");
            createHeaderRow(sheet, columnas, styles);

            int rowNum = 1;
            for (ServiceModel s : servicios) {
                Row row = sheet.createRow(rowNum);
                CellStyle rowStyle = (rowNum % 2 == 0) ? styles.normalStyle : styles.alternateStyle;

                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getName());
                row.createCell(2).setCellValue(s.getBaseprice());
                row.createCell(3).setCellValue(s.getDurationMin());

                applyRowStyle(row, rowStyle, columnas.length);
                rowNum++;
            }

            autoSizeColumns(sheet, columnas.length);
            return toByteArray(workbook);
        } catch (Exception e) {
            logger.error("Error generando reporte de servicios", e);
            throw new RuntimeException("Error generando Excel de servicios", e);
        }
    }

    // Reservas
    public static byte[] generarReporteReservas(List<AppointmentModel> reservas) {
        String[] columnas = {"ID", "Cliente", "Trabajador", "Servicio", "Fecha y Hora", "Estado"};
        try (Workbook workbook = new XSSFWorkbook()) {
            ExcelStyles styles = initWorkbook(workbook);
            Sheet sheet = workbook.createSheet("Reservas");
            createHeaderRow(sheet, columnas, styles);

            int rowNum = 1;
            for (AppointmentModel a : reservas) {
                Row row = sheet.createRow(rowNum);
                CellStyle rowStyle = (rowNum % 2 == 0) ? styles.normalStyle : styles.alternateStyle;

                row.createCell(0).setCellValue(a.getId());
                row.createCell(1).setCellValue(a.getUser().getUsername());
                row.createCell(2).setCellValue(a.getWorker().getUsername());
                row.createCell(3).setCellValue(a.getService().getName());
                row.createCell(4).setCellValue(a.getAppointmentStart().toString());
                row.createCell(5).setCellValue(a.getStatus().toString());

                applyRowStyle(row, rowStyle, columnas.length);
                rowNum++;
            }

            autoSizeColumns(sheet, columnas.length);
            return toByteArray(workbook);
        } catch (Exception e) {
            logger.error("Error generando reporte de reservas", e);
            throw new RuntimeException("Error generando Excel de reservas", e);
        }
    }

    private static class ExcelStyles {
        CellStyle headerStyle;
        CellStyle normalStyle;
        CellStyle alternateStyle;
    }

    private static ExcelStyles createStyles(Workbook workbook) {
        ExcelStyles styles = new ExcelStyles();

        // Fuente para encabezado
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // Estilo encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Estilo normal
        CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);

        // Estilo alterno (para filas pares)
        CellStyle alternateStyle = workbook.createCellStyle();
        alternateStyle.cloneStyleFrom(normalStyle);
        alternateStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styles.headerStyle = headerStyle;
        styles.normalStyle = normalStyle;
        styles.alternateStyle = alternateStyle;
        return styles;
    }



}
