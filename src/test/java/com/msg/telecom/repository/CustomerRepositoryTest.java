package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@msgtel.com");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    void testFindById_Success() {
        Customer customer = Customer.builder()
                .fullName("John Doe")
                .email("john.doe@msgtel.com")
                .address("123 Main St")
                .phoneNumber("1234567890")
                .user(testUser)
                .build();
        customer = customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findById(customer.getCustomerId());

        assertTrue(foundCustomer.isPresent());
        assertEquals("John Doe", foundCustomer.get().getFullName());
        assertEquals("123 Main St", foundCustomer.get().getAddress());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Customer> foundCustomer = customerRepository.findById(999L);
        assertFalse(foundCustomer.isPresent());
    }

    @Test
    void findByUser_UserId_ReturnsCustomers() {
        Customer customer = new Customer();
        customer.setFullName("John Doe");
        customer.setAddress("456 Test Ave");
        customer.setPhoneNumber("9876543210");
        customer.setEmail("john@test.com");
        customer.setUser(testUser);
        customerRepository.save(customer);

        List<Customer> customers = customerRepository.findByUser_UserId(testUser.getUserId());
        assertFalse(customers.isEmpty());
        assertEquals("John Doe", customers.get(0).getFullName());
    }

    @Test
    void existsByPhoneNumber_ReturnsTrue() {
        Customer customer = new Customer();
        customer.setFullName("Jane Doe");
        customer.setAddress("789 Sample Blvd");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("jane@test.com");
        customer.setUser(testUser);
        customerRepository.save(customer);

        boolean exists = customerRepository.existsByPhoneNumber("1234567890");
        assertTrue(exists);
    }
}
