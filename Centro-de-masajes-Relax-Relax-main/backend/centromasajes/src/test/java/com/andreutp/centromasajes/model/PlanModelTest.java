package com.andreutp.centromasajes.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class PlanModelTest {

    @Test
    void gettersSettersAndBuilder() {
        PlanModel plan = new PlanModel();
        plan.setId(1L);
        plan.setName("Basic");
        plan.setPrice(50.0);
        plan.setDurationDays(30);
        plan.setTipo("Mensual");
        plan.setServiciosIncluidos(Arrays.asList("A","B"));
        plan.setDuracionUnidad("dias");

        assertEquals(1L, plan.getId());
        assertEquals("Basic", plan.getName());
        assertEquals(50.0, plan.getPrice());
        assertEquals("Mensual", plan.getTipo());
        assertEquals(Arrays.asList("A","B"), plan.getServiciosIncluidos());
        assertEquals("dias", plan.getDuracionUnidad());

        PlanModel built = PlanModel.builder()
                .id(2L)
                .name("Pro")
                .price(100.0)
                .build();
        assertEquals(2L, built.getId());
        assertEquals("Pro", built.getName());
    }
}
