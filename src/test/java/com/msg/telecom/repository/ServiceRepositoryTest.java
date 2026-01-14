package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@msgtel.com");
        testUser.setPasswordHash("password");
        testUser.setRole(UserRole.CUSTOMER);
        testUser = entityManager.persistAndFlush(testUser);

        testCustomer = new Customer();
        testCustomer.setFullName("John Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setEmail("john@test.com");
        testCustomer.setUser(testUser);
        testCustomer = entityManager.persistAndFlush(testCustomer);
    }

    @Test
    void findByCustomer_CustomerId_ReturnsServices() {
        Service service = new Service();
        service.setCustomer(testCustomer);
        service.setStatus("ACTIVE");
        service.setServiceType("POSTPAID");
        service.setStartDate(LocalDate.now());
        serviceRepository.save(service);

        List<Service> services = serviceRepository.findByCustomer_CustomerId(testCustomer.getCustomerId());
        assertFalse(services.isEmpty());
        assertEquals("ACTIVE", services.get(0).getStatus());
    }

    @Test
    void findByStatus_ReturnsServices() {
        Service service = new Service();
        service.setCustomer(testCustomer);
        service.setStatus("INACTIVE");
        service.setServiceType("POSTPAID");
        service.setStartDate(LocalDate.now());
        serviceRepository.save(service);

        List<Service> services = serviceRepository.findByStatus("INACTIVE");
        assertFalse(services.isEmpty());
        assertEquals("INACTIVE", services.get(0).getStatus());
    }
}
