package com.msg.telecom.controller;

import com.msg.telecom.dto.NotificationRequest;
import com.msg.telecom.dto.NotificationResponse;
import com.msg.telecom.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

        @Mock
        private NotificationService notificationService;

        @InjectMocks
        private NotificationController notificationController;

        private NotificationRequest testRequest;
        private NotificationResponse successResponse;
        private NotificationResponse failureResponse;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);

                testRequest = new NotificationRequest();
                testRequest.setInvoiceId(1L);
                testRequest.setNotificationType("EMAIL");
                testRequest.setCustomMessage("Test message");

                successResponse = NotificationResponse.builder()
                                .success(true)
                                .message("Notification sent successfully")
                                .emailStatus("SENT")
                                .invoiceId(1L)
                                .customerName("John Doe")
                                .customerEmail("john@msgtel.com")
                                .build();

                failureResponse = NotificationResponse.builder()
                                .success(false)
                                .message("Failed to send notification")
                                .emailStatus("FAILED")
                                .invoiceId(1L)
                                .build();
        }

        @Test
        void sendInvoiceNotification_Success() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse);

                ResponseEntity<NotificationResponse> response = notificationController
                                .sendInvoiceNotification(testRequest);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                assertEquals("Notification sent successfully", response.getBody().getMessage());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceNotification_Failure() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(failureResponse);

                ResponseEntity<NotificationResponse> response = notificationController
                                .sendInvoiceNotification(testRequest);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertFalse(response.getBody().isSuccess());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceEmail_Success() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendInvoiceEmail(1L,
                                "Custom email message");

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceEmail_WithoutMessage() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendInvoiceEmail(1L, null);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceWhatsApp_Success() {
                NotificationResponse whatsappResponse = NotificationResponse.builder()
                                .success(true)
                                .message("WhatsApp notification prepared")
                                .whatsappStatus("READY")
                                .whatsappLink("https://wa.me/1234567890?text=...")
                                .invoiceId(1L)
                                .customerName("John Doe")
                                .customerPhone("+1234567890")
                                .build();

                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(whatsappResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendInvoiceWhatsApp(1L,
                                "WhatsApp message");

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertNotNull(response.getBody().getWhatsappLink());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceWhatsApp_WithoutMessage() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendInvoiceWhatsApp(1L, null);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(notificationService, times(1)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendPaymentConfirmation_Success() {
                NotificationResponse paymentResponse = NotificationResponse.builder()
                                .success(true)
                                .message("Payment confirmation sent")
                                .invoiceId(1L)
                                .build();

                when(notificationService.sendPaymentConfirmation(anyLong(), anyDouble()))
                                .thenReturn(paymentResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendPaymentConfirmation(1L,
                                100.50);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().isSuccess());
                verify(notificationService, times(1)).sendPaymentConfirmation(1L, 100.50);
        }

        @Test
        void sendPaymentConfirmation_WithZeroAmount() {
                NotificationResponse paymentResponse = NotificationResponse.builder()
                                .success(true)
                                .message("Payment confirmation sent")
                                .invoiceId(1L)
                                .build();

                when(notificationService.sendPaymentConfirmation(anyLong(), anyDouble()))
                                .thenReturn(paymentResponse);

                ResponseEntity<NotificationResponse> response = notificationController.sendPaymentConfirmation(1L, 0.0);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(notificationService, times(1)).sendPaymentConfirmation(1L, 0.0);
        }

        @Test
        void sendBulkInvoiceNotifications_AllSuccess() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse);

                NotificationRequest request1 = new NotificationRequest();
                request1.setInvoiceId(1L);
                request1.setNotificationType("EMAIL");

                NotificationRequest request2 = new NotificationRequest();
                request2.setInvoiceId(2L);
                request2.setNotificationType("EMAIL");

                List<NotificationRequest> requests = Arrays.asList(request1, request2);

                ResponseEntity<String> response = notificationController.sendBulkInvoiceNotifications(requests);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().contains("2 successful"));
                assertTrue(response.getBody().contains("0 failed"));
                verify(notificationService, times(2)).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendBulkInvoiceNotifications_PartialSuccess() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(successResponse)
                                .thenReturn(failureResponse);

                NotificationRequest request1 = new NotificationRequest();
                request1.setInvoiceId(1L);
                request1.setNotificationType("EMAIL");

                NotificationRequest request2 = new NotificationRequest();
                request2.setInvoiceId(2L);
                request2.setNotificationType("EMAIL");

                List<NotificationRequest> requests = Arrays.asList(request1, request2);

                ResponseEntity<String> response = notificationController.sendBulkInvoiceNotifications(requests);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().contains("1 successful"));
                assertTrue(response.getBody().contains("1 failed"));
        }

        @Test
        void sendBulkInvoiceNotifications_AllFailed() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(failureResponse);

                NotificationRequest request1 = new NotificationRequest();
                request1.setInvoiceId(1L);
                request1.setNotificationType("EMAIL");

                List<NotificationRequest> requests = Arrays.asList(request1);

                ResponseEntity<String> response = notificationController.sendBulkInvoiceNotifications(requests);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().contains("0 successful"));
                assertTrue(response.getBody().contains("1 failed"));
        }

        @Test
        void sendBulkInvoiceNotifications_WithException() {
                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenThrow(new RuntimeException("Service error"));

                NotificationRequest request1 = new NotificationRequest();
                request1.setInvoiceId(1L);
                request1.setNotificationType("EMAIL");

                List<NotificationRequest> requests = Arrays.asList(request1);

                ResponseEntity<String> response = notificationController.sendBulkInvoiceNotifications(requests);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().contains("0 successful"));
                assertTrue(response.getBody().contains("1 failed"));
        }

        @Test
        void sendBulkInvoiceNotifications_EmptyList() {
                List<NotificationRequest> requests = Arrays.asList();

                ResponseEntity<String> response = notificationController.sendBulkInvoiceNotifications(requests);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertTrue(response.getBody().contains("0 successful"));
                assertTrue(response.getBody().contains("0 failed"));
                verify(notificationService, never()).sendInvoiceNotification(any(NotificationRequest.class));
        }

        @Test
        void sendInvoiceNotification_WithBothType() {
                testRequest.setNotificationType("BOTH");

                NotificationResponse bothResponse = NotificationResponse.builder()
                                .success(true)
                                .message("Notification sent via email and WhatsApp")
                                .emailStatus("SENT")
                                .whatsappStatus("READY")
                                .invoiceId(1L)
                                .build();

                when(notificationService.sendInvoiceNotification(any(NotificationRequest.class)))
                                .thenReturn(bothResponse);

                ResponseEntity<NotificationResponse> response = notificationController
                                .sendInvoiceNotification(testRequest);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("SENT", response.getBody().getEmailStatus());
                assertEquals("READY", response.getBody().getWhatsappStatus());
        }
}
