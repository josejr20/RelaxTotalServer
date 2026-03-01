package com.andreutp.centromasajes.service;


import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.InvoiceModel;
import com.andreutp.centromasajes.dao.IInvoiceRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dto.PaymentRequest;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.UserModel;
import com.andreutp.centromasajes.utils.EmailService;
import com.andreutp.centromasajes.utils.PdfGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final IPaymentRepository paymentRepository;
    private final IUserRepository userRepository;
    private final IInvoiceRepository invoiceRepository;
    private final IAppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(PdfGenerator.class);


    public PaymentService(IPaymentRepository paymentRepository, IUserRepository userRepository,
                          IInvoiceRepository invoiceRepository, IAppointmentRepository appointmentRepository
            ,EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    public List<PaymentModel> getAllPayments() {
        return paymentRepository.findAll();
    }

        public PaymentModel createPayment(PaymentRequest request) {
        // 1. buscar user y appointment
        UserModel user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Usuario no encontrado"));

        AppointmentModel appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Cita no encontrada"));

        // 2. preparar payment
        LocalDateTime payDate = request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now();

        // avoid Lombok builder in case annotation processing fails
        PaymentModel payment = new PaymentModel();
        payment.setUser(user);
        payment.setAppointment(appointment);
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(payDate);
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentModel.Status.PAID);
        payment.setCoveredBySubscription(Boolean.TRUE.equals(request.getCoveredBySubscription()));

        PaymentModel savedPayment = paymentRepository.save(payment);

        // 3. determinar tipo de comprobante: BOLETA (default) o FACTURA
        InvoiceModel.Type tipoComprobante = InvoiceModel.Type.BOLETA; // por defecto

        if (request.getInvoiceType() != null) {
            String tipo = request.getInvoiceType().trim().toUpperCase();
            if ("FACTURA".equals(tipo)) {
                tipoComprobante = InvoiceModel.Type.FACTURA;
            } else if ("BOLETA".equals(tipo)) {
                tipoComprobante = InvoiceModel.Type.BOLETA;
            }
            // si viene cualquier otra cosa, se queda en BOLETA
        }

        // 4. crear invoice relacionado
        // manual creation to avoid reliance on Lombok builder
        InvoiceModel invoice = new InvoiceModel();
        invoice.setPayment(savedPayment);
        invoice.setUser(user);
        invoice.setAppointment(appointment);
        invoice.setType(tipoComprobante);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setCustomerName(user.getUsername());
        invoice.setCustomerDoc(user.getDni());
        invoice.setTotal(savedPayment.getAmount());
        invoice.setStatus(InvoiceModel.Status.PENDING);

        invoice = invoiceRepository.save(invoice);

        // 5. asociar invoice al payment
        savedPayment.setInvoice(invoice);
        paymentRepository.save(savedPayment);

        // 6. enviar correo con el PDF ya sea de la fac o coleta
        try {

            byte[] pdfBytes;

            // descripción común para ambos tipos
            String descripcion = buildDescriptionFromAppointment(appointment);  

            if (tipoComprobante == InvoiceModel.Type.FACTURA) {
                //  FACTURA: usar diseño A4
                pdfBytes = PdfGenerator.generateInvoiceA4Pdf(
                        user.getUsername(),
                        invoice.getInvoiceNumber(),
                        descripcion,
                        1,
                        invoice.getTotal(),
                        savedPayment.getMethod(),
                        String.valueOf(invoice.getInvoiceNumber())
                );
            } else {
                //  BOLETA: usar ticket estilizado
                pdfBytes = PdfGenerator.generateStyledInvoicePdf(
                        user.getUsername(),
                        invoice.getInvoiceNumber(),
                        descripcion,
                        1,
                        invoice.getTotal(),
                        savedPayment.getMethod(),
                        String.valueOf(invoice.getInvoiceNumber())
                );
            }

            String correoDestino =
                    (request.getCorreo() != null && !request.getCorreo().isBlank())
                            ? request.getCorreo()
                            : user.getEmail();

            // texto dinámico según tipo
            String tipoTexto = (tipoComprobante == InvoiceModel.Type.FACTURA) ? "factura" : "boleta";
            String tipoTextoCap = (tipoComprobante == InvoiceModel.Type.FACTURA) ? "Factura" : "Boleta";

            emailService.enviarCorreoConAdjunto(
                    correoDestino,
                    "Tu " + tipoTextoCap + " - " + invoice.getInvoiceNumber(),
                    "Adjuntamos su " + tipoTexto + " electrónica. ¡Gracias por su preferencia!",
                    pdfBytes,
                    tipoTextoCap + "_" + invoice.getInvoiceNumber() + ".pdf"
            );
        } catch (Exception e) {
            String tipo = (tipoComprobante == InvoiceModel.Type.FACTURA ? "factura" : "boleta");
            logger.error("Error enviando {} por correo", tipo, e);
        }

        return savedPayment;
    }

    // Metododin para generar la descripción de la cita
    private String buildDescriptionFromAppointment(AppointmentModel appointment) {
        String serv = appointment.getService() != null ? appointment.getService().getName() : "Servicio";
        String worker = appointment.getWorker() != null ? appointment.getWorker().getUsername() : "Trabajador";
        String start = appointment.getAppointmentStart() != null ? appointment.getAppointmentStart().toString() : "";
        return serv + " - " + worker + " - " + start;
    }




    // Generar factura para un pago existente
    public InvoiceModel createInvoiceForPayment(Long paymentId, String type, String customerName, String customerDoc) {
        PaymentModel payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Pago no encontrado"));

        InvoiceModel invoice = new InvoiceModel();
        invoice.setPayment(payment);
        invoice.setType(InvoiceModel.Type.valueOf(type.toUpperCase()));  // BOLETA o FACTURA
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setCustomerName(customerName);
        invoice.setCustomerDoc(customerDoc);
        invoice.setTotal(payment.getAmount());

        invoice = invoiceRepository.save(invoice);

        // Actualizar pago con la factura
        payment.setInvoice(invoice);
        paymentRepository.save(payment);

        return invoice;
    }

    private String generateInvoiceNumber() {
        // Genera un número único simple
        return "INV-" + System.currentTimeMillis();
    }

    public List<PaymentModel> getPaymentsByUser(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Usuario no encontrado"));
        return paymentRepository.findByUser(user);
    }

    public PaymentModel getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Pago no encontrado"));
    }

    public PaymentModel updatePayment(Long id, PaymentModel updatedPayment) {
        PaymentModel existing = getPaymentById(id);
        existing.setAmount(updatedPayment.getAmount());
        existing.setPaymentDate(updatedPayment.getPaymentDate());
        existing.setMethod(updatedPayment.getMethod());
        existing.setStatus(updatedPayment.getStatus());
        return paymentRepository.save(existing);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
