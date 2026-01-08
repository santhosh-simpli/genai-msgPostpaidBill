package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        // Return invoices ordered by invoice ID descending (most recent first)
        return invoiceRepository.findAllByOrderByInvoiceIdDesc();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public List<Invoice> getInvoicesByCustomerId(Long customerId) {
        return invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(customerId);
    }

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Invoice invoice = getInvoiceById(id);
        invoice.setBillingPeriodStart(invoiceDetails.getBillingPeriodStart());
        invoice.setBillingPeriodEnd(invoiceDetails.getBillingPeriodEnd());
        invoice.setTotalAmount(invoiceDetails.getTotalAmount());
        invoice.setStatus(invoiceDetails.getStatus());
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}
