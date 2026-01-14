package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for NotificationRequest DTO.
 * Tests getters, setters, equals, hashCode, and toString methods.
 */
@DisplayName("NotificationRequest DTO Tests")
class NotificationRequestTest {

    @Test
    @DisplayName("Should create NotificationRequest with default constructor")
    void testDefaultConstructor() {
        NotificationRequest request = new NotificationRequest();
        assertNotNull(request);
        assertNull(request.getInvoiceId());
        assertNull(request.getNotificationType());
        assertNull(request.getCustomMessage());
    }

    @Test
    @DisplayName("Should set and get invoiceId")
    void testInvoiceIdGetterSetter() {
        NotificationRequest request = new NotificationRequest();
        Long invoiceId = 123L;

        request.setInvoiceId(invoiceId);

        assertEquals(invoiceId, request.getInvoiceId());
    }

    @Test
    @DisplayName("Should set and get notificationType")
    void testNotificationTypeGetterSetter() {
        NotificationRequest request = new NotificationRequest();
        String type = "EMAIL";

        request.setNotificationType(type);

        assertEquals(type, request.getNotificationType());
    }

    @Test
    @DisplayName("Should set and get customMessage")
    void testCustomMessageGetterSetter() {
        NotificationRequest request = new NotificationRequest();
        String message = "Your invoice is ready";

        request.setCustomMessage(message);

        assertEquals(message, request.getCustomMessage());
    }

    @Test
    @DisplayName("Should handle all notification types")
    void testAllNotificationTypes() {
        NotificationRequest emailRequest = new NotificationRequest();
        emailRequest.setNotificationType("EMAIL");
        assertEquals("EMAIL", emailRequest.getNotificationType());

        NotificationRequest whatsappRequest = new NotificationRequest();
        whatsappRequest.setNotificationType("WHATSAPP");
        assertEquals("WHATSAPP", whatsappRequest.getNotificationType());

        NotificationRequest bothRequest = new NotificationRequest();
        bothRequest.setNotificationType("BOTH");
        assertEquals("BOTH", bothRequest.getNotificationType());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(null);
        request.setNotificationType(null);
        request.setCustomMessage(null);

        assertNull(request.getInvoiceId());
        assertNull(request.getNotificationType());
        assertNull(request.getCustomMessage());
    }

    @Test
    @DisplayName("Should test equals with same object")
    void testEqualsSameObject() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");
        request.setCustomMessage("Test");

        assertEquals(request, request);
    }

    @Test
    @DisplayName("Should test equals with equal objects")
    void testEqualsEqualObjects() {
        NotificationRequest request1 = new NotificationRequest();
        request1.setInvoiceId(1L);
        request1.setNotificationType("EMAIL");
        request1.setCustomMessage("Test");

        NotificationRequest request2 = new NotificationRequest();
        request2.setInvoiceId(1L);
        request2.setNotificationType("EMAIL");
        request2.setCustomMessage("Test");

        assertEquals(request1, request2);
    }

    @Test
    @DisplayName("Should test equals with different objects")
    void testEqualsDifferentObjects() {
        NotificationRequest request1 = new NotificationRequest();
        request1.setInvoiceId(1L);
        request1.setNotificationType("EMAIL");

        NotificationRequest request2 = new NotificationRequest();
        request2.setInvoiceId(2L);
        request2.setNotificationType("WHATSAPP");

        assertNotEquals(request1, request2);
    }

    @Test
    @DisplayName("Should test equals with null")
    void testEqualsWithNull() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);

        assertNotEquals(null, request);
    }

    @Test
    @DisplayName("Should test equals with different class")
    void testEqualsWithDifferentClass() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);

        assertNotEquals("string", request);
    }

    @Test
    @DisplayName("Should test hashCode consistency")
    void testHashCodeConsistency() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");
        request.setCustomMessage("Test");

        int hashCode1 = request.hashCode();
        int hashCode2 = request.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    @DisplayName("Should test hashCode with equal objects")
    void testHashCodeEqualObjects() {
        NotificationRequest request1 = new NotificationRequest();
        request1.setInvoiceId(1L);
        request1.setNotificationType("EMAIL");
        request1.setCustomMessage("Test");

        NotificationRequest request2 = new NotificationRequest();
        request2.setInvoiceId(1L);
        request2.setNotificationType("EMAIL");
        request2.setCustomMessage("Test");

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should test toString contains all fields")
    void testToString() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(123L);
        request.setNotificationType("EMAIL");
        request.setCustomMessage("Test message");

        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("EMAIL"));
        assertTrue(toString.contains("Test message"));
    }

    @Test
    @DisplayName("Should test toString with null values")
    void testToStringWithNullValues() {
        NotificationRequest request = new NotificationRequest();

        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("NotificationRequest"));
    }

    @Test
    @DisplayName("Should create complete NotificationRequest")
    void testCompleteNotificationRequest() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(456L);
        request.setNotificationType("BOTH");
        request.setCustomMessage("Important invoice notification");

        assertAll("Complete NotificationRequest",
                () -> assertEquals(456L, request.getInvoiceId()),
                () -> assertEquals("BOTH", request.getNotificationType()),
                () -> assertEquals("Important invoice notification", request.getCustomMessage()));
    }

    @Test
    @DisplayName("Should update existing NotificationRequest")
    void testUpdateNotificationRequest() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");
        request.setCustomMessage("Original message");

        // Update values
        request.setInvoiceId(2L);
        request.setNotificationType("WHATSAPP");
        request.setCustomMessage("Updated message");

        assertAll("Updated NotificationRequest",
                () -> assertEquals(2L, request.getInvoiceId()),
                () -> assertEquals("WHATSAPP", request.getNotificationType()),
                () -> assertEquals("Updated message", request.getCustomMessage()));
    }

    @Test
    @DisplayName("Should handle empty custom message")
    void testEmptyCustomMessage() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(1L);
        request.setNotificationType("EMAIL");
        request.setCustomMessage("");

        assertEquals("", request.getCustomMessage());
    }

    @Test
    @DisplayName("Should handle special characters in custom message")
    void testSpecialCharactersInCustomMessage() {
        NotificationRequest request = new NotificationRequest();
        String specialMessage = "Invoice #123 - Amount: $1,000.50 (Due: 2024-01-01)";
        request.setCustomMessage(specialMessage);

        assertEquals(specialMessage, request.getCustomMessage());
    }

    @Test
    @DisplayName("Should handle long custom message")
    void testLongCustomMessage() {
        NotificationRequest request = new NotificationRequest();
        String longMessage = "This is a very long custom message ".repeat(10);
        request.setCustomMessage(longMessage);

        assertEquals(longMessage, request.getCustomMessage());
    }

    @Test
    @DisplayName("Should handle large invoice ID")
    void testLargeInvoiceId() {
        NotificationRequest request = new NotificationRequest();
        Long largeId = Long.MAX_VALUE;
        request.setInvoiceId(largeId);

        assertEquals(largeId, request.getInvoiceId());
    }

    @Test
    @DisplayName("Should handle zero invoice ID")
    void testZeroInvoiceId() {
        NotificationRequest request = new NotificationRequest();
        request.setInvoiceId(0L);

        assertEquals(0L, request.getInvoiceId());
    }
}
