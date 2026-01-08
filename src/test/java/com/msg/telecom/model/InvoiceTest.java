package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    private Invoice invoice;
    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setUser(user);
        customer.setFullName("John Doe");
        
        invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setCustomer(customer);
        invoice.setBillingPeriodStart(LocalDate.of(2026, 1, 1));
        invoice.setBillingPeriodEnd(LocalDate.of(2026, 1, 31));
        invoice.setTotalAmount(150.00);
        invoice.setStatus("PENDING");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, invoice.getInvoiceId());
        assertEquals(customer, invoice.getCustomer());
        assertEquals(LocalDate.of(2026, 1, 1), invoice.getBillingPeriodStart());
        assertEquals(LocalDate.of(2026, 1, 31), invoice.getBillingPeriodEnd());
        assertEquals(150.00, invoice.getTotalAmount());
        assertEquals("PENDING", invoice.getStatus());
    }

    @Test
    void testEquality() {
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceId(1L);
        invoice1.setTotalAmount(150.00);
        invoice1.setStatus("PENDING");

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceId(1L);
        invoice2.setTotalAmount(150.00);
        invoice2.setStatus("PENDING");

        assertEquals(invoice1, invoice2);
        assertEquals(invoice1.hashCode(), invoice2.hashCode());
    }

    @Test
    void testInequality() {
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceId(1L);
        invoice1.setTotalAmount(150.00);

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceId(2L);
        invoice2.setTotalAmount(200.00);

        assertNotEquals(invoice1, invoice2);
    }

    @Test
    void testToString() {
        String toString = invoice.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("150.0"));
        assertTrue(toString.contains("PENDING"));
    }

    @Test
    void testNoArgsConstructor() {
        Invoice emptyInvoice = new Invoice();
        assertNull(emptyInvoice.getInvoiceId());
        assertNull(emptyInvoice.getCustomer());
        assertNull(emptyInvoice.getBillingPeriodStart());
        assertNull(emptyInvoice.getBillingPeriodEnd());
        assertNull(emptyInvoice.getTotalAmount());
        assertNull(emptyInvoice.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate start = LocalDate.of(2026, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);
        Invoice fullInvoice = new Invoice(2L, customer, start, end, 200.00, "PAID");
        
        assertEquals(2L, fullInvoice.getInvoiceId());
        assertEquals(customer, fullInvoice.getCustomer());
        assertEquals(start, fullInvoice.getBillingPeriodStart());
        assertEquals(end, fullInvoice.getBillingPeriodEnd());
        assertEquals(200.00, fullInvoice.getTotalAmount());
        assertEquals("PAID", fullInvoice.getStatus());
    }

    @Test
    void testBuilder() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);
        Invoice builtInvoice = Invoice.builder()
                .invoiceId(3L)
                .customer(customer)
                .billingPeriodStart(start)
                .billingPeriodEnd(end)
                .totalAmount(300.00)
                .status("OVERDUE")
                .build();
        
        assertEquals(3L, builtInvoice.getInvoiceId());
        assertEquals(customer, builtInvoice.getCustomer());
        assertEquals(start, builtInvoice.getBillingPeriodStart());
        assertEquals(end, builtInvoice.getBillingPeriodEnd());
        assertEquals(300.00, builtInvoice.getTotalAmount());
        assertEquals("OVERDUE", builtInvoice.getStatus());
    }

    @Test
    void testSetters() {
        Invoice testInvoice = new Invoice();
        LocalDate start = LocalDate.of(2026, 4, 1);
        LocalDate end = LocalDate.of(2026, 4, 30);
        
        testInvoice.setInvoiceId(100L);
        testInvoice.setCustomer(customer);
        testInvoice.setBillingPeriodStart(start);
        testInvoice.setBillingPeriodEnd(end);
        testInvoice.setTotalAmount(500.00);
        testInvoice.setStatus("CANCELLED");
        
        assertEquals(100L, testInvoice.getInvoiceId());
        assertEquals(customer, testInvoice.getCustomer());
        assertEquals(start, testInvoice.getBillingPeriodStart());
        assertEquals(end, testInvoice.getBillingPeriodEnd());
        assertEquals(500.00, testInvoice.getTotalAmount());
        assertEquals("CANCELLED", testInvoice.getStatus());
    }

    @Test
    void testAllStatuses() {
        String[] statuses = {"PENDING", "PAID", "OVERDUE", "CANCELLED", "PARTIALLY_PAID"};
        
        for (String status : statuses) {
            invoice.setStatus(status);
            assertEquals(status, invoice.getStatus());
        }
    }

    @Test
    void testZeroTotalAmount() {
        invoice.setTotalAmount(0.0);
        assertEquals(0.0, invoice.getTotalAmount());
    }

    @Test
    void testLargeTotalAmount() {
        invoice.setTotalAmount(999999.99);
        assertEquals(999999.99, invoice.getTotalAmount());
    }

    @Test
    void testNegativeTotalAmount() {
        invoice.setTotalAmount(-50.00);
        assertEquals(-50.00, invoice.getTotalAmount());
    }

    @Test
    void testCustomerRelationship() {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerId(2L);
        newCustomer.setFullName("Jane Doe");
        
        invoice.setCustomer(newCustomer);
        assertEquals(newCustomer, invoice.getCustomer());
        assertEquals(2L, invoice.getCustomer().getCustomerId());
    }

    @Test
    void testNullCustomer() {
        Invoice invoiceWithNullCustomer = new Invoice();
        invoiceWithNullCustomer.setInvoiceId(1L);
        invoiceWithNullCustomer.setCustomer(null);
        
        assertNull(invoiceWithNullCustomer.getCustomer());
    }

    @Test
    void testBillingPeriodDates() {
        LocalDate start = LocalDate.of(2026, 12, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        
        invoice.setBillingPeriodStart(start);
        invoice.setBillingPeriodEnd(end);
        
        assertEquals(start, invoice.getBillingPeriodStart());
        assertEquals(end, invoice.getBillingPeriodEnd());
        assertTrue(invoice.getBillingPeriodEnd().isAfter(invoice.getBillingPeriodStart()));
    }
}
