package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceExtendedTest2 {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Invoice testInvoice;
    private Payment testPayment1;
    private Payment testPayment2;

    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setTotalAmount(200.0);

        testPayment1 = new Payment();
        testPayment1.setPaymentId(1L);
        testPayment1.setInvoice(testInvoice);
        testPayment1.setAmount(100.0);
        testPayment1.setPaymentDate(LocalDate.now().minusDays(1));
        testPayment1.setPaymentMethod("CREDIT_CARD");

        testPayment2 = new Payment();
        testPayment2.setPaymentId(2L);
        testPayment2.setInvoice(testInvoice);
        testPayment2.setAmount(100.0);
        testPayment2.setPaymentDate(LocalDate.now());
        testPayment2.setPaymentMethod("DEBIT_CARD");
    }

    @Test
    void getAllPayments_ReturnsOrderedByIdDesc() {
        when(paymentRepository.findAllByOrderByPaymentIdDesc())
                .thenReturn(Arrays.asList(testPayment2, testPayment1));

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getPaymentId());
        verify(paymentRepository).findAllByOrderByPaymentIdDesc();
    }

    @Test
    void getAllPayments_EmptyList() {
        when(paymentRepository.findAllByOrderByPaymentIdDesc())
                .thenReturn(Collections.emptyList());

        List<Payment> result = paymentService.getAllPayments();

        assertTrue(result.isEmpty());
    }

    @Test
    void getPaymentById_Found() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment1));

        Payment result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPaymentId());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
    }

    @Test
    void getPaymentById_NotFound_ThrowsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentService.getPaymentById(999L));
        assertTrue(exception.getMessage().contains("Payment not found"));
    }

    @Test
    void getPaymentsByInvoiceId_ReturnsList() {
        when(paymentRepository.findByInvoice_InvoiceId(1L))
                .thenReturn(Arrays.asList(testPayment1, testPayment2));

        List<Payment> result = paymentService.getPaymentsByInvoiceId(1L);

        assertEquals(2, result.size());
        verify(paymentRepository).findByInvoice_InvoiceId(1L);
    }

    @Test
    void getPaymentsByInvoiceId_EmptyForNonExistent() {
        when(paymentRepository.findByInvoice_InvoiceId(999L))
                .thenReturn(Collections.emptyList());

        List<Payment> result = paymentService.getPaymentsByInvoiceId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.createPayment(testPayment1);

        assertNotNull(result);
        assertEquals(1L, result.getPaymentId());
        verify(paymentRepository).save(testPayment1);
    }

    @Test
    void createPayment_WithDifferentMethods() {
        String[] methods = { "CREDIT_CARD", "DEBIT_CARD", "NET_BANKING", "UPI", "BANK_TRANSFER" };

        for (String method : methods) {
            testPayment1.setPaymentMethod(method);
            when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

            Payment result = paymentService.createPayment(testPayment1);

            assertEquals(method, result.getPaymentMethod());
        }
    }

    @Test
    void updatePayment_Success() {
        Payment updateDetails = new Payment();
        updateDetails.setPaymentDate(LocalDate.now());
        updateDetails.setAmount(150.0);
        updateDetails.setPaymentMethod("NET_BANKING");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment1));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.updatePayment(1L, updateDetails);

        assertNotNull(result);
        verify(paymentRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void updatePayment_NotFound_ThrowsException() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.updatePayment(999L, testPayment1));
    }

    @Test
    void deletePayment_Success() {
        doNothing().when(paymentRepository).deleteById(1L);

        assertDoesNotThrow(() -> paymentService.deletePayment(1L));
        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void createPayment_WithZeroAmount() {
        testPayment1.setAmount(0.0);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.createPayment(testPayment1);

        assertEquals(0.0, result.getAmount());
    }

    @Test
    void createPayment_WithLargeAmount() {
        testPayment1.setAmount(999999.99);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.createPayment(testPayment1);

        assertEquals(999999.99, result.getAmount());
    }

    @Test
    void createPayment_WithPastDate() {
        testPayment1.setPaymentDate(LocalDate.now().minusYears(1));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.createPayment(testPayment1);

        assertNotNull(result.getPaymentDate());
    }

    @Test
    void createPayment_WithNullInvoice() {
        testPayment1.setInvoice(null);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment1);

        Payment result = paymentService.createPayment(testPayment1);

        assertNull(result.getInvoice());
    }
}
