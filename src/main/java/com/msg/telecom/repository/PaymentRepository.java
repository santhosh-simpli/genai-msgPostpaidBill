package com.msg.telecom.repository;

import com.msg.telecom.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Payment entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing payment data.
 * Supports ordered retrieval to display most recent payments first.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Finds all payments for a specific invoice.
     *
     * @param invoiceId The invoice's unique identifier
     * @return List of payments for the invoice
     */
    List<Payment> findByInvoice_InvoiceId(Long invoiceId);

    /**
     * Retrieves all payments ordered by payment date descending (most recent
     * first).
     * <p>
     * This ensures newly made payments appear at the top of payment lists.
     * </p>
     *
     * @return List of all payments with most recent payments first
     */
    List<Payment> findAllByOrderByPaymentDateDesc();

    /**
     * Retrieves all payments ordered by payment ID descending.
     *
     * @return List of all payments with newest entries first
     */
    List<Payment> findAllByOrderByPaymentIdDesc();
}
