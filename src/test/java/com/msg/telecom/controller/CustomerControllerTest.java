package com.msg.telecom.controller;

import com.msg.telecom.dto.CustomerDto;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.ServiceService;
import com.msg.telecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {
    @Mock
    private CustomerService customerService;
    @Mock
    private ServiceService serviceService;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private CustomerController customerController;

    private User adminUser;
    private User customerUser;
    private Customer testCustomer;

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
        
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("Test Customer");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setUser(customerUser);
    }

    @Test
    void getAllCustomers_AdminRole_ReturnsAll() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(List.of(testCustomer));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Customer", response.getBody().get(0).getName());
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getAllCustomers_AdminRole_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllCustomers_CustomerRole_ReturnsOwn() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(List.of(testCustomer));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(customerService, times(1)).getCustomersByUserId(2L);
        verify(customerService, never()).getAllCustomers();
    }

    @Test
    void getAllCustomers_CustomerRole_ReturnsEmptyList() {
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomersByUserId(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getCustomerById_AsAdmin_ReturnsDto() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L, authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Customer", response.getBody().getName());
        assertEquals("1234567890", response.getBody().getPhoneNumber());
    }

    @Test
    void getCustomerById_AsCustomer_OwnCustomer_ReturnsDto() {
        testCustomer.setUser(customerUser);
        when(authentication.getName()).thenReturn("customer");
        when(userService.getUserByUsername("customer")).thenReturn(customerUser);
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L, authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createCustomer_Success_ReturnsCreatedDto() {
        CustomerDto dto = new CustomerDto();
        dto.setName("New Customer");
        dto.setPhoneNumber("9876543210");
        
        Customer createdCustomer = new Customer();
        createdCustomer.setCustomerId(2L);
        createdCustomer.setFullName("New Customer");
        createdCustomer.setPhoneNumber("9876543210");
        
        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

        ResponseEntity<CustomerDto> response = customerController.createCustomer(dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Customer", response.getBody().getName());
        assertEquals("9876543210", response.getBody().getPhoneNumber());
    }

    @Test
    void createCustomer_WithAllFields_ReturnsDto() {
        CustomerDto dto = new CustomerDto();
        dto.setName("Full Customer");
        dto.setEmail("full@example.com");
        dto.setPhoneNumber("5555555555");
        dto.setUserId(1L);
        
        Customer createdCustomer = new Customer();
        createdCustomer.setCustomerId(3L);
        createdCustomer.setFullName("Full Customer");
        createdCustomer.setPhoneNumber("5555555555");
        
        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

        ResponseEntity<CustomerDto> response = customerController.createCustomer(dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteCustomer_Success_ReturnsOk() {
        doNothing().when(customerService).deleteCustomer(1L);
        
        ResponseEntity<Void> response = customerController.deleteCustomer(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_NonExistent_ReturnsOk() {
        doNothing().when(customerService).deleteCustomer(999L);
        
        ResponseEntity<Void> response = customerController.deleteCustomer(999L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerService, times(1)).deleteCustomer(999L);
    }

    @Test
    void getAllCustomers_MultipleCustomers_ReturnsAll() {
        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setFullName("Second Customer");
        
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(testCustomer, customer2));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    // Additional tests for missing method coverage

    @Test
    void getAllCustomers_OperatorRole_ReturnsAll() {
        User operatorUser = new User();
        operatorUser.setUserId(3L);
        operatorUser.setUsername("operator");
        operatorUser.setRole(UserRole.OPERATOR);

        when(authentication.getName()).thenReturn("operator");
        when(userService.getUserByUsername("operator")).thenReturn(operatorUser);
        when(customerService.getAllCustomers()).thenReturn(List.of(testCustomer));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById_AsCustomer_OtherCustomer_ReturnsForbidden() {
        User otherUser = new User();
        otherUser.setUserId(99L);
        otherUser.setUsername("otheruser");
        otherUser.setRole(UserRole.CUSTOMER);
        
        testCustomer.setUser(customerUser);
        
        when(authentication.getName()).thenReturn("otheruser");
        when(userService.getUserByUsername("otheruser")).thenReturn(otherUser);
        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);

        ResponseEntity<CustomerDto> response = customerController.getCustomerById(1L, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getCustomerServices_ReturnsServiceList() {
        Service service1 = new Service();
        service1.setServiceId(1L);
        service1.setServiceType("DATA");
        
        Service service2 = new Service();
        service2.setServiceId(2L);
        service2.setServiceType("VOICE");

        when(serviceService.getServicesByCustomerId(1L)).thenReturn(Arrays.asList(service1, service2));

        ResponseEntity<List<Service>> response = customerController.getCustomerServices(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getCustomerServices_NoServices_ReturnsEmptyList() {
        when(serviceService.getServicesByCustomerId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Service>> response = customerController.getCustomerServices(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void addCustomerService_Success_ReturnsCreatedService() {
        Service newService = new Service();
        newService.setServiceType("DATA");
        newService.setStatus("ACTIVE");

        Service createdService = new Service();
        createdService.setServiceId(10L);
        createdService.setServiceType("DATA");
        createdService.setStatus("ACTIVE");
        createdService.setCustomer(testCustomer);

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(serviceService.createService(any(Service.class))).thenReturn(createdService);

        ResponseEntity<Service> response = customerController.addCustomerService(1L, newService);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("DATA", response.getBody().getServiceType());
        assertEquals("ACTIVE", response.getBody().getStatus());
    }

    @Test
    void getCustomerInvoices_ReturnsInvoiceList() {
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceId(1L);
        invoice1.setTotalAmount(100.0);
        invoice1.setStatus("PENDING");

        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceId(2L);
        invoice2.setTotalAmount(200.0);
        invoice2.setStatus("PAID");

        when(invoiceService.getInvoicesByCustomerId(1L)).thenReturn(Arrays.asList(invoice1, invoice2));

        ResponseEntity<List<Invoice>> response = customerController.getCustomerInvoices(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getCustomerInvoices_NoInvoices_ReturnsEmptyList() {
        when(invoiceService.getInvoicesByCustomerId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Invoice>> response = customerController.getCustomerInvoices(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void generateCustomerInvoice_Success_ReturnsCreatedInvoice() {
        Invoice newInvoice = new Invoice();
        newInvoice.setTotalAmount(150.0);
        newInvoice.setStatus("PENDING");

        Invoice createdInvoice = new Invoice();
        createdInvoice.setInvoiceId(10L);
        createdInvoice.setTotalAmount(150.0);
        createdInvoice.setStatus("PENDING");
        createdInvoice.setCustomer(testCustomer);

        when(customerService.getCustomerById(1L)).thenReturn(testCustomer);
        when(invoiceService.createInvoice(any(Invoice.class))).thenReturn(createdInvoice);

        ResponseEntity<Invoice> response = customerController.generateCustomerInvoice(1L, newInvoice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150.0, response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
    }

    @Test
    void toDto_CustomerWithNullUser_HandlesGracefully() {
        Customer customerWithNullUser = new Customer();
        customerWithNullUser.setCustomerId(5L);
        customerWithNullUser.setFullName("No User Customer");
        customerWithNullUser.setPhoneNumber("1111111111");
        customerWithNullUser.setAddress("456 Test St");
        customerWithNullUser.setUser(null);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(List.of(customerWithNullUser));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getEmail());
        assertNull(response.getBody().get(0).getUserId());
        assertNull(response.getBody().get(0).getUser());
    }

    @Test
    void toDto_CustomerWithUserAndRole_IncludesUserDto() {
        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(List.of(testCustomer));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get(0).getUser());
        assertEquals("customer", response.getBody().get(0).getUser().getUsername());
        assertEquals("CUSTOMER", response.getBody().get(0).getUser().getRole());
    }

    @Test
    void toDto_CustomerWithUserNullRole_HandlesGracefully() {
        User userWithNullRole = new User();
        userWithNullRole.setUserId(10L);
        userWithNullRole.setUsername("nullrole");
        userWithNullRole.setEmail("nullrole@test.com");
        userWithNullRole.setRole(null);

        Customer customerWithNullRole = new Customer();
        customerWithNullRole.setCustomerId(6L);
        customerWithNullRole.setFullName("Null Role Customer");
        customerWithNullRole.setUser(userWithNullRole);

        when(authentication.getName()).thenReturn("admin");
        when(userService.getUserByUsername("admin")).thenReturn(adminUser);
        when(customerService.getAllCustomers()).thenReturn(List.of(customerWithNullRole));

        ResponseEntity<List<CustomerDto>> response = customerController.getAllCustomers(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().get(0).getUser().getRole());
    }

    @Test
    void createCustomer_WithAddress_ReturnsDto() {
        CustomerDto dto = new CustomerDto();
        dto.setName("Address Customer");
        dto.setPhoneNumber("2222222222");

        Customer createdCustomer = new Customer();
        createdCustomer.setCustomerId(7L);
        createdCustomer.setFullName("Address Customer");
        createdCustomer.setPhoneNumber("2222222222");
        createdCustomer.setAddress("789 Address St");

        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

        ResponseEntity<CustomerDto> response = customerController.createCustomer(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("789 Address St", response.getBody().getAddress());
    }
}
