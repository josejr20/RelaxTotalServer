package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.dao.IInvoiceRepository;
import com.andreutp.centromasajes.dao.IPlanRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dao.IServiceRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dto.AppointmentRequest;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.ServiceModel;
import com.andreutp.centromasajes.model.UserModel;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private IAppointmentRepository appointmentRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IServiceRepository serviceRepository;

    @Mock
    private IPlanRepository planRepository;

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IInvoiceRepository invoiceRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private UserModel testUser;
    private UserModel testWorker;
    private ServiceModel testService;
    private AppointmentModel testAppointment;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testWorker = new UserModel();
        testWorker.setId(2L);
        testWorker.setUsername("testWorker");

        testService = new ServiceModel();
        testService.setId(1L);
        testService.setName("Masaje Relajante");
        testService.setDurationMin(60);

        testAppointment = new AppointmentModel();
        testAppointment.setId(1L);
        testAppointment.setUser(testUser);
        testAppointment.setWorker(testWorker);
        testAppointment.setService(testService);
        testAppointment.setStatus(AppointmentModel.Status.PENDING);
        testAppointment.setAppointmentStart(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testCreateAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(1L);
        request.setWorkerId(2L);
        request.setServiceId(1L);
        request.setAppointmentStart(LocalDateTime.now().plusDays(1));
        request.setNotes("Test notes");
        request.setStatus("PENDING");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testWorker));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(appointmentRepository.existsByWorkerAndAppointmentStart(testWorker, request.getAppointmentStart()))
                .thenReturn(false);
        when(appointmentRepository.save(any())).thenReturn(testAppointment);

        AppointmentModel result = appointmentService.createAppointment(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    void testCreateAppointment_UserNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(999L);
        request.setWorkerId(2L);
        request.setServiceId(1L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.createAppointment(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void testCreateAppointment_WorkerNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(1L);
        request.setWorkerId(999L);
        request.setServiceId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    void testCreateAppointment_ServiceNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(1L);
        request.setWorkerId(2L);
        request.setServiceId(999L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testWorker));
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    void testCreateAppointment_WorkerUnavailable() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(1L);
        request.setWorkerId(2L);
        request.setServiceId(1L);
        request.setAppointmentStart(LocalDateTime.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testWorker));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(appointmentRepository.existsByWorkerAndAppointmentStart(testWorker, request.getAppointmentStart()))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> appointmentService.createAppointment(request));
    }

    @Test
    void testGetAllAppointments() {
        List<AppointmentModel> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentModel> result = appointmentService.getAllAppointments();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAppointmentsByUser() {
        List<AppointmentModel> appointments = Arrays.asList(testAppointment);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(appointmentRepository.findByUser(testUser)).thenReturn(appointments);

        List<AppointmentModel> result = appointmentService.getAppointmentsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAppointmentsByUser_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentsByUser(999L));
    }

    @Test
    void testGetAppointmentsByWorker() {
        List<AppointmentModel> appointments = Arrays.asList(testAppointment);
        when(userRepository.findById(2L)).thenReturn(Optional.of(testWorker));
        when(appointmentRepository.findByWorker(testWorker)).thenReturn(appointments);

        List<AppointmentModel> result = appointmentService.getAppointmentsByWorker(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAppointmentById() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        AppointmentModel result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAppointmentById_NotFound() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentById(999L));
    }

    @Test
    void testUpdateAppointmentStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any())).thenReturn(testAppointment);

        AppointmentModel result = appointmentService.updateAppointmentStatus(1L, "CONFIRMED");

        assertNotNull(result);
        assertEquals(AppointmentModel.Status.CONFIRMED, result.getStatus());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    void testUpdateAppointmentStatus_InvalidStatus() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        assertThrows(IllegalArgumentException.class,
                () -> appointmentService.updateAppointmentStatus(1L, "INVALID_STATUS"));
    }

    @Test
    void testUpdateAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setUserId(1L);
        request.setWorkerId(2L);
        request.setServiceId(1L);
        request.setAppointmentStart(LocalDateTime.now().plusDays(2));
        request.setNotes("Updated notes");
        request.setStatus("CONFIRMED");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testWorker));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(appointmentRepository.save(any())).thenReturn(testAppointment);

        AppointmentModel result = appointmentService.updateAppointment(1L, request);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(any());
    }
}
