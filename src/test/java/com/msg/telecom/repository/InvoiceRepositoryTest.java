package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void findByStatus_ReturnsInvoices() {
        Invoice invoice = new Invoice();
        invoice.setStatus("PENDING");
        invoiceRepository.save(invoice);

        List<Invoice> invoices = invoiceRepository.findByStatus("PENDING");
        assertFalse(invoices.isEmpty());
        assertEquals("PENDING", invoices.get(0).getStatus());
    }

    @Test
    void findByCustomer_CustomerId_ReturnsInvoices() {
        Customer customer = new Customer();
        customer.setFullName("John Doe");

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoiceRepository.save(invoice);

        List<Invoice> invoices = invoiceRepository.findByCustomer_CustomerId(customer.getCustomerId());
        assertFalse(invoices.isEmpty());
        assertEquals(customer.getCustomerId(), invoices.get(0).getCustomer().getCustomerId());
    }
}