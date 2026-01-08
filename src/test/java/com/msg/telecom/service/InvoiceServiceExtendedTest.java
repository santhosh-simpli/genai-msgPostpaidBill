package com.msg.telecom.service;

import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.User;
import com.msg.telecom.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("InvoiceService Extended Tests")
class InvoiceServiceExtendedTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice testInvoice;
    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("John Doe");
        testCustomer.setUser(testUser);

        testInvoice = Invoice.builder()
                .invoiceId(1L)
                .customer(testCustomer)
                .billingPeriodStart(LocalDate.of(2026, 1, 1))
                .billingPeriodEnd(LocalDate.of(2026, 1, 31))
                .totalAmount(150.00)
                .status("PENDING")
                .build();
    }

    @Nested
    @DisplayName("getAllInvoices Extended Tests")
    class GetAllInvoicesExtendedTests {

        @Test
        @DisplayName("Should return multiple invoices")
        void getAllInvoices_MultipleInvoices() {
            Invoice invoice2 = Invoice.builder()
                    .invoiceId(2L)
                    .customer(testCustomer)
                    .totalAmount(200.00)
                    .status("PAID")
                    .build();
            Invoice invoice3 = Invoice.builder()
                    .invoiceId(3L)
                    .customer(testCustomer)
                    .totalAmount(75.50)
                    .status("OVERDUE")
                    .build();

            when(invoiceRepository.findAllByOrderByInvoiceIdDesc())
                    .thenReturn(Arrays.asList(testInvoice, invoice2, invoice3));

            List<Invoice> result = invoiceService.getAllInvoices();

            assertEquals(3, result.size());
            assertEquals(150.00, result.get(0).getTotalAmount());
            assertEquals(200.00, result.get(1).getTotalAmount());
            assertEquals(75.50, result.get(2).getTotalAmount());
        }

        @Test
        @DisplayName("Should verify repository is called once")
        void getAllInvoices_VerifyRepositoryCalled() {
            when(invoiceRepository.findAllByOrderByInvoiceIdDesc()).thenReturn(Collections.emptyList());

            invoiceService.getAllInvoices();

            verify(invoiceRepository, times(1)).findAllByOrderByInvoiceIdDesc();
        }
    }

    @Nested
    @DisplayName("getInvoiceById Extended Tests")
    class GetInvoiceByIdExtendedTests {

        @Test
        @DisplayName("Should return invoice with all fields populated")
        void getInvoiceById_AllFieldsPresent() {
            when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));

            Invoice result = invoiceService.getInvoiceById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getInvoiceId());
            assertNotNull(result.getCustomer());
            assertEquals("John Doe", result.getCustomer().getFullName());
            assertEquals(LocalDate.of(2026, 1, 1), result.getBillingPeriodStart());
            assertEquals(LocalDate.of(2026, 1, 31), result.getBillingPeriodEnd());
            assertEquals(150.00, result.getTotalAmount());
            assertEquals("PENDING", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception with correct message format")
        void getInvoiceById_NotFound_MessageFormat() {
            when(invoiceRepository.findById(123L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> invoiceService.getInvoiceById(123L));

            assertTrue(exception.getMessage().contains("Invoice not found"));
            assertTrue(exception.getMessage().contains("123"));
        }

        @Test
        @DisplayName("Should handle zero ID")
        void getInvoiceById_ZeroId() {
            when(invoiceRepository.findById(0L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(0L));
        }

        @Test
        @DisplayName("Should handle negative ID")
        void getInvoiceById_NegativeId() {
            when(invoiceRepository.findById(-1L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(-1L));
        }
    }

    @Nested
    @DisplayName("getInvoicesByCustomerId Extended Tests")
    class GetInvoicesByCustomerIdExtendedTests {

        @Test
        @DisplayName("Should return invoices for customer with different statuses")
        void getInvoicesByCustomerId_MixedStatuses() {
            Invoice paidInvoice = Invoice.builder()
                    .invoiceId(2L)
                    .customer(testCustomer)
                    .status("PAID")
                    .build();
            Invoice overdueInvoice = Invoice.builder()
                    .invoiceId(3L)
                    .customer(testCustomer)
                    .status("OVERDUE")
                    .build();

            when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(1L))
                    .thenReturn(Arrays.asList(testInvoice, paidInvoice, overdueInvoice));

            List<Invoice> result = invoiceService.getInvoicesByCustomerId(1L);

            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(i -> "PENDING".equals(i.getStatus())));
            assertTrue(result.stream().anyMatch(i -> "PAID".equals(i.getStatus())));
            assertTrue(result.stream().anyMatch(i -> "OVERDUE".equals(i.getStatus())));
        }

        @Test
        @DisplayName("Should return empty list for customer with no invoices")
        void getInvoicesByCustomerId_NoInvoices() {
            when(invoiceRepository.findByCustomer_CustomerIdOrderByInvoiceIdDesc(999L))
                    .thenReturn(Collections.emptyList());

            List<Invoice> result = invoiceService.getInvoicesByCustomerId(999L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("createInvoice Extended Tests")
    class CreateInvoiceExtendedTests {

        @Test
        @DisplayName("Should create invoice with all fields")
        void createInvoice_AllFields() {
            Invoice newInvoice = Invoice.builder()
                    .customer(testCustomer)
                    .billingPeriodStart(LocalDate.of(2026, 2, 1))
                    .billingPeriodEnd(LocalDate.of(2026, 2, 28))
                    .totalAmount(299.99)
                    .status("PENDING")
                    .build();

            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
                Invoice saved = invocation.getArgument(0);
                saved.setInvoiceId(100L);
                return saved;
            });

            Invoice result = invoiceService.createInvoice(newInvoice);

            assertNotNull(result);
            assertEquals(100L, result.getInvoiceId());
            assertEquals(299.99, result.getTotalAmount());
        }

        @Test
        @DisplayName("Should create invoice with zero amount")
        void createInvoice_ZeroAmount() {
            Invoice newInvoice = Invoice.builder()
                    .customer(testCustomer)
                    .totalAmount(0.0)
                    .status("PENDING")
                    .build();

            when(invoiceRepository.save(any(Invoice.class))).thenReturn(newInvoice);

            Invoice result = invoiceService.createInvoice(newInvoice);

            assertEquals(0.0, result.getTotalAmount());
        }

        @Test
        @DisplayName("Should create invoice with large amount")
        void createInvoice_LargeAmount() {
            Invoice newInvoice = Invoice.builder()
                    .customer(testCustomer)
                    .totalAmount(999999.99)
                    .status("PENDING")
                    .build();

            when(invoiceRepository.save(any(Invoice.class))).thenReturn(newInvoice);

            Invoice result = invoiceService.createInvoice(newInvoice);

            assertEquals(999999.99, result.getTotalAmount());
        }
    }

    @Nested
    @DisplayName("updateInvoice Extended Tests")
    class UpdateInvoiceExtendedTests {

        @Test
        @DisplayName("Should update all invoice fields")
        void updateInvoice_AllFields() {
            Invoice updateDetails = Invoice.builder()
                    .billingPeriodStart(LocalDate.of(2026, 3, 1))
                    .billingPeriodEnd(LocalDate.of(2026, 3, 31))
                    .totalAmount(500.00)
                    .status("PAID")
                    .build();

            when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Invoice result = invoiceService.updateInvoice(1L, updateDetails);

            assertEquals(LocalDate.of(2026, 3, 1), result.getBillingPeriodStart());
            assertEquals(LocalDate.of(2026, 3, 31), result.getBillingPeriodEnd());
            assertEquals(500.00, result.getTotalAmount());
            assertEquals("PAID", result.getStatus());
        }

        @Test
        @DisplayName("Should update status to OVERDUE")
        void updateInvoice_StatusOverdue() {
            Invoice updateDetails = Invoice.builder()
                    .billingPeriodStart(testInvoice.getBillingPeriodStart())
                    .billingPeriodEnd(testInvoice.getBillingPeriodEnd())
                    .totalAmount(testInvoice.getTotalAmount())
                    .status("OVERDUE")
                    .build();

            when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Invoice result = invoiceService.updateInvoice(1L, updateDetails);

            assertEquals("OVERDUE", result.getStatus());
        }

        @Test
        @DisplayName("Should update status to CANCELLED")
        void updateInvoice_StatusCancelled() {
            Invoice updateDetails = Invoice.builder()
                    .billingPeriodStart(testInvoice.getBillingPeriodStart())
                    .billingPeriodEnd(testInvoice.getBillingPeriodEnd())
                    .totalAmount(testInvoice.getTotalAmount())
                    .status("CANCELLED")
                    .build();

            when(invoiceRepository.findById(1L)).thenReturn(Optional.of(testInvoice));
            when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Invoice result = invoiceService.updateInvoice(1L, updateDetails);

            assertEquals("CANCELLED", result.getStatus());
        }

        @Test
        @DisplayName("Should throw exception for non-existent invoice")
        void updateInvoice_NotFound() {
            Invoice updateDetails = Invoice.builder().status("PAID").build();
            when(invoiceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> invoiceService.updateInvoice(999L, updateDetails));

            verify(invoiceRepository, never()).save(any(Invoice.class));
        }
    }

    @Nested
    @DisplayName("deleteInvoice Extended Tests")
    class DeleteInvoiceExtendedTests {

        @Test
        @DisplayName("Should delete existing invoice")
        void deleteInvoice_Existing() {
            doNothing().when(invoiceRepository).deleteById(1L);

            assertDoesNotThrow(() -> invoiceService.deleteInvoice(1L));

            verify(invoiceRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should handle deleting non-existent invoice")
        void deleteInvoice_NonExistent() {
            doNothing().when(invoiceRepository).deleteById(9999L);

            assertDoesNotThrow(() -> invoiceService.deleteInvoice(9999L));
        }

        @Test
        @DisplayName("Should call repository with correct ID")
        void deleteInvoice_CorrectId() {
            doNothing().when(invoiceRepository).deleteById(42L);

            invoiceService.deleteInvoice(42L);

            verify(invoiceRepository).deleteById(42L);
        }
    }
}
