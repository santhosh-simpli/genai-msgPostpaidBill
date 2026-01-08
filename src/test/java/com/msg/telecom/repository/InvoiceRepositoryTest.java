package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = entityManager.persistAndFlush(testUser);

        testCustomer = new Customer();
        testCustomer.setFullName("John Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setEmail("john@test.com");
        testCustomer.setUser(testUser);
        testCustomer = entityManager.persistAndFlush(testCustomer);
    }

    @Test
    void findByStatus_ReturnsInvoices() {
        Invoice invoice = new Invoice();
        invoice.setStatus("PENDING");
        invoice.setCustomer(testCustomer);
        invoice.setTotalAmount(100.0);
        invoice.setBillingPeriodStart(LocalDate.now().minusMonths(1));
        invoice.setBillingPeriodEnd(LocalDate.now());
        invoiceRepository.save(invoice);

        List<Invoice> invoices = invoiceRepository.findByStatus("PENDING");
        assertFalse(invoices.isEmpty());
        assertEquals("PENDING", invoices.get(0).getStatus());
    }

    @Test
    void findByCustomer_CustomerId_ReturnsInvoices() {
        Invoice invoice = new Invoice();
        invoice.setCustomer(testCustomer);
        invoice.setStatus("PENDING");
        invoice.setTotalAmount(200.0);
        invoice.setBillingPeriodStart(LocalDate.now().minusMonths(1));
        invoice.setBillingPeriodEnd(LocalDate.now());
        invoiceRepository.save(invoice);

        List<Invoice> invoices = invoiceRepository.findByCustomer_CustomerId(testCustomer.getCustomerId());
        assertFalse(invoices.isEmpty());
        assertEquals(testCustomer.getCustomerId(), invoices.get(0).getCustomer().getCustomerId());
    }
}