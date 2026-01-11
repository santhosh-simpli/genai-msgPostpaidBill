package com.msg.telecom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing an Invoice in the telecom billing system.
 * <p>
 * Invoices are generated for customers based on their usage during a billing
 * period. Each invoice has a status that tracks its payment state.
 * </p>
 * <p>
 * Status values: PENDING, PAID, OVERDUE, CANCELLED
 * When a payment is made against an invoice, the status is automatically
 * updated to PAID by the PaymentService.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class Invoice {

    /**
     * Unique identifier for the invoice.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;

    /**
     * The customer associated with this invoice.
     * Uses @JsonBackReference to prevent infinite recursion during serialization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    /**
     * Start date of the billing period covered by this invoice.
     */
    @Column(name = "billing_period_start", nullable = false)
    private LocalDate billingPeriodStart;

    /**
     * End date of the billing period covered by this invoice.
     */
    @Column(name = "billing_period_end", nullable = false)
    private LocalDate billingPeriodEnd;

    /**
     * Total amount due for this invoice.
     */
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    /**
     * Current status of the invoice.
     * Possible values: PENDING, PAID, OVERDUE, CANCELLED
     */
    @Column(nullable = false)
    private String status;
}
