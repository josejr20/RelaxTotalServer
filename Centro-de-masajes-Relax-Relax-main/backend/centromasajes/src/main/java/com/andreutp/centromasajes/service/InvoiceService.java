package com.andreutp.centromasajes.service;


import com.andreutp.centromasajes.model.InvoiceModel;
import com.andreutp.centromasajes.dao.IInvoiceRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dto.InvoiceRequest;
import com.andreutp.centromasajes.model.PaymentModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final IInvoiceRepository invoiceRepository;
    private final IPaymentRepository paymentRepository;

    public InvoiceService(IInvoiceRepository invoiceRepository, IPaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }
/*
    // Crear factura o boleta antes de pagar
    public InvoiceModel createInvoice(InvoiceRequest request) {
        PaymentModel payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        InvoiceModel invoice = InvoiceModel.builder()
                .payment(payment)
                .type(InvoiceModel.Type.valueOf(request.getType().toUpperCase()))
                .invoiceNumber(request.getInvoiceNumber())
                .customerName(request.getCustomerName())
                .customerDoc(request.getCustomerDoc())
                .total(request.getTotal())
                .notes(request.getNotes())
                .build();

        return invoiceRepository.save(invoice);
    }*/

    // Crear factura despues del pago
    public InvoiceModel createInvoice(InvoiceRequest request) {
        PaymentModel payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        // build without Lombok in case annotation processing isn't available
        InvoiceModel invoice = new InvoiceModel();
        invoice.setPayment(payment);
        invoice.setAppointment(payment.getAppointment());
        invoice.setUser(payment.getUser());
        invoice.setType(InvoiceModel.Type.valueOf(request.getType().toUpperCase()));
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerDoc(request.getCustomerDoc());
        invoice.setTotal(request.getTotal());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(InvoiceModel.Status.PENDING);

        invoice = invoiceRepository.save(invoice);

        // Opcional: actualizar el pago con la factura
        payment.setInvoice(invoice);
        paymentRepository.save(payment);

        return invoice;
    }


    // Listar todas las facturas
    public List<InvoiceModel> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Obtener factura por ID
    public InvoiceModel getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    // Actualizar factura (solo campos modificables)
    public InvoiceModel updateInvoice(Long id, InvoiceRequest request) {
        InvoiceModel existing = getInvoiceById(id);

        existing.setTotal(request.getTotal());
        existing.setNotes(request.getNotes());
        existing.setType(InvoiceModel.Type.valueOf(request.getType().toUpperCase()));
        existing.setInvoiceNumber(request.getInvoiceNumber());
        existing.setCustomerName(request.getCustomerName());
        existing.setCustomerDoc(request.getCustomerDoc());

        return invoiceRepository.save(existing);
    }

    // Eliminar factura
    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}
