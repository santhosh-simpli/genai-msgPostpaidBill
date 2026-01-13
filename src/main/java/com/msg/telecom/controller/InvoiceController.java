package com.msg.telecom.controller;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.model.User;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.PaymentService;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.InvoiceDto;
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
 * REST Controller for managing Invoice resources.
 * <p>
 * This controller handles all HTTP requests related to invoice management
 * including creating invoices, recording payments, and retrieving invoice
 * history.
 * </p>
 * <p>
 * Invoices are returned in descending order by ID (most recent first).
 * When a payment is recorded for an invoice, the invoice status is
 * automatically
 * updated to "PAID" and will appear at the top of lists.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final CustomerService customerService;

    /**
     * Retrieves all invoices based on user role.
     * <p>
     * ADMIN and OPERATOR roles can view all invoices.
     * CUSTOMER role can only view their own invoices.
     * Invoices are ordered by ID descending (most recent first).
     * </p>
     *
     * @param authentication The current authentication context
     * @return ResponseEntity containing list of InvoiceDto objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Invoice> invoices;

        // Role-based filtering of invoice data
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            invoices = invoiceService.getAllInvoices();
        } else {
            // Customer can only see their own invoices
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            invoices = invoiceService.getInvoicesByCustomerId(customers.get(0).getCustomerId());
        }

        List<InvoiceDto> dtos = invoices.stream().map(this::toInvoiceDto).toList();
        log.debug("Retrieved {} invoices for user: {}", dtos.size(), authentication.getName());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Creates a new invoice for a customer.
     * <p>
     * New invoices are created with "PENDING" status. When a payment is recorded,
     * the status will be automatically updated to "PAID".
     * </p>
     *
     * @param invoiceDto The invoice data to create
     * @return ResponseEntity containing the created InvoiceDto
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoiceDto) {
        Customer customer = customerService.getCustomerById(invoiceDto.getCustomerId());

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setTotalAmount(invoiceDto.getTotalAmount() != null ? invoiceDto.getTotalAmount() : 0.0);
        invoice.setStatus("PENDING");

        // Handle billing dates - support both naming conventions for API flexibility
        String startDateStr = invoiceDto.getBillingStartDate() != null ? invoiceDto.getBillingStartDate()
                : invoiceDto.getBillingPeriodStart();
        String endDateStr = invoiceDto.getBillingEndDate() != null ? invoiceDto.getBillingEndDate()
                : invoiceDto.getBillingPeriodEnd();

        // Parse and set billing period start date with default fallback
        if (startDateStr != null && !startDateStr.isEmpty()) {
            invoice.setBillingPeriodStart(LocalDate.parse(startDateStr));
        } else {
            invoice.setBillingPeriodStart(LocalDate.now().withDayOfMonth(1));
        }

        // Parse and set billing period end date with default fallback
        if (endDateStr != null && !endDateStr.isEmpty()) {
            invoice.setBillingPeriodEnd(LocalDate.parse(endDateStr));
        } else {
            invoice.setBillingPeriodEnd(LocalDate.now());
        }

        Invoice created = invoiceService.createInvoice(invoice);
        log.info("Created new invoice {} for customer {}", created.getInvoiceId(), customer.getCustomerId());
        return ResponseEntity.ok(toInvoiceDto(created));
    }

    /**
     * Records a payment for a specific invoice.
     * <p>
     * When a payment is recorded, the invoice status is automatically updated
     * to "PAID". The payment will appear at the top of payment lists immediately.
     * </p>
     *
     * @param id             The invoice's unique identifier
     * @param paymentDto     The payment data to record
     * @param authentication The current authentication context
     * @return ResponseEntity containing the created PaymentDto or 403 if
     *         unauthorized
     */
    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PaymentDto> recordPayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto,
            Authentication authentication) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        User currentUser = userService.getUserByUsername(authentication.getName());

        // Customers can only record payments for their own invoices
        if (currentUser.getRole().name().equals("CUSTOMER")) {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty()
                    || !invoice.getCustomer().getCustomerId().equals(customers.get(0).getCustomerId())) {
                log.warn("Unauthorized payment attempt by user {} for invoice {}",
                        currentUser.getUsername(), id);
                return ResponseEntity.status(403).build();
            }
        }

        Payment payment = toPaymentEntity(paymentDto);
        payment.setInvoice(invoice);
        Payment created = paymentService.createPayment(payment);
        log.info("Recorded payment {} for invoice {} - Invoice now marked as PAID",
                created.getPaymentId(), id);
        return ResponseEntity.ok(toPaymentDto(created));
    }

    /**
     * Retrieves all payments for a specific invoice.
     *
     * @param id The invoice's unique identifier
     * @return ResponseEntity containing list of PaymentDto objects
     */
    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getInvoicePayments(@PathVariable Long id) {
        List<Payment> payments = paymentService.getPaymentsByInvoiceId(id);
        List<PaymentDto> dtos = payments.stream().map(this::toPaymentDto).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Converts an Invoice entity to InvoiceDto.
     *
     * @param invoice The invoice entity to convert
     * @return InvoiceDto with relevant data
     */
    private InvoiceDto toInvoiceDto(Invoice invoice) {
        InvoiceDto dto = new InvoiceDto();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setCustomerId(invoice.getCustomer() != null ? invoice.getCustomer().getCustomerId() : null);
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setDueDate(invoice.getBillingPeriodEnd() != null ? invoice.getBillingPeriodEnd().toString() : null);
        dto.setBillingPeriodStart(
                invoice.getBillingPeriodStart() != null ? invoice.getBillingPeriodStart().toString() : null);
        dto.setBillingPeriodEnd(
                invoice.getBillingPeriodEnd() != null ? invoice.getBillingPeriodEnd().toString() : null);
        return dto;
    }

    /**
     * Converts a Payment entity to PaymentDto.
     *
     * @param payment The payment entity to convert
     * @return PaymentDto with relevant data
     */
    private PaymentDto toPaymentDto(Payment payment) {
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
    private Payment toPaymentEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getStatus());

        // Set payment date to current date if not provided
        if (dto.getPaymentDate() != null && !dto.getPaymentDate().isEmpty()) {
            payment.setPaymentDate(LocalDate.parse(dto.getPaymentDate()));
        } else {
            payment.setPaymentDate(LocalDate.now());
        }

        return payment;
    }
}
