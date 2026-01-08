package com.msg.telecom.controller;

import com.msg.telecom.dto.NotificationRequest;
import com.msg.telecom.dto.NotificationResponse;
import com.msg.telecom.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send invoice notification via Email and/or WhatsApp
     */
    @PostMapping("/invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<NotificationResponse> sendInvoiceNotification(@RequestBody NotificationRequest request) {
        log.info("Sending invoice notification for invoice #{}, type: {}",
                request.getInvoiceId(), request.getNotificationType());

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response); // Still return 200 with error details
        }
    }

    /**
     * Send invoice notification via Email only
     */
    @PostMapping("/invoice/{invoiceId}/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<NotificationResponse> sendInvoiceEmail(
            @PathVariable Long invoiceId,
            @RequestParam(required = false) String message) {

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(invoiceId);
        request.setNotificationType("EMAIL");
        request.setCustomMessage(message);

        return ResponseEntity.ok(notificationService.sendInvoiceNotification(request));
    }

    /**
     * Send invoice notification via WhatsApp only
     */
    @PostMapping("/invoice/{invoiceId}/whatsapp")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<NotificationResponse> sendInvoiceWhatsApp(
            @PathVariable Long invoiceId,
            @RequestParam(required = false) String message) {

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(invoiceId);
        request.setNotificationType("WHATSAPP");
        request.setCustomMessage(message);

        return ResponseEntity.ok(notificationService.sendInvoiceNotification(request));
    }

    /**
     * Send payment confirmation notification
     */
    @PostMapping("/payment-confirmation")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<NotificationResponse> sendPaymentConfirmation(
            @RequestParam Long invoiceId,
            @RequestParam Double amount) {

        return ResponseEntity.ok(notificationService.sendPaymentConfirmation(invoiceId, amount));
    }

    /**
     * Bulk send invoices to multiple customers
     */
    @PostMapping("/invoice/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendBulkInvoiceNotifications(
            @RequestBody java.util.List<NotificationRequest> requests) {

        int successCount = 0;
        int failCount = 0;

        for (NotificationRequest request : requests) {
            try {
                NotificationResponse response = notificationService.sendInvoiceNotification(request);
                if (response.isSuccess()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.error("Failed to send notification for invoice #{}: {}",
                        request.getInvoiceId(), e.getMessage());
            }
        }

        return ResponseEntity.ok(String.format(
                "Bulk notification completed: %d successful, %d failed", successCount, failCount));
    }
}
