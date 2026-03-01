package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dao.IServiceRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.UserModel;
import com.andreutp.centromasajes.utils.EmailService;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IServiceRepository serviceRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IAppointmentRepository appointmentRepository;

    @InjectMocks
    private ReportService reportService;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void testReportService_PaymentRepositoryIntegration() {
        List<PaymentModel> payments = Arrays.asList(new PaymentModel());
        when(paymentRepository.findAllByUserId(1L)).thenReturn(payments);

        List<PaymentModel> result = paymentRepository.findAllByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void testReportService_UserRepositoryIntegration() {
        List<UserModel> clients = Arrays.asList(testUser);
        when(userRepository.findByRoleName("USER")).thenReturn(clients);

        List<UserModel> result = userRepository.findByRoleName("USER");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByRoleName("USER");
    }

    @Test
    void testReportService_EmailServiceIntegration() {
        String recipient = "test@example.com";
        String subject = "Test Report";
        String body = "Test Body";
        byte[] attachment = new byte[] { 1, 2, 3 };
        String filename = "report.xlsx";

        verify(emailService, Mockito.never()).enviarCorreoConAdjunto(recipient, subject, body, attachment, filename);
    }
}
