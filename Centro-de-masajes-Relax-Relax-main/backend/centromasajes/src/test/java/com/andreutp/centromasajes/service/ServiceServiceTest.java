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

import com.andreutp.centromasajes.dao.IServiceRepository;
import com.andreutp.centromasajes.model.ServiceModel;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private IServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceModel testService;

    @BeforeEach
    void setUp() {
        testService = new ServiceModel();
        testService.setId(1L);
        testService.setName("Masaje Relajante");
        testService.setDescription("Masaje relajante de 60 minutos");
        testService.setDurationMin(60);
        testService.setBaseprice(50.0);
        testService.setActive(true);
    }

    @Test
    void testSaveModelService() {
        when(serviceRepository.save(any())).thenReturn(testService);

        ServiceModel result = serviceService.saveModelService(testService);

        assertNotNull(result);
        assertEquals("Masaje Relajante", result.getName());
        verify(serviceRepository, times(1)).save(any());
    }

    @Test
    void testGetAllService() {
        List<ServiceModel> services = Arrays.asList(testService);
        when(serviceRepository.findAll()).thenReturn(services);

        List<ServiceModel> result = serviceService.getAllService();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetServiceById() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));

        ServiceModel result = serviceService.getServiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetServiceById_NotFound() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> serviceService.getServiceById(999L));
    }

    @Test
    void testUpdateService() {
        ServiceModel updated = new ServiceModel();
        updated.setName("Updated Service");
        updated.setDescription("Updated description");
        updated.setDurationMin(90);
        updated.setBaseprice(75.0);
        updated.setActive(true);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(serviceRepository.save(any())).thenReturn(testService);

        ServiceModel result = serviceService.updateService(1L, updated);

        assertNotNull(result);
        verify(serviceRepository, times(1)).save(any());
    }

    @Test
    void testDeleteService() {
        serviceService.deleteService(1L);

        verify(serviceRepository, times(1)).deleteById(1L);
    }
}
