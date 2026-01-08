package com.msg.telecom.repository;

import com.msg.telecom.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice_InvoiceId(Long invoiceId);

    List<Payment> findAllByOrderByPaymentDateDesc();

    List<Payment> findAllByOrderByPaymentIdDesc();
}
