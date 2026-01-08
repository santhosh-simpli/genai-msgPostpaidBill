package com.msg.telecom.repository;

import com.msg.telecom.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomer_CustomerId(Long customerId);

    List<Invoice> findByStatus(String status);

    List<Invoice> findAllByOrderByInvoiceIdDesc();

    List<Invoice> findByCustomer_CustomerIdOrderByInvoiceIdDesc(Long customerId);
}
