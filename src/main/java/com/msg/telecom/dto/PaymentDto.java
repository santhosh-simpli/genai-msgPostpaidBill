package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for Payment entity.
 * <p>
 * Used for creating and displaying payment records.
 * When a payment is created, the associated invoice is automatically marked as
 * PAID.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class PaymentDto {

    /** Unique identifier for the payment. */
    private Long paymentId;

    /** ID of the invoice this payment is for. */
    private Long invoiceId;

    /** Payment amount. */
    private Double amount;

    /** Date when the payment was made. */
    private String paymentDate;

    /** Payment status. */
    private String status;
}
