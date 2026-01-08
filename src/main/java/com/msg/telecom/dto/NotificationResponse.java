package com.msg.telecom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private boolean success;
    private String message;
    private String emailStatus;
    private String whatsappStatus;
    private String whatsappLink; // WhatsApp Web link for manual sending
    private Long invoiceId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
