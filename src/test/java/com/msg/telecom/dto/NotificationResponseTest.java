package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for NotificationResponse DTO.
 * Tests builder pattern, getters, setters, equals, hashCode, and toString methods.
 */
@DisplayName("NotificationResponse DTO Tests")
class NotificationResponseTest {

    @Test
    @DisplayName("Should create NotificationResponse with default constructor")
    void testDefaultConstructor() {
        NotificationResponse response = new NotificationResponse();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should create NotificationResponse with all-args constructor")
    void testAllArgsConstructor() {
        NotificationResponse response = new NotificationResponse(
            true, 
            "Notification sent successfully",
            "SENT",
            "SENT",
            "https://wa.me/1234567890",
            123L,
            "John Doe",
            "john@example.com",
            "1234567890"
        );
        
        assertAll("All args constructor",
            () -> assertTrue(response.isSuccess()),
            () -> assertEquals("Notification sent successfully", response.getMessage()),
            () -> assertEquals("SENT", response.getEmailStatus()),
            () -> assertEquals("SENT", response.getWhatsappStatus()),
            () -> assertEquals("https://wa.me/1234567890", response.getWhatsappLink()),
            () -> assertEquals(123L, response.getInvoiceId()),
            () -> assertEquals("John Doe", response.getCustomerName()),
            () -> assertEquals("john@example.com", response.getCustomerEmail()),
            () -> assertEquals("1234567890", response.getCustomerPhone())
        );
    }

    @Test
    @DisplayName("Should create NotificationResponse using builder")
    void testBuilder() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Notification sent")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .whatsappLink("https://wa.me/1234567890")
            .invoiceId(456L)
            .customerName("Jane Smith")
            .customerEmail("jane@example.com")
            .customerPhone("9876543210")
            .build();
        
        assertAll("Builder pattern",
            () -> assertTrue(response.isSuccess()),
            () -> assertEquals("Notification sent", response.getMessage()),
            () -> assertEquals("SENT", response.getEmailStatus()),
            () -> assertEquals("SENT", response.getWhatsappStatus()),
            () -> assertEquals("https://wa.me/1234567890", response.getWhatsappLink()),
            () -> assertEquals(456L, response.getInvoiceId()),
            () -> assertEquals("Jane Smith", response.getCustomerName()),
            () -> assertEquals("jane@example.com", response.getCustomerEmail()),
            () -> assertEquals("9876543210", response.getCustomerPhone())
        );
    }

    @Test
    @DisplayName("Should create partial NotificationResponse using builder")
    void testPartialBuilder() {
        NotificationResponse response = NotificationResponse.builder()
            .success(false)
            .message("Email failed")
            .emailStatus("FAILED")
            .whatsappStatus("SKIPPED")
            .build();
        
        assertAll("Partial builder",
            () -> assertFalse(response.isSuccess()),
            () -> assertEquals("Email failed", response.getMessage()),
            () -> assertEquals("FAILED", response.getEmailStatus()),
            () -> assertEquals("SKIPPED", response.getWhatsappStatus()),
            () -> assertNull(response.getWhatsappLink()),
            () -> assertNull(response.getInvoiceId()),
            () -> assertNull(response.getCustomerName()),
            () -> assertNull(response.getCustomerEmail()),
            () -> assertNull(response.getCustomerPhone())
        );
    }

    @Test
    @DisplayName("Should set and get success")
    void testSuccessGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        response.setSuccess(true);
        assertTrue(response.isSuccess());
        
