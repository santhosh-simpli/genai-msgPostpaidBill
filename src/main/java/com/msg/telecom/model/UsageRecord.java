package com.msg.telecom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a Usage Record in the telecom billing system.
 * <p>
 * Usage records track the consumption of services by customers,
 * such as minutes used, data consumed, or SMS sent.
 * These records are used to calculate billing amounts.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "usage_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class UsageRecord {

    /**
     * Unique identifier for the usage record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    /**
     * The service this usage is associated with.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    /**
     * Date when the usage occurred.
     */
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    /**
     * Amount of usage recorded (e.g., minutes, MB, count).
     */
    @Column(name = "usage_amount", nullable = false)
    private Double usageAmount;

    /**
     * Unit of measurement for the usage (e.g., MINUTES, MB, COUNT).
     */
    @Column(nullable = false)
    private String unit;
}
