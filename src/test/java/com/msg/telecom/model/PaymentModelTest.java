package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentModelTest {

    private Payment payment;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        Customer testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("John Doe");

        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setCustomer(testCustomer);
        testInvoice.setTotalAmount(150.00);
        testInvoice.setStatus("PENDING");

        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setInvoice(testInvoice);
        payment.setPaymentDate(LocalDate.of(2024, 1, 15));
        payment.setAmount(150.00);
        payment.setPaymentMethod("CREDIT_CARD");
    }

    @Test
    void testPaymentGettersAndSetters() {
        assertEquals(1L, payment.getPaymentId());
        assertEquals(testInvoice, payment.getInvoice());
        assertEquals(LocalDate.of(2024, 1, 15), payment.getPaymentDate());
        assertEquals(150.00, payment.getAmount());
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
    }

    @Test
    void testPaymentBuilder() {
        Payment builtPayment = Payment.builder()
                .paymentId(2L)
                .invoice(testInvoice)
                .paymentDate(LocalDate.of(2024, 2, 15))
                .amount(200.00)
                .paymentMethod("DEBIT_CARD")
                .build();

        assertEquals(2L, builtPayment.getPaymentId());
        assertEquals(200.00, builtPayment.getAmount());
        assertEquals("DEBIT_CARD", builtPayment.getPaymentMethod());
    }

    @Test
    void testPaymentNoArgsConstructor() {
        Payment emptyPayment = new Payment();
        assertNull(emptyPayment.getPaymentId());
        assertNull(emptyPayment.getAmount());
        assertNull(emptyPayment.getPaymentMethod());
    }

    @Test
    void testPaymentAllArgsConstructor() {
        Payment fullPayment = new Payment(3L, testInvoice,
                LocalDate.of(2024, 3, 15), 300.00, "NET_BANKING");

        assertEquals(3L, fullPayment.getPaymentId());
        assertEquals(300.00, fullPayment.getAmount());
        assertEquals("NET_BANKING", fullPayment.getPaymentMethod());
    }

    @Test
    void testEqualsAndHashCode() {
        Payment payment1 = Payment.builder()
                .paymentId(1L)
                .amount(150.00)
                .paymentMethod("CREDIT_CARD")
                .build();
        Payment payment2 = Payment.builder()
                .paymentId(1L)
                .amount(150.00)
                .paymentMethod("CREDIT_CARD")
                .build();

        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());
    }

    @Test
    void testNotEquals() {
        Payment payment1 = Payment.builder()
                .paymentId(1L)
                .amount(150.00)
                .build();
        Payment payment2 = Payment.builder()
                .paymentId(2L)
                .amount(200.00)
                .build();

        assertNotEquals(payment1, payment2);
    }

    @Test
    void testToString() {
        String toString = payment.toString();
        assertTrue(toString.contains("150.0"));
        assertTrue(toString.contains("CREDIT_CARD"));
    }

    @Test
    void testPaymentMethods() {
        payment.setPaymentMethod("CREDIT_CARD");
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());

        payment.setPaymentMethod("DEBIT_CARD");
        assertEquals("DEBIT_CARD", payment.getPaymentMethod());

        payment.setPaymentMethod("NET_BANKING");
        assertEquals("NET_BANKING", payment.getPaymentMethod());

        payment.setPaymentMethod("UPI");
        assertEquals("UPI", payment.getPaymentMethod());
    }

    @Test
    void testInvoiceRelationship() {
        assertNotNull(payment.getInvoice());
        assertEquals(1L, payment.getInvoice().getInvoiceId());
        assertEquals(150.00, payment.getInvoice().getTotalAmount());
    }

    @Test
    void testPaymentDateManipulation() {
        LocalDate today = LocalDate.now();
        payment.setPaymentDate(today);
        assertEquals(today, payment.getPaymentDate());
    }
}
