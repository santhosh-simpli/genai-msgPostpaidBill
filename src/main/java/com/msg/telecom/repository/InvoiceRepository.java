package com.msg.telecom.repository;

import com.msg.telecom.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Invoice entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing invoice data.
 * Supports ordered retrieval to display most recent invoices first.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * Finds all invoices for a specific customer.
     *
     * @param customerId The customer's unique identifier
     * @return List of invoices for the customer
     */
    List<Invoice> findByCustomer_CustomerId(Long customerId);

    /**
     * Finds all invoices with a specific status.
     *
     * @param status The invoice status (PENDING, PAID, OVERDUE)
     * @return List of invoices with the specified status
     */
    List<Invoice> findByStatus(String status);

    /**
     * Retrieves all invoices ordered by invoice ID descending (most recent first).
     * <p>
     * This ensures newly created or updated invoices appear at the top of lists.
     * </p>
     *
     * @return List of all invoices with newest entries first
     */
    List<Invoice> findAllByOrderByInvoiceIdDesc();

    /**
     * Retrieves all invoices for a customer ordered by invoice ID descending.
     *
     * @param customerId The customer's unique identifier
     * @return List of customer's invoices with newest entries first
     */
    List<Invoice> findByCustomer_CustomerIdOrderByInvoiceIdDesc(Long customerId);
}
