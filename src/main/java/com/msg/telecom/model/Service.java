package com.msg.telecom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a Service subscription in the telecom billing system.
 * <p>
 * Services represent the telecom products subscribed to by customers,
 * such as voice plans, data plans, SMS packages, etc.
 * </p>
 * <p>
 * Service types: VOICE, DATA, SMS, BUNDLE
 * Status values: ACTIVE, SUSPENDED, CANCELLED
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class Service {

    /**
     * Unique identifier for the service.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    /**
     * The customer who has subscribed to this service.
     * Uses @JsonBackReference to prevent infinite recursion during serialization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    /**
     * Type of service (e.g., VOICE, DATA, SMS, BUNDLE).
     */
    @Column(name = "service_type", nullable = false)
    private String serviceType;

    /**
     * Date when the service subscription started.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Current status of the service (ACTIVE, SUSPENDED, CANCELLED).
     */
    @Column(nullable = false)
    private String status;
}
