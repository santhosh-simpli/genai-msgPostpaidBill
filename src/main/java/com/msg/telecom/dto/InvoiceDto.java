package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for Invoice entity.
 * <p>
 * Used for transferring invoice data in API requests and responses.
 * Status is automatically updated to PAID when a payment is made.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class InvoiceDto {

    /** Unique identifier for the invoice. */
    private Long invoiceId;

    /** ID of the customer this invoice belongs to. */
    private Long customerId;

    /** Total amount due on this invoice. */
    private Double totalAmount;

    /** Current status (PENDING, PAID, OVERDUE, CANCELLED). */
    private String status;

    /** Due date for payment. */
    private String dueDate;

    /** Start date of the billing period. */
    private String billingPeriodStart;

    /** End date of the billing period. */
    private String billingPeriodEnd;

    /** Alternative name for billing period start (frontend compatibility). */
    private String billingStartDate;

    /** Alternative name for billing period end (frontend compatibility). */
    private String billingEndDate;
}
