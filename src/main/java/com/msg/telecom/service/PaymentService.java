package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.repository.InvoiceRepository;
import com.msg.telecom.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Payment entities and payment processing.
 * <p>
 * This service handles all payment-related operations including creating,
 * updating, and retrieving payments. When a payment is created, the associated
 * invoice status is automatically updated to reflect the payment.
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Retrieves all payments from the database.
     * <p>
     * Payments are ordered by payment date descending, showing the most
     * recent payments first for better user experience.
     * </p>
     *
     * @return List of all payments with most recent first
     */
    public List<Payment> getAllPayments() {
        // Return payments ordered by payment date descending (most recent first)
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }

    /**
     * Retrieves a payment by its unique identifier.
     *
     * @param id The payment's unique identifier
     * @return The payment entity
     * @throws RuntimeException if payment is not found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    /**
     * Retrieves all payments for a specific invoice.
     *
     * @param invoiceId The invoice's unique identifier
     * @return List of payments for the specified invoice
     */
    public List<Payment> getPaymentsByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoice_InvoiceId(invoiceId);
    }

    /**
     * Creates a new payment and automatically updates the associated invoice
     * status.
     * <p>
     * When a payment is created, the linked invoice status is automatically
     * updated to "PAID" to reflect the payment. This ensures data consistency
     * and provides immediate feedback to users.
     * </p>
     *
     * @param payment The payment entity to create
     * @return The created payment with generated ID
     */
    public Payment createPayment(Payment payment) {
        // Save the payment first
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created new payment with ID: {} for invoice ID: {}",
                savedPayment.getPaymentId(),
                payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : "N/A");

        // Automatically update invoice status to PAID after payment is created
        if (payment.getInvoice() != null) {
            Invoice invoice = payment.getInvoice();
            invoice.setStatus("PAID");
            invoiceRepository.save(invoice);
            log.info("Updated invoice {} status to PAID after payment", invoice.getInvoiceId());
        }

        return savedPayment;
    }

    /**
     * Updates an existing payment's information.
     *
     * @param id             The payment's unique identifier
     * @param paymentDetails The payment data containing updated values
     * @return The updated payment entity
     * @throws RuntimeException if payment is not found
     */
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = getPaymentById(id);
        payment.setPaymentDate(paymentDetails.getPaymentDate());
        payment.setAmount(paymentDetails.getAmount());
        payment.setPaymentMethod(paymentDetails.getPaymentMethod());
        log.info("Updated payment with ID: {}", id);
        return paymentRepository.save(payment);
    }

    /**
     * Deletes a payment from the system.
     * <p>
     * Note: Deleting a payment does not revert the invoice status.
     * Manual intervention may be required to update invoice status.
     * </p>
     *
     * @param id The payment's unique identifier
     */
    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        paymentRepository.deleteById(id);
    }
}
