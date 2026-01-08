package com.msg.telecom.controller;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Payment;
import com.msg.telecom.model.User;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.PaymentService;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final UserService userService;
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<PaymentDto>> getAllPayments(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Payment> payments;
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            payments = paymentService.getAllPayments();
        } else {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            payments = new ArrayList<>();
            if (!customers.isEmpty()) {
                invoiceService.getInvoicesByCustomerId(customers.get(0).getCustomerId()).forEach(invoice -> {
                    payments.addAll(paymentService.getPaymentsByInvoiceId(invoice.getInvoiceId()));
                });
            }
        }
        List<PaymentDto> dtos = payments.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto, Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        // Customers can only create payments for their own invoices
        if (currentUser.getRole().name().equals("CUSTOMER")) {
            List<Customer> customers = customerService.getCustomersByUserId(currentUser.getUserId());
            if (customers.isEmpty() || 
                !paymentDto.getInvoiceId().equals(customers.get(0).getCustomerId())) {
                return ResponseEntity.status(403).build();
            }
        }
        Payment payment = toEntity(paymentDto);
        Payment created = paymentService.createPayment(payment);
        return ResponseEntity.ok(toDto(created));
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceId(payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
        dto.setStatus(payment.getPaymentMethod());
        return dto;
    }

    private Payment toEntity(PaymentDto dto) {
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getStatus());
        // Set paymentDate and invoice as needed
        return payment;
    }
}
