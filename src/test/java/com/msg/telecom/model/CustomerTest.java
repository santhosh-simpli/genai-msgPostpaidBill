package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setEmail("test@msgtel.com");
        user.setRole(UserRole.CUSTOMER);

        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setUser(user);
        customer.setFullName("John Doe");
        customer.setAddress("123 Main St, City, Country");
        customer.setPhoneNumber("1234567890");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, customer.getCustomerId());
        assertEquals(user, customer.getUser());
        assertEquals("John Doe", customer.getFullName());
        assertEquals("123 Main St, City, Country", customer.getAddress());
        assertEquals("1234567890", customer.getPhoneNumber());
    }

    @Test
    void testEquality() {
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setFullName("John Doe");
        customer1.setPhoneNumber("1234567890");

        Customer customer2 = new Customer();
        customer2.setCustomerId(1L);
        customer2.setFullName("John Doe");
        customer2.setPhoneNumber("1234567890");

        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void testInequality() {
        Customer customer1 = new Customer();
        customer1.setCustomerId(1L);
        customer1.setFullName("John Doe");

        Customer customer2 = new Customer();
        customer2.setCustomerId(2L);
        customer2.setFullName("Jane Doe");

        assertNotEquals(customer1, customer2);
    }

    @Test
    void testToString() {
        String toString = customer.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("1234567890"));
    }

    @Test
    void testNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNull(emptyCustomer.getCustomerId());
        assertNull(emptyCustomer.getUser());
        assertNull(emptyCustomer.getFullName());
        assertNull(emptyCustomer.getAddress());
        assertNull(emptyCustomer.getPhoneNumber());
    }

    @Test
    void testAllArgsConstructor() {
        Customer fullCustomer = new Customer(2L, user, "Jane Doe", "456 Oak Ave", "9876543210");
        
        assertEquals(2L, fullCustomer.getCustomerId());
        assertEquals(user, fullCustomer.getUser());
        assertEquals("Jane Doe", fullCustomer.getFullName());
        assertEquals("456 Oak Ave", fullCustomer.getAddress());
        assertEquals("9876543210", fullCustomer.getPhoneNumber());
    }

    @Test
    void testBuilder() {
        Customer builtCustomer = Customer.builder()
                .customerId(3L)
                .user(user)
                .fullName("Built Customer")
                .address("789 Pine St")
                .phoneNumber("5551234567")
                .build();
        
        assertEquals(3L, builtCustomer.getCustomerId());
        assertEquals(user, builtCustomer.getUser());
        assertEquals("Built Customer", builtCustomer.getFullName());
        assertEquals("789 Pine St", builtCustomer.getAddress());
        assertEquals("5551234567", builtCustomer.getPhoneNumber());
    }

    @Test
    void testSetters() {
        Customer testCustomer = new Customer();
        testCustomer.setCustomerId(100L);
        testCustomer.setUser(user);
        testCustomer.setFullName("New Customer");
        testCustomer.setAddress("New Address");
        testCustomer.setPhoneNumber("1112223333");
        
        assertEquals(100L, testCustomer.getCustomerId());
        assertEquals(user, testCustomer.getUser());
        assertEquals("New Customer", testCustomer.getFullName());
        assertEquals("New Address", testCustomer.getAddress());
        assertEquals("1112223333", testCustomer.getPhoneNumber());
    }

    @Test
    void testUserRelationship() {
        User newUser = new User();
        newUser.setUserId(2L);
        newUser.setUsername("anotheruser");
        
        customer.setUser(newUser);
        assertEquals(newUser, customer.getUser());
        assertEquals(2L, customer.getUser().getUserId());
    }

    @Test
    void testNullUser() {
        Customer customerWithNullUser = new Customer();
        customerWithNullUser.setCustomerId(1L);
        customerWithNullUser.setFullName("No User");
        customerWithNullUser.setUser(null);
        
        assertNull(customerWithNullUser.getUser());
    }

    @Test
    void testDifferentPhoneFormats() {
        customer.setPhoneNumber("+1-123-456-7890");
        assertEquals("+1-123-456-7890", customer.getPhoneNumber());
        
        customer.setPhoneNumber("(123) 456-7890");
        assertEquals("(123) 456-7890", customer.getPhoneNumber());
    }

    @Test
    void testLongAddress() {
        String longAddress = "123 Very Long Street Name, Apartment 456, Building C, Some Very Long City Name, Some State, 12345-6789, Country";
        customer.setAddress(longAddress);
        assertEquals(longAddress, customer.getAddress());
    }
}
