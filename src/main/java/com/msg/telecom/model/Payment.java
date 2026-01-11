package com.msg.telecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity class representing a Payment in the telecom billing system.
 * <p>
 * Payments are made against invoices to settle customer bills.
 * When a payment is created, the associated invoice's status is
 * automatically updated to PAID.
 * </p>
 * <p>
 * Payments are displayed with the most recent ones at the top
 * for better user experience.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class Payment {

    /**
     * Unique identifier for the payment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    /**
     * The invoice this payment is applied to.
     * Invoice status is updated to PAID when payment is created.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    /**
     * Date when the payment was made.
     */
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /**
     * Amount of the payment.
     */
    @Column(nullable = false)
    private Double amount;

    /**
     * Method used for payment (e.g., CASH, CARD, UPI, BANK_TRANSFER).
     */
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;
}
