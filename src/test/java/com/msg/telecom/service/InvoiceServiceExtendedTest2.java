package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Customer;
import com.msg.telecom.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceExtendedTest2 {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Customer testCustomer;
    private Invoice testInvoice1;
    private Invoice testInvoice2;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("Test Customer");

        testInvoice1 = new Invoice();
        testInvoice1.setInvoiceId(1L);
        testInvoice1.setCustomer(testCustomer);
        testInvoice1.setTotalAmount(100.0);
        testInvoice1.setStatus("PENDING");
        testInvoice1.setBillingPeriodStart(LocalDate.now().minusMonths(1));
        testInvoice1.setBillingPeriodEnd(LocalDate.now());

        testInvoice2 = new Invoice();
        testInvoice2.setInvoiceId(2L);
        testInvoice2.setCustomer(testCustomer);
        testInvoice2.setTotalAmount(200.0);
        testInvoice2.setStatus("PAID");
        testInvoice2.setBillingPeriodStart(LocalDate.now().minusMonths(2));
        testInvoice2.setBillingPeriodEnd(LocalDate.now().minusMonths(1));
    }

    @Test
    void getAllInvoices_ReturnsOrderedList() {
        when(invoiceRepository.findAllByOrderByInvoiceIdDesc())
                .thenReturn(Arrays.asList(testInvoice2, testInvoice1));

        List<Invoice> result = invoiceService.getAllInvoices();

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getInvoiceId());
        verify(invoiceRepository).findAllByOrderByInvoiceIdDesc();
    }

    @Test
    void getAllInvoices_EmptyList() {
        when(invoiceRepository.findAllByOrderByInvoiceIdDesc())
                .thenReturn(Collections.emptyList());

        List<Invoice> result = invoiceService.getAllInvoices();

        assertTrue(result.isEmpty());
    }

    @Test
    void getInvoiceById_Found() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice1));

        Invoice result = invoiceService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getInvoiceId());
        assertEquals(100.0, result.getTotalAmount());
    }

    @Test
    void getInvoiceById_NotFound_ThrowsException() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(999L));
    }

    @Test
    void getInvoicesByCustomerId_ReturnsOrderedList() {
        when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(1L))
                .thenReturn(Arrays.asList(testInvoice2, testInvoice1));

        List<Invoice> result = invoiceService.getInvoicesByCustomerId(1L);

        assertEquals(2, result.size());
        verify(invoiceRepository).findByCustomer_CustomerIdOrderByInvoiceIdDesc(1L);
    }

    @Test
    void getInvoicesByCustomerId_EmptyForNonExistent() {
        when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(999L))
                .thenReturn(Collections.emptyList());

        List<Invoice> result = invoiceService.getInvoicesByCustomerId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createInvoice_Success() {
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice1);

        Invoice result = invoiceService.createInvoice(testInvoice1);

        assertNotNull(result);
        assertEquals(1L, result.getInvoiceId());
        verify(invoiceRepository).save(testInvoice1);
    }

    @Test
    void createInvoice_WithNullAmount() {
        testInvoice1.setTotalAmount(null);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice1);

        Invoice result = invoiceService.createInvoice(testInvoice1);

        assertNotNull(result);
        verify(invoiceRepository).save(testInvoice1);
    }

    @Test
    void updateInvoice_Success() {
        Invoice updateDetails = new Invoice();
        updateDetails.setBillingPeriodStart(LocalDate.now());
        updateDetails.setBillingPeriodEnd(LocalDate.now().plusDays(30));
        updateDetails.setTotalAmount(250.0);
        updateDetails.setStatus("PAID");

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice1));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice1);

        Invoice result = invoiceService.updateInvoice(1L, updateDetails);

        assertNotNull(result);
        verify(invoiceRepository).findById(1L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void updateInvoice_NotFound_ThrowsException() {
        when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.updateInvoice(999L, testInvoice1));
    }

    @Test
    void deleteInvoice_Success() {
        doNothing().when(invoiceRepository).deleteById(1L);

        assertDoesNotThrow(() -> invoiceService.deleteInvoice(1L));
        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void createInvoice_WithZeroAmount() {
        testInvoice1.setTotalAmount(0.0);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice1);

        Invoice result = invoiceService.createInvoice(testInvoice1);

        assertEquals(0.0, result.getTotalAmount());
    }

    @Test
    void createInvoice_WithDifferentStatuses() {
        String[] statuses = { "PENDING", "PAID", "OVERDUE", "CANCELLED" };

        for (String status : statuses) {
            testInvoice1.setStatus(status);
            when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice1);

            Invoice result = invoiceService.createInvoice(testInvoice1);

            assertEquals(status, result.getStatus());
        }
    }
}
