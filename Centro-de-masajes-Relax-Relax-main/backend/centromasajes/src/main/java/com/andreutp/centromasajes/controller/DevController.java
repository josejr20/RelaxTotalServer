package com.andreutp.centromasajes.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.UserModel;
import com.andreutp.centromasajes.utils.EmailService;
import com.andreutp.centromasajes.utils.ExcelReportGenerator;
import com.andreutp.centromasajes.utils.PdfGenerator;

/**
 * Controlador con operaciones de desarrollo utilizadas únicamente en el
 * perfil <code>dev</code>. Provee endpoints para enviar correos de prueba
 * con adjuntos (PDF y Excel) sin exponerlos en producción.
 */
@Profile("dev")
@RestController
@RequestMapping("/dev")
public class DevController {

    @Autowired
    private EmailService emailService;

    /**
     * Envío de boleta de prueba. Sólo disponible en perfil <code>dev</code>.
     * El destinatario se pasa como parámetro para evitar valores fijos.
     */
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam(required = false) String to) {
        if (to == null || to.isBlank()) {
            return ResponseEntity.badRequest().body("Parámetro 'to' requerido");
        }

        try {
            byte[] pdfBytes = PdfGenerator.generateStyledInvoicePdf(
                    "Cliente Prueba",
                    "B" + System.currentTimeMillis(),
                    "Descripción de prueba",
                    1,
                    100.0,
                    "Tarjeta",
                    "ORD" + System.currentTimeMillis()
            );
            emailService.enviarCorreoConAdjunto(
                    to,
                    "Boleta de pago - Relax Total",
                    "Adjuntamos su boleta electrónica con diseño. ¡Gracias por su preferencia!",
                    pdfBytes,
                    "BoletaRelaxTotal.pdf"
            );
            return ResponseEntity.ok("Correo enviado correctamente!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error enviando correo: " + e.getMessage());
        }
    }

    @GetMapping("/test-excel")
    public ResponseEntity<String> testExcel(@RequestParam(required = false) String to) {
        if (to == null || to.isBlank()) {
            return ResponseEntity.badRequest().body("Parámetro 'to' requerido");
        }

        try {
            PaymentModel pago1 = new PaymentModel();
            pago1.setId(1L);
            pago1.setAmount(100.0);
            pago1.setMethod("Tarjeta");
            pago1.setCreatedAt(LocalDateTime.now());
            UserModel user1 = new UserModel();
            user1.setUsername("André");
            pago1.setUser(user1);

            PaymentModel pago2 = new PaymentModel();
            pago2.setId(2L);
            pago2.setAmount(150.0);
            pago2.setMethod("Efectivo");
            pago2.setCreatedAt(LocalDateTime.now());
            UserModel user2 = new UserModel();
            user2.setUsername("Juan");
            pago2.setUser(user2);

            List<PaymentModel> pagos = List.of(pago1, pago2);
            byte[] excelBytes = ExcelReportGenerator.generarReportePagos(pagos);
            emailService.enviarCorreoConAdjunto(
                    to,
                    "Reporte de pagos",
                    "Adjunto encontrarás tu reporte de pagos.",
                    excelBytes,
                    "reporte_pagos.xlsx"
            );
            return ResponseEntity.ok("Correo con Excel enviado!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error enviando Excel: " + e.getMessage());
        }
    }
}
