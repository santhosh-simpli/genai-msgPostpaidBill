package com.msg.telecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for notification responses.
 * <p>
 * Contains the result of a notification send attempt,
 * including status for each channel and customer information.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    /** Whether the notification was sent successfully. */
    private boolean success;
    
    /** Human-readable message describing the result. */
    private String message;
    
    /** Status of email notification ("SENT", "FAILED", "SKIPPED"). */
    private String emailStatus;
    
    /** Status of WhatsApp notification ("SENT", "FAILED", "SKIPPED"). */
    private String whatsappStatus;
    
    /** WhatsApp Web link for manual message sending. */
    private String whatsappLink;
    
    /** ID of the invoice the notification was for. */
    private Long invoiceId;
    
    /** Name of the customer who was notified. */
    private String customerName;
    
    /** Email address the notification was sent to. */
    private String customerEmail;
    
    /** Phone number for WhatsApp notification. */
    private String customerPhone;
}
