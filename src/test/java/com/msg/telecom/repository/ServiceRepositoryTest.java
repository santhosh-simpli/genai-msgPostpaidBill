package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void findByCustomer_CustomerId_ReturnsServices() {
        Customer customer = new Customer();
        customer.setFullName("John Doe");

        Service service = new Service();
        service.setCustomer(customer);
        service.setStatus("ACTIVE");
        serviceRepository.save(service);

        List<Service> services = serviceRepository.findByCustomer_CustomerId(customer.getCustomerId());
        assertFalse(services.isEmpty());
        assertEquals("ACTIVE", services.get(0).getStatus());
    }

    @Test
    void findByStatus_ReturnsServices() {
        Service service = new Service();
        service.setStatus("INACTIVE");
        serviceRepository.save(service);

        List<Service> services = serviceRepository.findByStatus("INACTIVE");
        assertFalse(services.isEmpty());
        assertEquals("INACTIVE", services.get(0).getStatus());
    }
}