package com.msg.telecom.service;

import com.msg.telecom.model.*;
import com.msg.telecom.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ServiceRepository serviceRepository;
    @Mock
    private UsageRecordRepository usageRecordRepository;
    @Mock
    private InvoiceRepository invoiceRepository;

    private CustomerService customerService;
    private Customer testCustomer;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerService(customerRepository, serviceRepository, 
                                              usageRecordRepository, invoiceRepository);
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("John Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setUser(testUser);
    }

    @Test
    void getAllCustomers_ReturnsList() {
        when(customerRepository.findAll()).thenReturn(List.of(testCustomer));
        List<Customer> customers = customerService.getAllCustomers();
        assertEquals(1, customers.size());
        assertEquals("John Doe", customers.get(0).getFullName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getAllCustomers_ReturnsEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<Customer> customers = customerService.getAllCustomers();
        assertTrue(customers.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomerById_Found() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        Customer result = customerService.getCustomerById(1L);
        assertEquals(1L, result.getCustomerId());
        assertEquals("John Doe", result.getFullName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void getCustomerById_NotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(999L));
        assertTrue(ex.getMessage().contains("Customer not found"));
        verify(customerRepository, times(1)).findById(999L);
    }

    @Test
    void getCustomersByUserId_ReturnsList() {
        when(customerRepository.findByUser_UserId(1L)).thenReturn(List.of(testCustomer));
        List<Customer> customers = customerService.getCustomersByUserId(1L);
        assertEquals(1, customers.size());
        verify(customerRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    void getCustomersByUserId_ReturnsEmptyList() {
        when(customerRepository.findByUser_UserId(999L)).thenReturn(Collections.emptyList());
        List<Customer> customers = customerService.getCustomersByUserId(999L);
        assertTrue(customers.isEmpty());
        verify(customerRepository, times(1)).findByUser_UserId(999L);
    }

    @Test
    void createCustomer_Success_WithoutPhone() {
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Jane Doe");
        newCustomer.setPhoneNumber(null);
        
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);
        when(serviceRepository.save(any(Service.class))).thenReturn(new Service());
        when(usageRecordRepository.save(any(UsageRecord.class))).thenReturn(new UsageRecord());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(new Invoice());
        
        Customer created = customerService.createCustomer(newCustomer);
        assertNotNull(created);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void createCustomer_Success_WithPhone() {
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Jane Doe");
        newCustomer.setPhoneNumber("9876543210");
        
        when(customerRepository.existsByPhoneNumber("9876543210")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);
        when(serviceRepository.save(any(Service.class))).thenReturn(new Service());
        when(usageRecordRepository.save(any(UsageRecord.class))).thenReturn(new UsageRecord());
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(new Invoice());
        
        Customer created = customerService.createCustomer(newCustomer);
        assertNotNull(created);
        verify(customerRepository, times(1)).existsByPhoneNumber("9876543210");
    }

    @Test
    void createCustomer_PhoneExists() {
        Customer customer = new Customer();
        customer.setPhoneNumber("1234567890");
        when(customerRepository.existsByPhoneNumber("1234567890")).thenReturn(true);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(customer));
        assertTrue(ex.getMessage().contains("Phone number already exists"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_NullAddress() {
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Jane Doe");
        newCustomer.setAddress(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(newCustomer));
        assertTrue(ex.getMessage().contains("Address cannot be null"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_Success() {
        Customer updateDetails = new Customer();
        updateDetails.setFullName("Updated Name");
        updateDetails.setAddress("456 New St");
        updateDetails.setPhoneNumber("5555555555");
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        
        Customer updated = customerService.updateCustomer(1L, updateDetails);
        assertNotNull(updated);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound() {
        Customer updateDetails = new Customer();
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> customerService.updateCustomer(999L, updateDetails));
    }

    @Test
    void updateCustomer_InvalidPhoneNumber() {
        Customer updateDetails = new Customer();
        updateDetails.setPhoneNumber("invalid-phone");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.updateCustomer(1L, updateDetails));
        assertTrue(ex.getMessage().contains("Invalid phone number"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        doNothing().when(customerRepository).deleteById(1L);
        
        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCustomer_NonExistent() {
        doNothing().when(customerRepository).deleteById(999L);
        
        assertDoesNotThrow(() -> customerService.deleteCustomer(999L));
        verify(customerRepository, times(1)).deleteById(999L);
    }
}
