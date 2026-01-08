package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testFindById_Success() {
        // Arrange
        User user = new User();
        user.setUserId(1L); // Ensure the User object has a valid ID

        Customer customer = Customer.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .user(user) // Associate the User object with the Customer
                .build();
        customer = customerRepository.save(customer);

        // Act
        Optional<Customer> foundCustomer = customerRepository.findById(customer.getCustomerId());

        // Assert
        assertTrue(foundCustomer.isPresent());
        assertEquals("John Doe", foundCustomer.get().getFullName());
        assertEquals(user.getUserId(), foundCustomer.get().getUser().getUserId()); // Verify User association
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Customer> foundCustomer = customerRepository.findById(999L);

        // Assert
        assertFalse(foundCustomer.isPresent());
    }

    // Update the test to use correct field names or add missing methods
    @Test
    public void testCustomerFields() {
        Customer customer = Customer.builder()
                .fullName("John Doe")
                .address("123 Main St")
                .phoneNumber("1234567890")
                .build();

        // Ensure the User object is properly initialized
        User user = new User();
        user.setUserId(1L);
        customer.setUser(user);

        assertEquals("John Doe", customer.getFullName());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("1234567890", customer.getPhoneNumber());
    }
}