package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dto.DashboardStatsDTO;
import com.andreutp.centromasajes.dto.MonthlyRevenueDTO;
import com.andreutp.centromasajes.dto.WeeklyReservationDTO;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.ServiceModel;
import com.andreutp.centromasajes.model.UserModel;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private IAppointmentRepository appointmentRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private AppointmentModel testAppointment;
    private UserModel testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        ServiceModel service = new ServiceModel();
        service.setBaseprice(100.0);

        testAppointment = new AppointmentModel();
        testAppointment.setId(1L);
        testAppointment.setStatus(AppointmentModel.Status.COMPLETED);
        testAppointment.setService(service);
    }

    @Test
    void testGetDashboardStats() {
        when(appointmentRepository.countByFecha(any())).thenReturn(5);
        when(appointmentRepository.countByFechaBetween(any(), any())).thenReturn(20);
        when(appointmentRepository.findByFechaBetween(any(), any()))
                .thenReturn(Arrays.asList(testAppointment));
        when(userRepository.countByFechaRegistroBetween(any(), any())).thenReturn(10);

        DashboardStatsDTO stats = dashboardService.getDashboardStats();

        assertNotNull(stats);
        verify(appointmentRepository, times(1)).countByFecha(any());
    }

    @Test
    void testGetMonthlyRevenue() {
        when(appointmentRepository.findByFechaBetween(any(), any()))
                .thenReturn(Arrays.asList(testAppointment));

        List<MonthlyRevenueDTO> revenue = dashboardService.getMonthlyRevenue(6);

        assertNotNull(revenue);
        assertEquals(6, revenue.size());
    }

    @Test
    void testGetWeeklyReservations() {
        when(appointmentRepository.countByFechaBetween(any(), any())).thenReturn(10);

        List<WeeklyReservationDTO> reservations = dashboardService.getWeeklyReservations(4);

        assertNotNull(reservations);
        assertEquals(4, reservations.size());
    }

    @Test
    void testGetRecentReservations() {
        List<AppointmentModel> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findTopByOrderByFechaCreacionDesc(10))
                .thenReturn(appointments);

        List<AppointmentModel> result = dashboardService.getRecentReservations(10);

        assertNotNull(result);
    }
}
