package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceModelTest {

    private Invoice invoice;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setUser(testUser);
        testCustomer.setFullName("John Doe");

        invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setCustomer(testCustomer);
        invoice.setBillingPeriodStart(LocalDate.of(2024, 1, 1));
        invoice.setBillingPeriodEnd(LocalDate.of(2024, 1, 31));
        invoice.setTotalAmount(150.00);
        invoice.setStatus("PENDING");
    }

    @Test
    void testInvoiceGettersAndSetters() {
        assertEquals(1L, invoice.getInvoiceId());
        assertEquals(testCustomer, invoice.getCustomer());
        assertEquals(LocalDate.of(2024, 1, 1), invoice.getBillingPeriodStart());
        assertEquals(LocalDate.of(2024, 1, 31), invoice.getBillingPeriodEnd());
        assertEquals(150.00, invoice.getTotalAmount());
        assertEquals("PENDING", invoice.getStatus());
    }

    @Test
    void testInvoiceBuilder() {
        Invoice builtInvoice = Invoice.builder()
                .invoiceId(2L)
                .customer(testCustomer)
                .billingPeriodStart(LocalDate.of(2024, 2, 1))
                .billingPeriodEnd(LocalDate.of(2024, 2, 29))
                .totalAmount(200.00)
                .status("PAID")
                .build();

        assertEquals(2L, builtInvoice.getInvoiceId());
        assertEquals(200.00, builtInvoice.getTotalAmount());
        assertEquals("PAID", builtInvoice.getStatus());
    }

    @Test
    void testInvoiceNoArgsConstructor() {
        Invoice emptyInvoice = new Invoice();
        assertNull(emptyInvoice.getInvoiceId());
        assertNull(emptyInvoice.getTotalAmount());
        assertNull(emptyInvoice.getStatus());
    }

    @Test
    void testInvoiceAllArgsConstructor() {
        Invoice fullInvoice = new Invoice(3L, testCustomer,
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31),
                300.00, "OVERDUE");

        assertEquals(3L, fullInvoice.getInvoiceId());
        assertEquals(300.00, fullInvoice.getTotalAmount());
        assertEquals("OVERDUE", fullInvoice.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        Invoice invoice1 = Invoice.builder()
                .invoiceId(1L)
                .totalAmount(150.00)
                .status("PENDING")
                .build();
        Invoice invoice2 = Invoice.builder()
                .invoiceId(1L)
                .totalAmount(150.00)
                .status("PENDING")
                .build();

        assertEquals(invoice1, invoice2);
        assertEquals(invoice1.hashCode(), invoice2.hashCode());
    }

    @Test
    void testNotEquals() {
        Invoice invoice1 = Invoice.builder()
                .invoiceId(1L)
                .totalAmount(150.00)
                .build();
        Invoice invoice2 = Invoice.builder()
                .invoiceId(2L)
                .totalAmount(200.00)
                .build();

        assertNotEquals(invoice1, invoice2);
    }

    @Test
    void testToString() {
        String toString = invoice.toString();
        assertTrue(toString.contains("150.0"));
        assertTrue(toString.contains("PENDING"));
    }

    @Test
    void testStatusValues() {
        invoice.setStatus("PAID");
        assertEquals("PAID", invoice.getStatus());

        invoice.setStatus("OVERDUE");
        assertEquals("OVERDUE", invoice.getStatus());

        invoice.setStatus("CANCELLED");
        assertEquals("CANCELLED", invoice.getStatus());
    }

    @Test
    void testBillingPeriod() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);

        invoice.setBillingPeriodStart(start);
        invoice.setBillingPeriodEnd(end);

        assertEquals(start, invoice.getBillingPeriodStart());
        assertEquals(end, invoice.getBillingPeriodEnd());
        assertTrue(invoice.getBillingPeriodEnd().isAfter(invoice.getBillingPeriodStart()));
    }

    @Test
    void testCustomerRelationship() {
        assertNotNull(invoice.getCustomer());
        assertEquals(1L, invoice.getCustomer().getCustomerId());
        assertEquals("John Doe", invoice.getCustomer().getFullName());
    }
}
