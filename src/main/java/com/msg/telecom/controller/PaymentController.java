package com.msg.telecom.controller;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.model.User;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.PaymentService;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller for managing Payment resources.
 * <p>
 * This controller handles all HTTP requests related to payment processing
 * including creating payments and retrieving payment history. When a payment
 * is created, the associated invoice is automatically marked as PAID.
 * </p>
 * <p>
 * Payments are returned in descending order by payment date (most recent first)
 * to ensure newly processed payments appear at the top of the list immediately.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final CustomerService customerService;

    /**
     * Retrieves all payments based on user role.
     * <p>
     * ADMIN and OPERATOR roles can view all payments.
     * CUSTOMER role can only view payments for their own invoices.
     * Payments are ordered by date descending (most recent first).
     * </p>
     *
     * @param authentication The current authentication context
     * @return ResponseEntity containing list of PaymentDto objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<PaymentDto>> getAllPayments(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Payment> payments;
        
        // Role-based filtering of payment data
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            payments = paymentService.getAllPayments();
        } else {
            // Customer can only see their own payments
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            payments = new ArrayList<>();
            if (!customers.isEmpty()) {
                invoiceService.getInvoicesByCustomerId(customers.get(0).getCustomerId()).forEach(invoice -> {
                    payments.addAll(paymentService.getPaymentsByInvoiceId(invoice.getInvoiceId()));
                });
            }
        }
        
        List<PaymentDto> dtos = payments.stream().map(this::toDto).toList();
        log.debug("Retrieved {} payments for user: {}", dtos.size(), authentication.getName());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Creates a new payment for an invoice.
     * <p>
     * When a payment is created, the associated invoice status is automatically
     * updated to "PAID". The payment will appear at the top of the payment list
     * immediately after creation.
     * </p>
     *
     * @param paymentDto     The payment data to create
     * @param authentication The current authentication context
     * @return ResponseEntity containing the created PaymentDto or 403 if unauthorized
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto, Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        
        // Validate invoice exists
        if (paymentDto.getInvoiceId() == null) {
            log.warn("Payment creation failed: No invoice ID provided");
            return ResponseEntity.badRequest().build();
        }
        
        Invoice invoice = invoiceService.getInvoiceById(paymentDto.getInvoiceId());
        
        // Customers can only create payments for their own invoices
        if (currentUser.getRole().name().equals("CUSTOMER")) {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty() || 
                !invoice.getCustomer().getCustomerId().equals(customers.get(0).getCustomerId())) {
                log.warn("Unauthorized payment attempt by user {} for invoice {}", 
                        currentUser.getUsername(), paymentDto.getInvoiceId());
                return ResponseEntity.status(403).build();
            }
        }
        
        Payment payment = toEntity(paymentDto);
        payment.setInvoice(invoice);
        Payment created = paymentService.createPayment(payment);
        log.info("Created payment {} for invoice {} - Invoice now marked as PAID", 
                created.getPaymentId(), invoice.getInvoiceId());
        return ResponseEntity.ok(toDto(created));
    }

    /**
     * Converts a Payment entity to PaymentDto.
     *
     * @param payment The payment entity to convert
     * @return PaymentDto with relevant data
     */
    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceId(payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
        dto.setStatus(payment.getPaymentMethod());
        return dto;
    }

    /**
     * Converts a PaymentDto to Payment entity.
     *
     * @param dto The PaymentDto to convert
     * @return Payment entity
     */
    private Payment toEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getStatus());
        
        // Set payment date to now if not provided
        if (dto.getPaymentDate() != null && !dto.getPaymentDate().isEmpty()) {
            payment.setPaymentDate(LocalDate.parse(dto.getPaymentDate()));
        } else {
            payment.setPaymentDate(LocalDate.now());
        }
        
        return payment;
    }
}
