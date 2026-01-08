package com.msg.telecom.service;

import com.msg.telecom.dto.NotificationRequest;
import com.msg.telecom.dto.NotificationResponse;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final InvoiceService invoiceService;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@msgtelecom.com}")
    private String fromEmail;

    @Value("${app.company.name:MSG Telecom}")
    private String companyName;

    @Value("${app.company.phone:1-800-MSG-TELCO}")
    private String companyPhone;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Send invoice notification via Email and/or WhatsApp
     */
    public NotificationResponse sendInvoiceNotification(NotificationRequest request) {
        Invoice invoice = invoiceService.getInvoiceById(request.getInvoiceId());
        Customer customer = invoice.getCustomer();

        if (customer == null) {
            return NotificationResponse.builder()
                    .success(false)
                    .message("Customer not found for this invoice")
                    .invoiceId(request.getInvoiceId())
                    .build();
        }

        String customerName = customer.getFullName();
        String customerEmail = customer.getUser() != null ? customer.getUser().getEmail() : null;
        String customerPhone = customer.getPhoneNumber();

        NotificationResponse.NotificationResponseBuilder responseBuilder = NotificationResponse.builder()
                .invoiceId(invoice.getInvoiceId())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone);

        String emailStatus = "NOT_SENT";
        String whatsappStatus = "NOT_SENT";
        String whatsappLink = null;
        boolean overallSuccess = true;

        String notificationType = request.getNotificationType() != null ? request.getNotificationType().toUpperCase()
                : "BOTH";

        // Send Email notification
        if ("EMAIL".equals(notificationType) || "BOTH".equals(notificationType)) {
            try {
                if (customerEmail != null && !customerEmail.isEmpty()) {
                    sendInvoiceEmail(invoice, customer, request.getCustomMessage());
                    emailStatus = "SENT";
                    log.info("Email sent successfully to {} for invoice #{}", customerEmail, invoice.getInvoiceId());
                } else {
                    emailStatus = "FAILED - No email address";
                    overallSuccess = false;
                }
            } catch (Exception e) {
                emailStatus = "FAILED - " + e.getMessage();
                overallSuccess = false;
                log.error("Failed to send email for invoice #{}: {}", invoice.getInvoiceId(), e.getMessage());
            }
        }

        // Generate WhatsApp link
        if ("WHATSAPP".equals(notificationType) || "BOTH".equals(notificationType)) {
            if (customerPhone != null && !customerPhone.isEmpty()) {
                whatsappLink = generateWhatsAppLink(invoice, customer, request.getCustomMessage());
                whatsappStatus = "LINK_GENERATED";
                log.info("WhatsApp link generated for customer {} for invoice #{}", customerPhone,
                        invoice.getInvoiceId());
            } else {
                whatsappStatus = "FAILED - No phone number";
                overallSuccess = false;
            }
        }

        String message;
        if (overallSuccess) {
            message = "Invoice notification sent successfully!";
        } else {
            message = "Some notifications may have failed. Please check individual status.";
        }

        return responseBuilder
                .success(overallSuccess)
                .message(message)
                .emailStatus(emailStatus)
                .whatsappStatus(whatsappStatus)
                .whatsappLink(whatsappLink)
                .build();
    }

    /**
     * Send invoice email to customer
     */
    private void sendInvoiceEmail(Invoice invoice, Customer customer, String customMessage) throws MessagingException {
        String customerEmail = customer.getUser().getEmail();
        String customerName = customer.getFullName();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(customerEmail);
        helper.setSubject("Invoice #" + invoice.getInvoiceId() + " from " + companyName);
        helper.setText(buildEmailBody(invoice, customer, customMessage), true);

        mailSender.send(mimeMessage);
    }

    /**
     * Build HTML email body for invoice
     */
    private String buildEmailBody(Invoice invoice, Customer customer, String customMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><style>");
        sb.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        sb.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        sb.append(
                ".header { background: linear-gradient(90deg, #1A5F7A, #2E86AB); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }");
        sb.append(".content { background: #f9f9f9; padding: 20px; border: 1px solid #ddd; }");
        sb.append(".invoice-details { background: white; padding: 15px; border-radius: 8px; margin: 15px 0; }");
        sb.append(".amount { font-size: 24px; color: #2E86AB; font-weight: bold; }");
        sb.append(
                ".status { display: inline-block; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; }");
        sb.append(".status-pending { background: #FEF3C7; color: #92400E; }");
        sb.append(".status-paid { background: #D1FAE5; color: #065F46; }");
        sb.append(".status-overdue { background: #FEE2E2; color: #991B1B; }");
        sb.append(
                ".footer { background: #333; color: white; padding: 15px; text-align: center; border-radius: 0 0 8px 8px; font-size: 12px; }");
        sb.append(
                ".btn { display: inline-block; background: #2E86AB; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin-top: 15px; }");
        sb.append("</style></head><body>");
        sb.append("<div class='container'>");

        // Header
        sb.append("<div class='header'>");
        sb.append("<h1>").append(companyName).append("</h1>");
        sb.append("<p>Invoice Notification</p>");
        sb.append("</div>");

        // Content
        sb.append("<div class='content'>");
        sb.append("<p>Dear <strong>").append(customer.getFullName()).append("</strong>,</p>");

        if (customMessage != null && !customMessage.isEmpty()) {
            sb.append("<p>").append(customMessage).append("</p>");
        } else {
            sb.append("<p>Please find below the details of your invoice:</p>");
        }

        // Invoice Details Box
        sb.append("<div class='invoice-details'>");
        sb.append("<table style='width: 100%; border-collapse: collapse;'>");
        sb.append("<tr><td><strong>Invoice Number:</strong></td><td>#").append(invoice.getInvoiceId())
                .append("</td></tr>");
        sb.append("<tr><td><strong>Billing Period:</strong></td><td>")
                .append(invoice.getBillingPeriodStart().format(DATE_FORMATTER))
                .append(" - ")
                .append(invoice.getBillingPeriodEnd().format(DATE_FORMATTER))
                .append("</td></tr>");
        sb.append("<tr><td><strong>Due Date:</strong></td><td>")
                .append(invoice.getBillingPeriodEnd().format(DATE_FORMATTER)).append("</td></tr>");
        sb.append("<tr><td><strong>Status:</strong></td><td><span class='status status-")
                .append(invoice.getStatus().toLowerCase())
                .append("'>").append(invoice.getStatus()).append("</span></td></tr>");
        sb.append("<tr><td colspan='2' style='padding-top: 15px;'><strong>Total Amount:</strong></td></tr>");
        sb.append("<tr><td colspan='2' class='amount'>").append(CURRENCY_FORMAT.format(invoice.getTotalAmount()))
                .append("</td></tr>");
        sb.append("</table>");
        sb.append("</div>");

        if ("PENDING".equalsIgnoreCase(invoice.getStatus()) || "OVERDUE".equalsIgnoreCase(invoice.getStatus())) {
            sb.append("<p style='text-align: center;'><a href='http://localhost:8090' class='btn'>Pay Now</a></p>");
        }

        sb.append("<p>If you have any questions about this invoice, please contact us at ").append(companyPhone)
                .append(".</p>");
        sb.append("<p>Thank you for choosing ").append(companyName).append("!</p>");
        sb.append("</div>");

        // Footer
        sb.append("<div class='footer'>");
        sb.append("<p>").append(companyName).append(" | Innovation Through Communication</p>");
        sb.append("<p>This is an automated message. Please do not reply directly to this email.</p>");
        sb.append("</div>");

        sb.append("</div></body></html>");
        return sb.toString();
    }

    /**
     * Generate WhatsApp Web link for sending invoice message
     */
    public String generateWhatsAppLink(Invoice invoice, Customer customer, String customMessage) {
        String phoneNumber = normalizePhoneNumber(customer.getPhoneNumber());
        String message = buildWhatsAppMessage(invoice, customer, customMessage);
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return "https://wa.me/" + phoneNumber + "?text=" + encodedMessage;
    }

    /**
     * Build WhatsApp message text
     */
    private String buildWhatsAppMessage(Invoice invoice, Customer customer, String customMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 *").append(companyName).append(" - Invoice Notification*\n\n");
        sb.append("Dear *").append(customer.getFullName()).append("*,\n\n");

        if (customMessage != null && !customMessage.isEmpty()) {
            sb.append(customMessage).append("\n\n");
        }

        sb.append("📋 *Invoice Details:*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📌 Invoice #: *").append(invoice.getInvoiceId()).append("*\n");
        sb.append("📅 Billing Period: ").append(invoice.getBillingPeriodStart().format(DATE_FORMATTER));
        sb.append(" - ").append(invoice.getBillingPeriodEnd().format(DATE_FORMATTER)).append("\n");
        sb.append("📆 Due Date: ").append(invoice.getBillingPeriodEnd().format(DATE_FORMATTER)).append("\n");
        sb.append("📊 Status: *").append(invoice.getStatus()).append("*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━\n");
        sb.append("💰 *Total Amount: ").append(CURRENCY_FORMAT.format(invoice.getTotalAmount())).append("*\n\n");

        if ("PENDING".equalsIgnoreCase(invoice.getStatus()) || "OVERDUE".equalsIgnoreCase(invoice.getStatus())) {
            sb.append("⚠️ Please make the payment at your earliest convenience.\n\n");
        } else {
            sb.append("✅ Thank you for your payment!\n\n");
        }

        sb.append("For any queries, please contact us at ").append(companyPhone).append("\n\n");
        sb.append("Thank you for being a valued customer! 🙏\n");
        sb.append("_").append(companyName).append(" - Innovation Through Communication_");

        return sb.toString();
    }

    /**
     * Normalize phone number to international format (remove spaces, dashes, and
     * ensure country code)
     */
    private String normalizePhoneNumber(String phone) {
        if (phone == null)
            return "";
        // Remove all non-numeric characters except +
        String cleaned = phone.replaceAll("[^0-9+]", "");
        // If doesn't start with +, assume it needs country code (default to +1 for US)
        if (!cleaned.startsWith("+") && !cleaned.startsWith("1")) {
            cleaned = "1" + cleaned;
        } else if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }
        return cleaned;
    }

    /**
     * Send payment confirmation notification
     */
    public NotificationResponse sendPaymentConfirmation(Long invoiceId, Double paymentAmount) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        Customer customer = invoice.getCustomer();

        if (customer == null) {
            return NotificationResponse.builder()
                    .success(false)
                    .message("Customer not found")
                    .build();
        }

        String customMessage = String.format(
                "We have received your payment of %s for Invoice #%d. Thank you for your prompt payment!",
                CURRENCY_FORMAT.format(paymentAmount),
                invoiceId);

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(invoiceId);
        request.setNotificationType("BOTH");
        request.setCustomMessage(customMessage);

        return sendInvoiceNotification(request);
    }
}
