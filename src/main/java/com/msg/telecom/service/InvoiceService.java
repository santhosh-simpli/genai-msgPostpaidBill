package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Invoice entities and billing operations.
 * <p>
 * This service handles all invoice-related operations including creating,
 * updating, retrieving, and managing invoice statuses. Invoices are always
 * returned in descending order by ID to show the most recent first.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    /**
     * Retrieves all invoices from the database.
     * <p>
     * Invoices are ordered by invoice ID descending, showing the most
     * recent invoices first. Paid invoices will appear at the top
     * immediately after payment is recorded.
     * </p>
     *
     * @return List of all invoices with most recent first
     */
    public List<Invoice> getAllInvoices() {
        // Return invoices ordered by invoice ID descending (most recent first)
        return invoiceRepository.findAllByOrderByInvoiceIdDesc();
    }

    /**
     * Retrieves an invoice by its unique identifier.
     *
     * @param id The invoice's unique identifier
     * @return The invoice entity
     * @throws RuntimeException if invoice is not found
     */
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    /**
     * Retrieves all invoices for a specific customer.
     * <p>
     * Invoices are ordered by invoice ID descending to show most recent first.
     * </p>
     *
     * @param customerId The customer's unique identifier
     * @return List of invoices for the specified customer
     */
    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(customerId);
    }

    /**
     * Creates a new invoice for a customer.
     * <p>
     * New invoices are typically created with "PENDING" status and will
     * be updated to "PAID" when a payment is recorded.
     * </p>
     *
     * @param invoice The invoice entity to create
     * @return The created invoice with generated ID
     */
    public Invoice createInvoice(Invoice invoice) {
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Created new invoice with ID: {} for customer ID: {}",
                savedInvoice.getInvoiceId(),
                invoice.getCustomer() != null ? invoice.getCustomer().getCustomerId() : "N/A");
        return savedInvoice;
    }

    /**
     * Updates an existing invoice's information.
     *
     * @param id             The invoice's unique identifier
     * @param invoiceDetails The invoice data containing updated values
     * @return The updated invoice entity
     * @throws RuntimeException if invoice is not found
     */
    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Invoice invoice = getInvoiceById(id);
        invoice.setBillingPeriodStart(invoiceDetails.getBillingPeriodStart());
        invoice.setBillingPeriodEnd(invoiceDetails.getBillingPeriodEnd());
        invoice.setTotalAmount(invoiceDetails.getTotalAmount());
        invoice.setStatus(invoiceDetails.getStatus());
        log.info("Updated invoice with ID: {} - Status: {}", id, invoiceDetails.getStatus());
        return invoiceRepository.save(invoice);
    }

    /**
     * Marks an invoice as paid.
     * <p>
     * This is a convenience method to quickly update invoice status to PAID.
     * </p>
     *
     * @param id The invoice's unique identifier
     * @return The updated invoice with PAID status
     * @throws RuntimeException if invoice is not found
     */
    public Invoice markAsPaid(Long id) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus("PAID");
        log.info("Marked invoice {} as PAID", id);
        return invoiceRepository.save(invoice);
    }

    /**
     * Deletes an invoice from the system.
     * <p>
     * Note: This will also cascade delete any associated payments.
     * </p>
     *
     * @param id The invoice's unique identifier
     */
    public void deleteInvoice(Long id) {
        log.info("Deleting invoice with ID: {}", id);
        invoiceRepository.deleteById(id);
    }
}
