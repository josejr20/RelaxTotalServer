package com.andreutp.centromasajes.model;


import com.andreutp.centromasajes.utils.JsonListConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Double price;

  @Column(name = "duration_days")
  private Integer durationDays;

  private String tipo;
  private String icono;

  @Column(name = "servicios_incluidos", columnDefinition = "LONGTEXT")
  @JsonProperty("servicios_incluidos")
  @Convert(converter = JsonListConverter.class)
  private List<String> serviciosIncluidos;

  @Column(columnDefinition = "LONGTEXT")
  @Convert(converter = JsonListConverter.class)
  private List<String> beneficios;

  private Boolean destacado;
  private String estado;

  private Integer duracion;
  @Column(name = "duracion_unidad")
  @JsonProperty("duracion_unidad")
  private String duracionUnidad;

  @Column(name = "created_at", nullable = false)
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
