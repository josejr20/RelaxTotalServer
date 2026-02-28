package com.andreutp.centromasajes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"availability", "hibernateLazyInitializer"})
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    @JsonIgnoreProperties({"availability", "hibernateLazyInitializer"})
    private UserModel worker;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnoreProperties({"availability", "hibernateLazyInitializer"})
    private ServiceModel service;

    @Column(name = "appointment_start", nullable = false)
    private LocalDateTime appointmentStart;

    @Column(name = "appointment_end", nullable = false)
    private LocalDateTime appointmentEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_plan_id")
    private PlanModel userPlan;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
}
