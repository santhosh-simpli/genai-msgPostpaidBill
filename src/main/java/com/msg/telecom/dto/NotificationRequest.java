package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for notification requests.
 * <p>
 * Used to request sending notifications to customers about invoices.
 * Supports email, WhatsApp, or both notification channels.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class NotificationRequest {

    /** ID of the invoice to notify about. */
    private Long invoiceId;

    /** Type of notification: "EMAIL", "WHATSAPP", or "BOTH". */
    private String notificationType;

    /** Optional custom message to include in the notification. */
    private String customMessage;
}
