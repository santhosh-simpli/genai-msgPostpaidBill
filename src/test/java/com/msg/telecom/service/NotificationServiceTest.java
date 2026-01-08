package com.msg.telecom.service;

import com.msg.telecom.dto.NotificationRequest;
import com.msg.telecom.dto.NotificationResponse;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private NotificationService notificationService;

    private Invoice testInvoice;
    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set field values using reflection
        ReflectionTestUtils.setField(notificationService, "fromEmail", "test@msgtelecom.com");
        ReflectionTestUtils.setField(notificationService, "companyName", "MSG Telecom");
        ReflectionTestUtils.setField(notificationService, "companyPhone", "1-800-MSG-TELCO");

        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("customer@test.com");

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("John Doe");
        testCustomer.setPhoneNumber("+1-555-123-4567");
        testCustomer.setUser(testUser);

        // Create test invoice
        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setCustomer(testCustomer);
        testInvoice.setTotalAmount(150.00);
        testInvoice.setStatus("PENDING");
        testInvoice.setBillingPeriodStart(LocalDate.of(2024, 1, 1));
        testInvoice.setBillingPeriodEnd(LocalDate.of(2024, 1, 31));
    }

    @Test
    void sendInvoiceNotification_Success_BothChannels() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("BOTH");
        request.setCustomMessage("Please pay your bill.");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getInvoiceId());
        assertEquals("John Doe", response.getCustomerName());
        assertEquals("customer@test.com", response.getCustomerEmail());
        assertNotNull(response.getWhatsappLink());
        assertTrue(response.getWhatsappLink().contains("wa.me"));
    }

    @Test
    void sendInvoiceNotification_EmailOnly() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getInvoiceId());
        assertEquals("customer@test.com", response.getCustomerEmail());
    }

    @Test
    void sendInvoiceNotification_WhatsAppOnly() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("WHATSAPP");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getInvoiceId());
        assertNotNull(response.getWhatsappLink());
        assertTrue(response.getWhatsappLink().contains("wa.me"));
        assertEquals("LINK_GENERATED", response.getWhatsappStatus());
    }

    @Test
    void sendInvoiceNotification_NoCustomer() {
        testInvoice.setCustomer(null);

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("BOTH");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Customer not found"));
    }

    @Test
    void sendInvoiceNotification_NoEmail() throws Exception {
        testUser.setEmail(null);

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getEmailStatus().contains("No email address"));
    }

    @Test
    void sendInvoiceNotification_NoPhone() {
        testCustomer.setPhoneNumber(null);

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("WHATSAPP");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getWhatsappStatus().contains("No phone number"));
    }

    @Test
    void sendInvoiceNotification_NullNotificationType() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType(null); // Should default to BOTH

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getInvoiceId());
    }

    @Test
    void generateWhatsAppLink_ValidPhone() {
        String link = notificationService.generateWhatsAppLink(testInvoice, testCustomer, "Test message");

        assertNotNull(link);
        assertTrue(link.startsWith("https://wa.me/"));
        assertTrue(link.contains("text="));
    }

    @Test
    void generateWhatsAppLink_PhoneWithCountryCode() {
        testCustomer.setPhoneNumber("+15551234567");

        String link = notificationService.generateWhatsAppLink(testInvoice, testCustomer, null);

        assertNotNull(link);
        assertTrue(link.contains("15551234567"));
    }

    @Test
    void generateWhatsAppLink_PhoneWithoutCountryCode() {
        testCustomer.setPhoneNumber("5551234567");

        String link = notificationService.generateWhatsAppLink(testInvoice, testCustomer, null);

        assertNotNull(link);
        assertTrue(link.contains("wa.me/"));
    }

    @Test
    void generateWhatsAppLink_CustomMessage() {
        String customMessage = "Special discount available!";

        String link = notificationService.generateWhatsAppLink(testInvoice, testCustomer, customMessage);

        assertNotNull(link);
        assertTrue(link.contains("Special"));
    }

    @Test
    void sendPaymentConfirmation_Success() throws Exception {
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationResponse response = notificationService.sendPaymentConfirmation(1L, 150.00);

        assertNotNull(response);
        assertEquals(1L, response.getInvoiceId());
    }

    @Test
    void sendPaymentConfirmation_NoCustomer() {
        testInvoice.setCustomer(null);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendPaymentConfirmation(1L, 150.00);

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Customer not found"));
    }

    @Test
    void sendInvoiceNotification_PaidStatus() throws Exception {
        testInvoice.setStatus("PAID");

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("WHATSAPP");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertTrue(response.getWhatsappLink().contains("Thank+you"));
    }

    @Test
    void sendInvoiceNotification_OverdueStatus() throws Exception {
        testInvoice.setStatus("OVERDUE");

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("WHATSAPP");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertNotNull(response);
        assertTrue(response.getWhatsappLink().contains("wa.me"));
    }

    @Test
    void sendInvoiceNotification_EmptyEmail() throws Exception {
        testUser.setEmail("");

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertFalse(response.isSuccess());
    }

    @Test
    void sendInvoiceNotification_EmptyPhone() {
        testCustomer.setPhoneNumber("");

        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("WHATSAPP");

        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);

        NotificationResponse response = notificationService.sendInvoiceNotification(request);

        assertFalse(response.isSuccess());
    }
}
