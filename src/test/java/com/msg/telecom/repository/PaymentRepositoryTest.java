package com.msg.telecom.repository;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void findByInvoice_InvoiceId_ReturnsPayments() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(200.0);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(200.0);
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByInvoice_InvoiceId(invoice.getInvoiceId());
        assertFalse(payments.isEmpty());
        assertEquals(200.0, payments.get(0).getAmount());
    }

    @Test
    void findAllByOrderByPaymentDateDesc_ReturnsPayments() {
        Payment payment1 = new Payment();
        payment1.setAmount(100.0);
        payment1.setPaymentDate(java.time.LocalDate.now().minusDays(1));

        Payment payment2 = new Payment();
        payment2.setAmount(200.0);
        payment2.setPaymentDate(java.time.LocalDate.now());

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findAllByOrderByPaymentDateDesc();
        assertEquals(2, payments.size());
        assertEquals(200.0, payments.get(0).getAmount());
    }
}