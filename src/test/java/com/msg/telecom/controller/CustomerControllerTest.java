package com.msg.telecom.controller;

import com.msg.telecom.dto.CustomerDto;
import com.msg.telecom.model.Customer;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.service.CustomerService;
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
}
