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

import com.andreutp.centromasajes.dao.IPlanRepository;
import com.andreutp.centromasajes.model.PlanModel;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private IPlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private PlanModel testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new PlanModel();
        testPlan.setId(1L);
        testPlan.setName("Premium Plan");
        testPlan.setDescription("Premium massage plan");
        testPlan.setPrice(99.99);
        testPlan.setDurationDays(30);
    }

    @Test
    void testGetAllPlans() {
        List<PlanModel> plans = Arrays.asList(testPlan);
        when(planRepository.findAll()).thenReturn(plans);

        List<PlanModel> result = planService.getAllPlans();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetPlanById() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        PlanModel result = planService.getPlanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPlanById_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> planService.getPlanById(999L));
    }

    @Test
    void testSavePlan_Success() {
        when(planRepository.findByName("Premium Plan")).thenReturn(Optional.empty());
        when(planRepository.save(any())).thenReturn(testPlan);

        PlanModel result = planService.savePlan(testPlan);

        assertNotNull(result);
        assertEquals("Premium Plan", result.getName());
        verify(planRepository, times(1)).save(any());
    }

    @Test
    void testSavePlan_DuplicateName() {
        when(planRepository.findByName("Premium Plan")).thenReturn(Optional.of(testPlan));

        assertThrows(RuntimeException.class, () -> planService.savePlan(testPlan));
        verify(planRepository, times(0)).save(any());
    }

    @Test
    void testUpdatePlan() {
        PlanModel updated = new PlanModel();
        updated.setName("Updated Plan");
        updated.setDescription("Updated description");
        updated.setPrice(149.99);
        updated.setDurationDays(60);

        when(planRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(planRepository.save(any())).thenReturn(testPlan);

        PlanModel result = planService.updatePlan(1L, updated);

        assertNotNull(result);
        verify(planRepository, times(1)).save(any());
    }

    @Test
    void testDeletePlan() {
        planService.deletePlan(1L);

        verify(planRepository, times(1)).deleteById(1L);
    }
}
