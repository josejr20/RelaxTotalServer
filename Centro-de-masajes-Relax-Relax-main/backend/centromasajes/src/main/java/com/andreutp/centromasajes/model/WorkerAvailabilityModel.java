package com.andreutp.centromasajes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;

/**
 * Represents a worker's availability for a given weekday and time range.
 *
 * Fields are intentionally indented with two spaces to comply with
 * Google Checkstyle rules used by Sonar analysis.
 */
@Entity
@Table(name = "worker_availability")
@Data
public class WorkerAvailabilityModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "weekday")
  private String day; // lunes, martes, etc.

  @Column(name = "active")
  private Boolean activo; // trabaja ese día o no

  @Column(name = "start_time")
  private String inicio; // hora de inicio HH:mm

  @Column(name = "end_time")
  private String fin; // hora de fin HH:mm

  @ManyToOne
  @JoinColumn(name = "worker_id")
  @JsonIgnoreProperties({"availability"})
  private UserModel worker;

}
