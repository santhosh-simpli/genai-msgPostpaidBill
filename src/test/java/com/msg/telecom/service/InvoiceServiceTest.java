package com.msg.telecom.service;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {
    @Mock
    private InvoiceRepository invoiceRepository;
    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice testInvoice;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("Test Customer");

        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setCustomer(testCustomer);
        testInvoice.setBillingPeriodStart(LocalDate.now().minusMonths(1));
        testInvoice.setBillingPeriodEnd(LocalDate.now());
        testInvoice.setTotalAmount(150.0);
        testInvoice.setStatus("PENDING");
    }

    @Test
    void getAllInvoices_ReturnsList() {
        when(invoiceRepository.findAllByOrderByInvoiceIdDesc()).thenReturn(List.of(testInvoice));
        List<Invoice> invoices = invoiceService.getAllInvoices();
        assertEquals(1, invoices.size());
        assertEquals(150.0, invoices.get(0).getTotalAmount());
        verify(invoiceRepository, times(1)).findAllByOrderByInvoiceIdDesc();
    }

    @Test
    void getAllInvoices_ReturnsEmptyList() {
        when(invoiceRepository.findAllByOrderByInvoiceIdDesc()).thenReturn(Collections.emptyList());
        List<Invoice> invoices = invoiceService.getAllInvoices();
        assertTrue(invoices.isEmpty());
        verify(invoiceRepository, times(1)).findAllByOrderByInvoiceIdDesc();
    }

    @Test
    void getInvoiceById_Found() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
        Invoice result = invoiceService.getInvoiceById(1L);
        assertEquals(1L, result.getInvoiceId());
        assertEquals("PENDING", result.getStatus());
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    void getInvoiceById_NotFound() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(999L));
        assertTrue(ex.getMessage().contains("Invoice not found"));
        verify(invoiceRepository, times(1)).findById(999L);
    }

    @Test
    void getInvoicesByCustomerId_ReturnsList() {
        when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(1L)).thenReturn(List.of(testInvoice));
        List<Invoice> invoices = invoiceService.getInvoicesByCustomerId(1L);
        assertEquals(1, invoices.size());
        verify(invoiceRepository, times(1)).findByCustomer_CustomerIdOrderByInvoiceIdDesc(1L);
    }

    @Test
    void getInvoicesByCustomerId_ReturnsEmptyList() {
        when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(999L)).thenReturn(Collections.emptyList());
        List<Invoice> invoices = invoiceService.getInvoicesByCustomerId(999L);
        assertTrue(invoices.isEmpty());
        verify(invoiceRepository, times(1)).findByCustomer_CustomerIdOrderByInvoiceIdDesc(999L);
    }

    @Test
    void createInvoice_Success() {
        Invoice newInvoice = new Invoice();
        newInvoice.setCustomer(testCustomer);
        newInvoice.setTotalAmount(200.0);
        newInvoice.setStatus("PENDING");

        when(invoiceRepository.save(any(Invoice.class))).thenReturn(newInvoice);
        Invoice created = invoiceService.createInvoice(newInvoice);

        assertNotNull(created);
        assertEquals(200.0, created.getTotalAmount());
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void updateInvoice_Success() {
        Invoice updateDetails = new Invoice();
        updateDetails.setBillingPeriodStart(LocalDate.now().minusMonths(2));
        updateDetails.setBillingPeriodEnd(LocalDate.now().minusMonths(1));
        updateDetails.setTotalAmount(300.0);
        updateDetails.setStatus("PAID");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        Invoice updated = invoiceService.updateInvoice(1L, updateDetails);
        assertNotNull(updated);
        verify(invoiceRepository, times(1)).findById(1L);
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void updateInvoice_NotFound() {
        Invoice updateDetails = new Invoice();
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.updateInvoice(999L, updateDetails));
        verify(invoiceRepository, times(1)).findById(999L);
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void deleteInvoice_Success() {
        doNothing().when(invoiceRepository).deleteById(1L);

        assertDoesNotThrow(() -> invoiceService.deleteInvoice(1L));
        verify(invoiceRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteInvoice_NonExistent() {
        doNothing().when(invoiceRepository).deleteById(999L);

        assertDoesNotThrow(() -> invoiceService.deleteInvoice(999L));
        verify(invoiceRepository, times(1)).deleteById(999L);
    }
}