        response.setSuccess(false);
        assertFalse(response.isSuccess());
    }

    @Test
    @DisplayName("Should set and get message")
    void testMessageGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        String message = "Notification processed";
        response.setMessage(message);
        assertEquals(message, response.getMessage());
    }

    @Test
    @DisplayName("Should set and get emailStatus")
    void testEmailStatusGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        
        response.setEmailStatus("SENT");
        assertEquals("SENT", response.getEmailStatus());
        
        response.setEmailStatus("FAILED");
        assertEquals("FAILED", response.getEmailStatus());
        
        response.setEmailStatus("SKIPPED");
        assertEquals("SKIPPED", response.getEmailStatus());
    }

    @Test
    @DisplayName("Should set and get whatsappStatus")
    void testWhatsappStatusGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        
        response.setWhatsappStatus("SENT");
        assertEquals("SENT", response.getWhatsappStatus());
        
        response.setWhatsappStatus("FAILED");
        assertEquals("FAILED", response.getWhatsappStatus());
        
        response.setWhatsappStatus("SKIPPED");
        assertEquals("SKIPPED", response.getWhatsappStatus());
    }

    @Test
    @DisplayName("Should set and get whatsappLink")
    void testWhatsappLinkGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        String link = "https://wa.me/1234567890?text=Invoice%20ready";
        response.setWhatsappLink(link);
        assertEquals(link, response.getWhatsappLink());
    }

    @Test
    @DisplayName("Should set and get invoiceId")
    void testInvoiceIdGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        Long invoiceId = 789L;
        response.setInvoiceId(invoiceId);
        assertEquals(invoiceId, response.getInvoiceId());
    }

    @Test
    @DisplayName("Should set and get customerName")
    void testCustomerNameGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        String name = "Alice Johnson";
        response.setCustomerName(name);
        assertEquals(name, response.getCustomerName());
    }

    @Test
    @DisplayName("Should set and get customerEmail")
    void testCustomerEmailGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        String email = "alice@example.com";
        response.setCustomerEmail(email);
        assertEquals(email, response.getCustomerEmail());
    }

    @Test
    @DisplayName("Should set and get customerPhone")
    void testCustomerPhoneGetterSetter() {
        NotificationResponse response = new NotificationResponse();
        String phone = "5551234567";
        response.setCustomerPhone(phone);
        assertEquals(phone, response.getCustomerPhone());
    }

    @Test
    @DisplayName("Should test equals with same object")
    void testEqualsSameObject() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .build();
        
        assertEquals(response, response);
    }

    @Test
    @DisplayName("Should test equals with equal objects")
    void testEqualsEqualObjects() {
        NotificationResponse response1 = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .invoiceId(1L)
            .customerName("Test User")
            .customerEmail("test@example.com")
            .customerPhone("1234567890")
            .whatsappLink("https://wa.me/1234567890")
            .build();
        
        NotificationResponse response2 = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .invoiceId(1L)
            .customerName("Test User")
            .customerEmail("test@example.com")
            .customerPhone("1234567890")
            .whatsappLink("https://wa.me/1234567890")
            .build();
        
        assertEquals(response1, response2);
    }

    @Test
    @DisplayName("Should test equals with different objects")
    void testEqualsDifferentObjects() {
        NotificationResponse response1 = NotificationResponse.builder()
            .success(true)
            .message("Test 1")
            .build();
        
        NotificationResponse response2 = NotificationResponse.builder()
            .success(false)
            .message("Test 2")
            .build();
        
        assertNotEquals(response1, response2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        NotificationResponse response = new NotificationResponse();
        assertNotEquals(null, response);
    }

    @Test
    @DisplayName("Should test equals with different class")
    void testEqualsWithDifferentClass() {
        NotificationResponse response = new NotificationResponse();
        assertNotEquals("string", response);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .build();
        
        int hashCode1 = response.hashCode();
        int hashCode2 = response.hashCode();
        
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test hashCode with equal objects")
    void testHashCodeEqualObjects() {
        NotificationResponse response1 = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .build();
        
        NotificationResponse response2 = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .build();
        
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Should test toString contains all fields")
    void testToString() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Notification sent successfully")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .whatsappLink("https://wa.me/1234567890")
            .invoiceId(123L)
            .customerName("John Doe")
            .customerEmail("john@example.com")
            .customerPhone("1234567890")
            .build();
        
        String toString = response.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("true"));
        assertTrue(toString.contains("Notification sent successfully"));
        assertTrue(toString.contains("SENT"));
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("1234567890"));
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNullValues() {
        NotificationResponse response = new NotificationResponse();
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("NotificationResponse"));
    }

    @Test
    @DisplayName("Should handle email-only notification")
    void testEmailOnlyNotification() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Email sent successfully")
            .emailStatus("SENT")
            .whatsappStatus("SKIPPED")
            .customerEmail("customer@example.com")
            .build();
        
        assertAll("Email only",
            () -> assertTrue(response.isSuccess()),
            () -> assertEquals("SENT", response.getEmailStatus()),
            () -> assertEquals("SKIPPED", response.getWhatsappStatus()),
            () -> assertNotNull(response.getCustomerEmail()),
            () -> assertNull(response.getCustomerPhone())
        );
    }

    @Test
    @DisplayName("Should handle WhatsApp-only notification")
    void testWhatsAppOnlyNotification() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("WhatsApp link generated")
            .emailStatus("SKIPPED")
            .whatsappStatus("SENT")
            .whatsappLink("https://wa.me/1234567890")
            .customerPhone("1234567890")
            .build();
        
        assertAll("WhatsApp only",
            () -> assertTrue(response.isSuccess()),
            () -> assertEquals("SKIPPED", response.getEmailStatus()),
            () -> assertEquals("SENT", response.getWhatsappStatus()),
            () -> assertNotNull(response.getWhatsappLink()),
            () -> assertNotNull(response.getCustomerPhone())
        );
    }

    @Test
    @DisplayName("Should handle failed notification")
    void testFailedNotification() {
        NotificationResponse response = NotificationResponse.builder()
            .success(false)
            .message("Failed to send notifications")
            .emailStatus("FAILED")
            .whatsappStatus("FAILED")
            .build();
        
        assertAll("Failed notification",
            () -> assertFalse(response.isSuccess()),
            () -> assertEquals("Failed to send notifications", response.getMessage()),
            () -> assertEquals("FAILED", response.getEmailStatus()),
            () -> assertEquals("FAILED", response.getWhatsappStatus())
        );
    }

    @Test
    @DisplayName("Should handle builder with chain methods")
    void testBuilderChaining() {
        NotificationResponse response = NotificationResponse.builder()
            .success(true)
            .message("Test")
            .emailStatus("SENT")
            .whatsappStatus("SENT")
            .whatsappLink("https://wa.me/1234567890")
            .invoiceId(1L)
            .customerName("Test")
            .customerEmail("test@example.com")
            .customerPhone("1234567890")
            .build();
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("Should handle null message")
    void testNullMessage() {
        NotificationResponse response = new NotificationResponse();
        response.setMessage(null);
        assertNull(response.getMessage());
    }

    @Test
    @DisplayName("Should handle empty message")
    void testEmptyMessage() {
        NotificationResponse response = new NotificationResponse();
        response.setMessage("");
        assertEquals("", response.getMessage());
    }

    @Test
    @DisplayName("Should handle special characters in WhatsApp link")
    void testSpecialCharactersInWhatsAppLink() {
        NotificationResponse response = new NotificationResponse();
        String link = "https://wa.me/1234567890?text=Invoice%20%23123%20-%20Amount%3A%20%241000";
        response.setWhatsappLink(link);
        assertEquals(link, response.getWhatsappLink());
    }

    @Test
    @DisplayName("Should handle large invoice ID")
    void testLargeInvoiceId() {
        NotificationResponse response = new NotificationResponse();
        Long largeId = Long.MAX_VALUE;
        response.setInvoiceId(largeId);
        assertEquals(largeId, response.getInvoiceId());
    }

    @Test
    @DisplayName("Should handle international phone numbers")
    void testInternationalPhoneNumbers() {
        NotificationResponse response = new NotificationResponse();
        String internationalPhone = "+91-9876543210";
        response.setCustomerPhone(internationalPhone);
        assertEquals(internationalPhone, response.getCustomerPhone());
    }

    @Test
    @DisplayName("Should handle update operations")
    void testUpdateOperations() {
        NotificationResponse response = new NotificationResponse();
        response.setSuccess(false);
        response.setMessage("Initial message");
        
        // Update
        response.setSuccess(true);
        response.setMessage("Updated message");
        
        assertTrue(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
    }

    @Test
    @DisplayName("Should create builder instance")
    void testBuilderInstance() {
        NotificationResponse.NotificationResponseBuilder builder = NotificationResponse.builder();
        assertNotNull(builder);
        
        NotificationResponse response = builder
            .success(true)
            .message("Test")
            .build();
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("Should handle complex customer names")
    void testComplexCustomerNames() {
        NotificationResponse response = new NotificationResponse();
        String complexName = "Dr. María José García-Fernández III";
        response.setCustomerName(complexName);
        assertEquals(complexName, response.getCustomerName());
    }

    @Test
    @DisplayName("Should handle multiple domain email addresses")
    void testMultipleDomainEmails() {
        NotificationResponse response = new NotificationResponse();
        String email = "user@subdomain.example.co.uk";
        response.setCustomerEmail(email);
        assertEquals(email, response.getCustomerEmail());
    }

    @Test
    @DisplayName("Should test no-args constructor initializes to defaults")
    void testNoArgsConstructorDefaults() {
        NotificationResponse response = new NotificationResponse();
        
        assertAll("Default values",
            () -> assertFalse(response.isSuccess()),
            () -> assertNull(response.getMessage()),
            () -> assertNull(response.getEmailStatus()),
            () -> assertNull(response.getWhatsappStatus()),
            () -> assertNull(response.getWhatsappLink()),
            () -> assertNull(response.getInvoiceId()),
            () -> assertNull(response.getCustomerName()),
            () -> assertNull(response.getCustomerEmail()),
            () -> assertNull(response.getCustomerPhone())
        );
    }
}
