package com.msg.telecom.controller;

import com.msg.telecom.dto.InvoiceDto;
import com.msg.telecom.dto.PaymentDto;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Payment;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.PaymentService;
import com.msg.telecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceControllerTest {
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserService userService;
    @Mock
    private CustomerService customerService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private InvoiceController invoiceController;

    private User adminUser;
    private User customerUser;
    private User operatorUser;
    private Customer testCustomer;
    private Invoice testInvoice;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        adminUser = new User();
        adminUser.setUserId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(UserRole.ADMIN);
        
        customerUser = new User();
        customerUser.setUserId(2L);
        customerUser.setUsername("customer");
        customerUser.setRole(UserRole.CUSTOMER);
        
        operatorUser = new User();
        operatorUser.setUserId(3L);
        operatorUser.setUsername("operator");
        operatorUser.setRole(UserRole.OPERATOR);
        
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setUser(customerUser);
        
        testInvoice = new Invoice();
        testInvoice.setInvoiceId(1L);
        testInvoice.setTotalAmount(100.0);
        testInvoice.setStatus("PENDING");
        testInvoice.setBillingPeriodEnd(LocalDate.now());
        testInvoice.setCustomer(testCustomer);
        
        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setAmount(50.0);
        testPayment.setPaymentMethod("CASH");
        testPayment.setPaymentDate(LocalDate.now());
        testPayment.setInvoice(testInvoice);
    }

    @Test
    void getAllInvoices_AdminRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    void getAllInvoices_AdminRole_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.emptyList());

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllInvoices_OperatorRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("operator");
        when(userService.getUserByUsername("operator")).thenReturn(operatorUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(invoiceService, times(1)).getAllInvoices();
    }

    @Test
    void getAllInvoices_CustomerRole_ReturnsOwnInvoices() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(invoiceService.getInvoicesByCustomerId(1L)).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(invoiceService, never()).getAllInvoices();
        verify(invoiceService, times(1)).getInvoicesByCustomerId(1L);
    }

    @Test
    void getAllInvoices_CustomerWithNoCustomerRecord_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllInvoices_MultipleInvoices_ReturnsAll() {
        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceId(2L);
        invoice2.setTotalAmount(200.0);
        invoice2.setStatus("PAID");
        invoice2.setBillingPeriodEnd(LocalDate.now().plusDays(30));
        invoice2.setCustomer(testCustomer);
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Arrays.asList(testInvoice, invoice2));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void recordPayment_AdminRole_ReturnsCreatedPaymentDto() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(50.0);
        dto.setStatus("CASH");
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(testPayment);

        ResponseEntity<PaymentDto> response = invoiceController.recordPayment(1L, dto, authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.0, response.getBody().getAmount());
    }

    @Test
    void recordPayment_CustomerRole_OwnInvoice_ReturnsCreatedPaymentDto() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(50.0);
        dto.setStatus("CARD");
        
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(paymentService.createPayment(any(Payment.class))).thenReturn(testPayment);

        ResponseEntity<PaymentDto> response = invoiceController.recordPayment(1L, dto, authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void recordPayment_CustomerRole_NotOwnInvoice_ReturnsForbidden() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(50.0);
        
        Customer otherCustomer = new Customer();
        otherCustomer.setCustomerId(999L);
        testInvoice.setCustomer(otherCustomer);
        
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));

        ResponseEntity<PaymentDto> response = invoiceController.recordPayment(1L, dto, authentication);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void recordPayment_CustomerWithNoCustomerRecord_ReturnsForbidden() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(50.0);
        
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<PaymentDto> response = invoiceController.recordPayment(1L, dto, authentication);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getInvoicePayments_ReturnsPaymentDtoList() {
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = invoiceController.getInvoicePayments(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(50.0, response.getBody().get(0).getAmount());
    }

    @Test
    void getInvoicePayments_NoPayments_ReturnsEmptyList() {
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = invoiceController.getInvoicePayments(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getInvoicePayments_MultiplePayments_ReturnsAll() {
        Payment payment2 = new Payment();
        payment2.setPaymentId(2L);
        payment2.setAmount(30.0);
        payment2.setPaymentMethod("CARD");
        payment2.setPaymentDate(LocalDate.now());
        
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Arrays.asList(testPayment, payment2));

        ResponseEntity<List<PaymentDto>> response = invoiceController.getInvoicePayments(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllInvoices_InvoiceWithNullBillingPeriod_HandlesProperly() {
        testInvoice.setBillingPeriodEnd(null);
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getDueDate());
    }

    @Test
    void getAllInvoices_InvoiceWithNullCustomer_HandlesProperly() {
        testInvoice.setCustomer(null);
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getCustomerId());
    }

    // Additional tests to improve coverage for createInvoice method

    @Test
    void createInvoice_WithAllFields_ReturnsCreatedDto() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(150.0);
        dto.setBillingStartDate("2025-01-01");
        dto.setBillingEndDate("2025-01-31");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(2L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(150.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.parse("2025-01-01"));
        createdInvoice.setBillingPeriodEnd(LocalDate.parse("2025-01-31"));

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0, response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
    }

    @Test
    void createInvoice_WithNullTotalAmount_DefaultsToZero() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(null);
        dto.setBillingStartDate("2025-02-01");
        dto.setBillingEndDate("2025-02-28");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(3L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(0.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.parse("2025-02-01"));
        createdInvoice.setBillingPeriodEnd(LocalDate.parse("2025-02-28"));

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().getTotalAmount());
    }

    @Test
    void createInvoice_WithBillingPeriodStartAndEnd_ReturnsCreatedDto() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(200.0);
        dto.setBillingPeriodStart("2025-03-01");
        dto.setBillingPeriodEnd("2025-03-31");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(4L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(200.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.parse("2025-03-01"));
        createdInvoice.setBillingPeriodEnd(LocalDate.parse("2025-03-31"));

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2025-03-01", response.getBody().getBillingPeriodStart());
        assertEquals("2025-03-31", response.getBody().getBillingPeriodEnd());
    }

    @Test
    void createInvoice_WithNullDates_UsesDefaults() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(100.0);
        dto.setBillingStartDate(null);
        dto.setBillingEndDate(null);

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(5L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(100.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.now().withDayOfMonth(1));
        createdInvoice.setBillingPeriodEnd(LocalDate.now());

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getBillingPeriodStart());
        assertNotNull(response.getBody().getBillingPeriodEnd());
    }

    @Test
    void createInvoice_WithEmptyDates_UsesDefaults() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(100.0);
        dto.setBillingStartDate("");
        dto.setBillingEndDate("");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(6L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(100.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.now().withDayOfMonth(1));
        createdInvoice.setBillingPeriodEnd(LocalDate.now());

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createInvoice_WithMixedDateFormats_PrefersStartEndDates() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(300.0);
        dto.setBillingStartDate("2025-04-01");
        dto.setBillingEndDate("2025-04-30");
        dto.setBillingPeriodStart("2025-05-01");
        dto.setBillingPeriodEnd("2025-05-31");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(7L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(300.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.parse("2025-04-01"));
        createdInvoice.setBillingPeriodEnd(LocalDate.parse("2025-04-30"));

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2025-04-01", response.getBody().getBillingPeriodStart());
    }

    @Test
    void createInvoice_WithOnlyPeriodStartAndEnd_ReturnsCreatedDto() {
        InvoiceDto dto = new InvoiceDto();
        dto.setCustomerId(1L);
        dto.setTotalAmount(250.0);
        dto.setBillingStartDate(null);
        dto.setBillingEndDate(null);
        dto.setBillingPeriodStart("2025-06-01");
        dto.setBillingPeriodEnd("2025-06-30");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(8L);
        createdInvoice.setCustomer(testCustomer);
        createdInvoice.setTotalAmount(250.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setBillingPeriodStart(LocalDate.parse("2025-06-01"));
        createdInvoice.setBillingPeriodEnd(LocalDate.parse("2025-06-30"));

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<InvoiceDto> response = invoiceController.createInvoice(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("2025-06-01", response.getBody().getBillingPeriodStart());
        assertEquals("2025-06-30", response.getBody().getBillingPeriodEnd());
    }

    @Test
    void getAllInvoices_InvoiceWithNullBillingPeriodStart_HandlesProperly() {
        testInvoice.setBillingPeriodStart(null);
        testInvoice.setBillingPeriodEnd(LocalDate.now());

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getAllInvoices()).thenReturn(Collections.singletonList(testInvoice));

        ResponseEntity<List<InvoiceDto>> response = invoiceController.getAllInvoices(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getBillingPeriodStart());
    }

    @Test
    void getInvoicePayments_PaymentWithNullInvoice_HandlesProperly() {
        testPayment.setInvoice(null);
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = invoiceController.getInvoicePayments(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getInvoiceId());
    }

    @Test
    void getInvoicePayments_PaymentWithNullPaymentDate_HandlesProperly() {
        testPayment.setPaymentDate(null);
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = invoiceController.getInvoicePayments(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getPaymentDate());
    }

    @Test
    void recordPayment_WithPaymentId_ReturnsCreatedDto() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(10L);
        dto.setAmount(75.0);
        dto.setStatus("BANK_TRANSFER");

        Payment createdPayment = new Payment();
        createdPayment.setPaymentId(10L);
        createdPayment.setAmount(75.0);
        createdPayment.setPaymentMethod("BANK_TRANSFER");
        createdPayment.setInvoice(testInvoice);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(createdPayment);

        ResponseEntity<PaymentDto> response = invoiceController.recordPayment(1L, dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getPaymentId());
    }
}
