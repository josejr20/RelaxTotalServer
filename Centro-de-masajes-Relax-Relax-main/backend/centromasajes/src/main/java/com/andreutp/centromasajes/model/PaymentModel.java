package com.andreutp.centromasajes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private AppointmentModel appointment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = true) //true para que primero se pague y luego cree factura , aunque prodria ser al revez tambien
    private InvoiceModel invoice;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_date", nullable = false)
    @Builder.Default
    private LocalDateTime paymentDate = LocalDateTime.now();;

    @Column(length = 50)
    private String method;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "covered_by_subscription", nullable = false)
    @Builder.Default
    private Boolean coveredBySubscription = false;

    public enum Status {
        PENDING,
        PAID,
        REFUNDED,
        FAILED
    }


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;
}
