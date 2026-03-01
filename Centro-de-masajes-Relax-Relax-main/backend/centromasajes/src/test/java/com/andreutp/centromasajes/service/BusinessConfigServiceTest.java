package com.andreutp.centromasajes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreutp.centromasajes.dao.IBusinessConfigRepository;
import com.andreutp.centromasajes.model.BusinessConfigModel;

@ExtendWith(MockitoExtension.class)
class BusinessConfigServiceTest {

    @Mock
    private IBusinessConfigRepository repository;

    @InjectMocks
    private BusinessConfigService businessConfigService;

    private BusinessConfigModel testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new BusinessConfigModel();
        testConfig.setId(1L);
        testConfig.setName("Relax Total");
        testConfig.setAddress("Av. Principal 123");
        testConfig.setPhone("+1 555-1234");
        testConfig.setEmail("info@relaxtotal.com");
    }

    @Test
    void testGetConfig_ExistingConfig() {
        when(repository.findAll()).thenReturn(java.util.Arrays.asList(testConfig));

        BusinessConfigModel result = businessConfigService.getConfig();

        assertNotNull(result);
        assertEquals("Relax Total", result.getName());
    }

    @Test
    void testGetConfig_NoConfig_CreatesDefault() {
        when(repository.findAll()).thenReturn(java.util.List.of());
        when(repository.save(any())).thenReturn(testConfig);

        BusinessConfigModel result = businessConfigService.getConfig();

        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateConfig_ExistingConfig() {
        BusinessConfigModel updated = new BusinessConfigModel();
        updated.setName("Updated Name");
        updated.setAddress("New Address");
        updated.setPhone("+1 999-9999");
        updated.setEmail("newemail@test.com");

        when(repository.findAll()).thenReturn(java.util.Arrays.asList(testConfig));
        when(repository.save(any())).thenReturn(testConfig);

        BusinessConfigModel result = businessConfigService.updateConfig(updated);

        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateConfig_NoConfig_SavesNew() {
        BusinessConfigModel config = new BusinessConfigModel();
        config.setName("New Config");

        when(repository.findAll()).thenReturn(java.util.List.of());
        when(repository.save(any())).thenReturn(config);

        BusinessConfigModel result = businessConfigService.updateConfig(config);

        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }
}
