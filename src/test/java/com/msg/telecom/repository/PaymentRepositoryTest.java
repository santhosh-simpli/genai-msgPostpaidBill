package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@msgtel.com");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = entityManager.persistAndFlush(testUser);

        Customer testCustomer = new Customer();
        testCustomer.setFullName("John Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setEmail("john@test.com");
        testCustomer.setUser(testUser);
        testCustomer = entityManager.persistAndFlush(testCustomer);

        testInvoice = new Invoice();
        testInvoice.setCustomer(testCustomer);
        testInvoice.setStatus("PENDING");
        testInvoice.setTotalAmount(200.0);
        testInvoice.setBillingPeriodStart(LocalDate.now().minusMonths(1));
        testInvoice.setBillingPeriodEnd(LocalDate.now());
        testInvoice = entityManager.persistAndFlush(testInvoice);
    }

    @Test
    void findByInvoice_InvoiceId_ReturnsPayments() {
        Payment payment = new Payment();
        payment.setInvoice(testInvoice);
        payment.setAmount(200.0);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("CREDIT_CARD");
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByInvoice_InvoiceId(testInvoice.getInvoiceId());
        assertFalse(payments.isEmpty());
        assertEquals(200.0, payments.get(0).getAmount());
    }

    @Test
    void findAllByOrderByPaymentDateDesc_ReturnsPayments() {
        Payment payment1 = new Payment();
        payment1.setInvoice(testInvoice);
        payment1.setAmount(100.0);
        payment1.setPaymentDate(LocalDate.now().minusDays(1));
        payment1.setPaymentMethod("CREDIT_CARD");

        Payment payment2 = new Payment();
        payment2.setInvoice(testInvoice);
        payment2.setAmount(200.0);
        payment2.setPaymentDate(LocalDate.now());
        payment2.setPaymentMethod("DEBIT_CARD");

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        List<Payment> payments = paymentRepository.findAllByOrderByPaymentDateDesc();
        assertEquals(2, payments.size());
        assertEquals(200.0, payments.get(0).getAmount());
    }
}
