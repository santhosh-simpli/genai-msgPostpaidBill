package com.msg.telecom.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long invoiceId;
    private String notificationType; // "EMAIL", "WHATSAPP", "BOTH"
    private String customMessage; // Optional custom message to include
}
