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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Invoice> invoices;
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            invoices = invoiceService.getAllInvoices();
        } else {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            invoices = invoiceService.getInvoicesByCustomerId(customers.get(0).getCustomerId());
        }
        List<InvoiceDto> dtos = invoices.stream().map(this::toInvoiceDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<InvoiceDto> createInvoice(@RequestBody InvoiceDto invoiceDto) {
        Customer customer = customerService.getCustomerById(invoiceDto.getCustomerId());

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setTotalAmount(invoiceDto.getTotalAmount() != null ? invoiceDto.getTotalAmount() : 0.0);
        invoice.setStatus("PENDING");

        // Handle billing dates - support both naming conventions
        String startDateStr = invoiceDto.getBillingStartDate() != null ? invoiceDto.getBillingStartDate()
                : invoiceDto.getBillingPeriodStart();
        String endDateStr = invoiceDto.getBillingEndDate() != null ? invoiceDto.getBillingEndDate()
                : invoiceDto.getBillingPeriodEnd();

        if (startDateStr != null && !startDateStr.isEmpty()) {
            invoice.setBillingPeriodStart(LocalDate.parse(startDateStr));
        } else {
            invoice.setBillingPeriodStart(LocalDate.now().withDayOfMonth(1));
        }

        if (endDateStr != null && !endDateStr.isEmpty()) {
            invoice.setBillingPeriodEnd(LocalDate.parse(endDateStr));
        } else {
            invoice.setBillingPeriodEnd(LocalDate.now());
        }

        Invoice created = invoiceService.createInvoice(invoice);
        return ResponseEntity.ok(toInvoiceDto(created));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PaymentDto> recordPayment(@PathVariable Long id, @RequestBody PaymentDto paymentDto,
            Authentication authentication) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        User currentUser = userService.getUserByUsername(authentication.getName());
        if (currentUser.getRole().name().equals("CUSTOMER")) {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty()
                    || !invoice.getCustomer().getCustomerId().equals(customers.get(0).getCustomerId())) {
                return ResponseEntity.status(403).build();
            }
        }
        Payment payment = toPaymentEntity(paymentDto);
        payment.setInvoice(invoice);
        Payment created = paymentService.createPayment(payment);
        return ResponseEntity.ok(toPaymentDto(created));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto>> getInvoicePayments(@PathVariable Long id) {
        List<Payment> payments = paymentService.getPaymentsByInvoiceId(id);
        List<PaymentDto> dtos = payments.stream().map(this::toPaymentDto).toList();
        return ResponseEntity.ok(dtos);
    }

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

    private PaymentDto toPaymentDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceId(payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
        dto.setStatus(payment.getPaymentMethod());
        return dto;
    }

    private Payment toPaymentEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setAmount(dto.getAmount());
        // Set paymentDate and invoice as needed
        payment.setPaymentMethod(dto.getStatus());
        return payment;
    }
}
