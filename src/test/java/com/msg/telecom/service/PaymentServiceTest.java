package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setTotalAmount(200.0);

        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setInvoice(testInvoice);
        testPayment.setPaymentDate(LocalDate.now());
        testPayment.setAmount(200.0);
        testPayment.setPaymentMethod("CREDIT_CARD");
    }

    @Test
    void getAllPayments_ReturnsList() {
        when(paymentRepository.findAllByOrderByPaymentIdDesc()).thenReturn(List.of(testPayment));
        List<Payment> payments = paymentService.getAllPayments();
        assertEquals(1, payments.size());
        assertEquals(200.0, payments.get(0).getAmount());
        verify(paymentRepository, times(1)).findAllByOrderByPaymentIdDesc();
    }

    @Test
    void getAllPayments_ReturnsEmptyList() {
        when(paymentRepository.findAllByOrderByPaymentIdDesc()).thenReturn(Collections.emptyList());
        List<Payment> payments = paymentService.getAllPayments();
        assertTrue(payments.isEmpty());
        verify(paymentRepository, times(1)).findAllByOrderByPaymentIdDesc();
    }

    @Test
    void getPaymentById_Found() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        Payment result = paymentService.getPaymentById(1L);
        assertEquals(1L, result.getPaymentId());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> paymentService.getPaymentById(999L));
        assertTrue(ex.getMessage().contains("Payment not found"));
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    void getPaymentsByInvoiceId_ReturnsList() {
        when(paymentRepository.findByInvoice_InvoiceId(1L)).thenReturn(List.of(testPayment));
        List<Payment> payments = paymentService.getPaymentsByInvoiceId(1L);
        assertEquals(1, payments.size());
        verify(paymentRepository, times(1)).findByInvoice_InvoiceId(1L);
    }

    @Test
    void getPaymentsByInvoiceId_ReturnsEmptyList() {
        when(paymentRepository.findByInvoice_InvoiceId(999L)).thenReturn(Collections.emptyList());
        List<Payment> payments = paymentService.getPaymentsByInvoiceId(999L);
        assertTrue(payments.isEmpty());
        verify(paymentRepository, times(1)).findByInvoice_InvoiceId(999L);
    }

    @Test
    void createPayment_Success() {
        Payment newPayment = new Payment();
        newPayment.setInvoice(testInvoice);
        newPayment.setAmount(100.0);
        newPayment.setPaymentMethod("BANK_TRANSFER");

        when(paymentRepository.save(any(Payment.class))).thenReturn(newPayment);
        Payment created = paymentService.createPayment(newPayment);

        assertNotNull(created);
        assertEquals(100.0, created.getAmount());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void createPayment_NullInvoice() {
        Payment newPayment = new Payment();
        newPayment.setInvoice(null);
        newPayment.setAmount(100.0);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> paymentService.createPayment(newPayment));
        assertTrue(ex.getMessage().contains("Invoice cannot be null"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void updatePayment_Success() {
        Payment updateDetails = new Payment();
        updateDetails.setPaymentDate(LocalDate.now().plusDays(1));
        updateDetails.setAmount(250.0);
        updateDetails.setPaymentMethod("DEBIT_CARD");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        Payment updated = paymentService.updatePayment(1L, updateDetails);
        assertNotNull(updated);
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void updatePayment_InvalidAmount() {
        Payment updateDetails = new Payment();
        updateDetails.setAmount(-50.0); // Negative amount

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> paymentService.updatePayment(1L, updateDetails));
        assertTrue(ex.getMessage().contains("Invalid payment amount"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void updatePayment_NotFound() {
        Payment updateDetails = new Payment();
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentService.updatePayment(999L, updateDetails));
        verify(paymentRepository, times(1)).findById(999L);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void deletePayment_Success() {
        doNothing().when(paymentRepository).deleteById(1L);

        assertDoesNotThrow(() -> paymentService.deletePayment(1L));
        verify(paymentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePayment_NonExistent() {
        doNothing().when(paymentRepository).deleteById(999L);

        assertDoesNotThrow(() -> paymentService.deletePayment(999L));
        verify(paymentRepository, times(1)).deleteById(999L);
    }
}
