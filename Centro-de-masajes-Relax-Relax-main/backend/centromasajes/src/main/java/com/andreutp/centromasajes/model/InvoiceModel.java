package com.andreutp.centromasajes.model;

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
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentModel payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private AppointmentModel appointment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;


    @Column(nullable = false)
    private String customerName;

    @Column(name = "customer_doc")
    private String customerDoc;

    @Column(nullable = false)
    private Double total;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        PAID,
        CANCELLED
    }

    public enum Type {
        BOLETA,
        FACTURA
    }
}
