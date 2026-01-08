package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFullName("John Doe");
        
        invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setCustomer(customer);
        invoice.setTotalAmount(150.00);
        invoice.setStatus("PENDING");
        
        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setInvoice(invoice);
        payment.setPaymentDate(LocalDate.of(2026, 1, 15));
        payment.setAmount(150.00);
        payment.setPaymentMethod("CARD");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, payment.getPaymentId());
        assertEquals(invoice, payment.getInvoice());
        assertEquals(LocalDate.of(2026, 1, 15), payment.getPaymentDate());
        assertEquals(150.00, payment.getAmount());
        assertEquals("CARD", payment.getPaymentMethod());
    }

    @Test
    void testEquality() {
        Payment payment1 = new Payment();
        payment1.setPaymentId(1L);
        payment1.setAmount(150.00);
        payment1.setPaymentMethod("CARD");

        Payment payment2 = new Payment();
        payment2.setPaymentId(1L);
        payment2.setAmount(150.00);
        payment2.setPaymentMethod("CARD");

        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());
    }

    @Test
    void testInequality() {
        Payment payment1 = new Payment();
        payment1.setPaymentId(1L);
        payment1.setAmount(150.00);

        Payment payment2 = new Payment();
        payment2.setPaymentId(2L);
        payment2.setAmount(200.00);

        assertNotEquals(payment1, payment2);
    }

    @Test
    void testToString() {
        String toString = payment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("150.0"));
        assertTrue(toString.contains("CARD"));
    }

    @Test
    void testNoArgsConstructor() {
        Payment emptyPayment = new Payment();
        assertNull(emptyPayment.getPaymentId());
        assertNull(emptyPayment.getInvoice());
        assertNull(emptyPayment.getPaymentDate());
        assertNull(emptyPayment.getAmount());
        assertNull(emptyPayment.getPaymentMethod());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2026, 2, 20);
        Payment fullPayment = new Payment(2L, invoice, date, 200.00, "CASH");
        
        assertEquals(2L, fullPayment.getPaymentId());
        assertEquals(invoice, fullPayment.getInvoice());
        assertEquals(date, fullPayment.getPaymentDate());
        assertEquals(200.00, fullPayment.getAmount());
        assertEquals("CASH", fullPayment.getPaymentMethod());
    }

    @Test
    void testBuilder() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        Payment builtPayment = Payment.builder()
                .paymentId(3L)
                .invoice(invoice)
                .paymentDate(date)
                .amount(300.00)
                .paymentMethod("TRANSFER")
                .build();
        
        assertEquals(3L, builtPayment.getPaymentId());
        assertEquals(invoice, builtPayment.getInvoice());
        assertEquals(date, builtPayment.getPaymentDate());
        assertEquals(300.00, builtPayment.getAmount());
        assertEquals("TRANSFER", builtPayment.getPaymentMethod());
    }

    @Test
    void testSetters() {
        Payment testPayment = new Payment();
        LocalDate date = LocalDate.of(2026, 4, 5);
        
        testPayment.setPaymentId(100L);
        testPayment.setInvoice(invoice);
        testPayment.setPaymentDate(date);
        testPayment.setAmount(500.00);
        testPayment.setPaymentMethod("CHECK");
        
        assertEquals(100L, testPayment.getPaymentId());
        assertEquals(invoice, testPayment.getInvoice());
        assertEquals(date, testPayment.getPaymentDate());
        assertEquals(500.00, testPayment.getAmount());
        assertEquals("CHECK", testPayment.getPaymentMethod());
    }

    @Test
    void testAllPaymentMethods() {
        String[] methods = {"CARD", "CASH", "TRANSFER", "CHECK", "ONLINE", "MOBILE"};
        
        for (String method : methods) {
            payment.setPaymentMethod(method);
            assertEquals(method, payment.getPaymentMethod());
        }
    }

    @Test
    void testZeroAmount() {
        payment.setAmount(0.0);
        assertEquals(0.0, payment.getAmount());
    }

    @Test
    void testLargeAmount() {
        payment.setAmount(999999.99);
        assertEquals(999999.99, payment.getAmount());
    }

    @Test
    void testPartialPayment() {
        payment.setAmount(50.00);
        assertEquals(50.00, payment.getAmount());
        assertTrue(payment.getAmount() < invoice.getTotalAmount());
    }

    @Test
    void testOverPayment() {
        payment.setAmount(200.00);
        assertEquals(200.00, payment.getAmount());
        assertTrue(payment.getAmount() > invoice.getTotalAmount());
    }

    @Test
    void testInvoiceRelationship() {
        Invoice newInvoice = new Invoice();
        newInvoice.setInvoiceId(2L);
        newInvoice.setTotalAmount(250.00);
        
        payment.setInvoice(newInvoice);
        assertEquals(newInvoice, payment.getInvoice());
        assertEquals(2L, payment.getInvoice().getInvoiceId());
    }

    @Test
    void testNullInvoice() {
        Payment paymentWithNullInvoice = new Payment();
        paymentWithNullInvoice.setPaymentId(1L);
        paymentWithNullInvoice.setInvoice(null);
        
        assertNull(paymentWithNullInvoice.getInvoice());
    }

    @Test
    void testDifferentDates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate futureDate = LocalDate.now().plusDays(30);
        
        payment.setPaymentDate(today);
        assertEquals(today, payment.getPaymentDate());
        
        payment.setPaymentDate(yesterday);
        assertEquals(yesterday, payment.getPaymentDate());
        
        payment.setPaymentDate(futureDate);
        assertEquals(futureDate, payment.getPaymentDate());
    }
}
