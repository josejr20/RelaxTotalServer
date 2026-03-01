package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IInvoiceRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dto.InvoiceRequest;
import com.andreutp.centromasajes.model.InvoiceModel;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.UserModel;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private IInvoiceRepository invoiceRepository;

    @Mock
    private IPaymentRepository paymentRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private InvoiceModel testInvoice;
    private PaymentModel testPayment;

    @BeforeEach
    void setUp() {
        testPayment = new PaymentModel();
        testPayment.setId(1L);
        testPayment.setAmount(100.0);

        UserModel user = new UserModel();
        user.setId(1L);
        testPayment.setUser(user);

        testInvoice = new InvoiceModel();
        testInvoice.setId(1L);
        testInvoice.setPayment(testPayment);
        testInvoice.setInvoiceNumber("INV-001");
        testInvoice.setType(InvoiceModel.Type.BOLETA);
        testInvoice.setTotal(100.0);
        testInvoice.setStatus(InvoiceModel.Status.PENDING);
    }

    @Test
    void testCreateInvoice() {
        InvoiceRequest request = new InvoiceRequest();
        request.setPaymentId(1L);
        request.setType("BOLETA");
        request.setInvoiceNumber("INV-001");
        request.setCustomerName("Test Customer");
        request.setTotal(100.0);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(invoiceRepository.save(any())).thenReturn(testInvoice);
        when(paymentRepository.save(any())).thenReturn(testPayment);

        InvoiceModel result = invoiceService.createInvoice(request);

        assertNotNull(result);
        assertEquals("INV-001", result.getInvoiceNumber());
        verify(invoiceRepository, times(1)).save(any());
    }

    @Test
    void testCreateInvoice_PaymentNotFound() {
        InvoiceRequest request = new InvoiceRequest();
        request.setPaymentId(999L);

        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.createInvoice(request));
    }

    @Test
    void testGetAllInvoices() {
        List<InvoiceModel> invoices = Arrays.asList(testInvoice);
        when(invoiceRepository.findAll()).thenReturn(invoices);

        List<InvoiceModel> result = invoiceService.getAllInvoices();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetInvoiceById() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

        InvoiceModel result = invoiceService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetInvoiceById_NotFound() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(999L));
    }

    @Test
    void testUpdateInvoice() {
        InvoiceRequest request = new InvoiceRequest();
        request.setType("FACTURA");
        request.setInvoiceNumber("INV-002");
        request.setCustomerName("Updated Customer");
        request.setTotal(150.0);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any())).thenReturn(testInvoice);

        InvoiceModel result = invoiceService.updateInvoice(1L, request);

        assertNotNull(result);
        verify(invoiceRepository, times(1)).save(any());
    }

    @Test
    void testDeleteInvoice() {
        invoiceService.deleteInvoice(1L);

        verify(invoiceRepository, times(1)).deleteById(1L);
    }
}
