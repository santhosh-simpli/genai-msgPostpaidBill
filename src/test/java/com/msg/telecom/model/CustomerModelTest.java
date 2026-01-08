package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerModelTest {

    private Customer customer;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setUser(testUser);
        customer.setFullName("John Doe");
        customer.setAddress("123 Main Street");
        customer.setPhoneNumber("+1-555-123-4567");
    }

    @Test
    void testCustomerGettersAndSetters() {
        assertEquals(1L, customer.getCustomerId());
        assertEquals("John Doe", customer.getFullName());
        assertEquals("123 Main Street", customer.getAddress());
        assertEquals("+1-555-123-4567", customer.getPhoneNumber());
        assertEquals(testUser, customer.getUser());
    }

    @Test
    void testCustomerBuilder() {
        Customer builtCustomer = Customer.builder()
                .customerId(2L)
                .user(testUser)
                .fullName("Jane Doe")
                .address("456 Oak Avenue")
                .phoneNumber("+1-555-987-6543")
                .build();

        assertEquals(2L, builtCustomer.getCustomerId());
        assertEquals("Jane Doe", builtCustomer.getFullName());
        assertEquals("456 Oak Avenue", builtCustomer.getAddress());
        assertEquals("+1-555-987-6543", builtCustomer.getPhoneNumber());
    }

    @Test
    void testCustomerNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNull(emptyCustomer.getCustomerId());
        assertNull(emptyCustomer.getFullName());
        assertNull(emptyCustomer.getAddress());
    }

    @Test
    void testCustomerAllArgsConstructor() {
        Customer fullCustomer = new Customer(3L, testUser, "Bob Smith", "789 Pine Road", "+1-555-555-5555");
        assertEquals(3L, fullCustomer.getCustomerId());
        assertEquals("Bob Smith", fullCustomer.getFullName());
        assertEquals("+1-555-555-5555", fullCustomer.getPhoneNumber());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer customer1 = Customer.builder()
                .customerId(1L)
                .fullName("John Doe")
                .build();
        Customer customer2 = Customer.builder()
                .customerId(1L)
                .fullName("John Doe")
                .build();

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void testNotEquals() {
        Customer customer1 = Customer.builder()
                .customerId(1L)
                .fullName("John Doe")
                .build();
        Customer customer2 = Customer.builder()
                .customerId(2L)
                .fullName("Jane Doe")
                .build();

        assertNotEquals(customer1, customer2);
    }

    @Test
    void testToString() {
        String toString = customer.toString();
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("123 Main Street"));
    }

    @Test
    void testSetters() {
        customer.setFullName("Updated Name");
        customer.setAddress("Updated Address");
        customer.setPhoneNumber("+1-999-999-9999");

        assertEquals("Updated Name", customer.getFullName());
        assertEquals("Updated Address", customer.getAddress());
        assertEquals("+1-999-999-9999", customer.getPhoneNumber());
    }

    @Test
    void testUserRelationship() {
        assertNotNull(customer.getUser());
        assertEquals(1L, customer.getUser().getUserId());
        assertEquals("testuser", customer.getUser().getUsername());
    }
}
