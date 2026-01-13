package com.msg.telecom.controller;

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

class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private UserService userService;
    @Mock
    private CustomerService customerService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private PaymentController paymentController;

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
        testInvoice.setCustomer(testCustomer);

        testPayment = new Payment();
        testPayment.setPaymentId(1L);
        testPayment.setAmount(100.0);
        testPayment.setPaymentMethod("CARD");
        testPayment.setPaymentDate(LocalDate.now());
        testPayment.setInvoice(testInvoice);
    }

    @Test
    void getAllPayments_AdminRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(100.0, response.getBody().get(0).getAmount());
        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void getAllPayments_AdminRole_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(paymentService.getAllPayments()).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllPayments_OperatorRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("operator");
        when(userService.getUserByUsername("operator")).thenReturn(operatorUser);
        when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void getAllPayments_CustomerRole_ReturnsOwnPayments() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(invoiceService.getInvoicesByCustomerId(1L)).thenReturn(Collections.singletonList(testInvoice));
        when(paymentService.getPaymentsByInvoiceId(1L)).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(paymentService, never()).getAllPayments();
    }

    @Test
    void getAllPayments_CustomerWithNoCustomerRecord_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllPayments_CustomerWithNoInvoices_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(invoiceService.getInvoicesByCustomerId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllPayments_MultiplePayments_ReturnsAll() {
        Payment payment2 = new Payment();
        payment2.setPaymentId(2L);
        payment2.setAmount(200.0);
        payment2.setPaymentMethod("CASH");
        payment2.setPaymentDate(LocalDate.now());

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(paymentService.getAllPayments()).thenReturn(Arrays.asList(testPayment, payment2));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void createPayment_AdminRole_ReturnsCreatedDto() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(200.0);
        dto.setStatus("CASH");
        dto.setInvoiceId(1L);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(testPayment);

        ResponseEntity<PaymentDto> response = paymentController.createPayment(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createPayment_CustomerRole_OwnInvoice_ReturnsCreatedDto() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(100.0);
        dto.setStatus("CARD");
        dto.setInvoiceId(1L);

        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));
        when(paymentService.createPayment(any(Payment.class))).thenReturn(testPayment);

        ResponseEntity<PaymentDto> response = paymentController.createPayment(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createPayment_CustomerRole_NotOwnInvoice_ReturnsForbidden() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(100.0);
        dto.setInvoiceId(999L);

        Customer otherCustomer = new Customer();
        otherCustomer.setCustomerId(999L);
        Invoice otherInvoice = new Invoice();
        otherInvoice.setInvoiceId(999L);
        otherInvoice.setCustomer(otherCustomer);

        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(invoiceService.getInvoiceById(999L)).thenReturn(otherInvoice);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.singletonList(testCustomer));

        ResponseEntity<PaymentDto> response = paymentController.createPayment(dto, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createPayment_CustomerWithNoCustomerRecord_ReturnsForbidden() {
        PaymentDto dto = new PaymentDto();
        dto.setAmount(100.0);
        dto.setInvoiceId(1L);

        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<PaymentDto> response = paymentController.createPayment(dto, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getAllPayments_PaymentWithNullInvoice_HandlesProperly() {
        testPayment.setInvoice(null);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getInvoiceId());
    }

    @Test
    void getAllPayments_PaymentWithNullPaymentDate_HandlesProperly() {
        testPayment.setPaymentDate(null);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(testPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getPaymentDate());
    }

    @Test
    void createPayment_WithAllFields_ReturnsDto() {
        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(5L);
        dto.setAmount(500.0);
        dto.setStatus("TRANSFER");
        dto.setInvoiceId(1L);

        Payment createdPayment = new Payment();
        createdPayment.setPaymentId(5L);
        createdPayment.setAmount(500.0);
        createdPayment.setPaymentMethod("TRANSFER");
        createdPayment.setInvoice(testInvoice);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(invoiceService.getInvoiceById(1L)).thenReturn(testInvoice);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(createdPayment);

        ResponseEntity<PaymentDto> response = paymentController.createPayment(dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(500.0, response.getBody().getAmount());
    }
}
