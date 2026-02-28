package com.andreutp.centromasajes.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Represents a service offered by the massage center.
 */
@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "El nombre del servicio es obligatorio")
  @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @NotBlank(message = "La descripción es obligatoria")
  @Column(columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "La duracion es obligatoria")
  @Min(value = 10, message = "La duración mínima es de 10 minutos")
  @Column(name = "duration_min", nullable = false)
  private Integer durationMin;

  @NotNull(message = "El precio es obligatorio")
  @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
  @Column(name = "base_price")
  private Double baseprice;

  @Builder.Default
  private Boolean active = true;

  @Builder.Default
  private LocalDateTime createAt = LocalDateTime.now();

}
