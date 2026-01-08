package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private Service service;
    private Customer customer;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("johndoe");
        
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFullName("John Doe");
        customer.setUser(user);
        
        service = new Service();
        service.setServiceId(1L);
        service.setCustomer(customer);
        service.setServiceType("DATA");
        service.setStatus("ACTIVE");
        service.setStartDate(LocalDate.of(2025, 1, 1));
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, service.getServiceId());
        assertEquals(customer, service.getCustomer());
        assertEquals("DATA", service.getServiceType());
        assertEquals("ACTIVE", service.getStatus());
        assertEquals(LocalDate.of(2025, 1, 1), service.getStartDate());
    }

    @Test
    void testEquality() {
        Service service1 = new Service();
        service1.setServiceId(1L);
        service1.setServiceType("DATA");
        service1.setStatus("ACTIVE");

        Service service2 = new Service();
        service2.setServiceId(1L);
        service2.setServiceType("DATA");
        service2.setStatus("ACTIVE");

        assertEquals(service1, service2);
        assertEquals(service1.hashCode(), service2.hashCode());
    }

    @Test
    void testInequality() {
        Service service1 = new Service();
        service1.setServiceId(1L);
        service1.setServiceType("DATA");

        Service service2 = new Service();
        service2.setServiceId(2L);
        service2.setServiceType("VOICE");

        assertNotEquals(service1, service2);
    }

    @Test
    void testToString() {
        String toString = service.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DATA"));
        assertTrue(toString.contains("ACTIVE"));
    }

    @Test
    void testNoArgsConstructor() {
        Service emptyService = new Service();
        assertNull(emptyService.getServiceId());
        assertNull(emptyService.getCustomer());
        assertNull(emptyService.getServiceType());
        assertNull(emptyService.getStatus());
        assertNull(emptyService.getStartDate());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2025, 6, 15);
        // Constructor order: serviceId, customer, serviceType, startDate, status
        Service fullService = new Service(2L, customer, "VOICE", date, "INACTIVE");
        
        assertEquals(2L, fullService.getServiceId());
        assertEquals(customer, fullService.getCustomer());
        assertEquals("VOICE", fullService.getServiceType());
        assertEquals("INACTIVE", fullService.getStatus());
        assertEquals(date, fullService.getStartDate());
    }

    @Test
    void testBuilder() {
        LocalDate date = LocalDate.of(2025, 7, 20);
        Service builtService = Service.builder()
                .serviceId(3L)
                .customer(customer)
                .serviceType("SMS")
                .status("SUSPENDED")
                .startDate(date)
                .build();
        
        assertEquals(3L, builtService.getServiceId());
        assertEquals(customer, builtService.getCustomer());
        assertEquals("SMS", builtService.getServiceType());
        assertEquals("SUSPENDED", builtService.getStatus());
        assertEquals(date, builtService.getStartDate());
    }

    @Test
    void testSetters() {
        Service testService = new Service();
        LocalDate date = LocalDate.of(2025, 8, 1);
        
        testService.setServiceId(100L);
        testService.setCustomer(customer);
        testService.setServiceType("ROAMING");
        testService.setStatus("PENDING");
        testService.setStartDate(date);
        
        assertEquals(100L, testService.getServiceId());
        assertEquals(customer, testService.getCustomer());
        assertEquals("ROAMING", testService.getServiceType());
        assertEquals("PENDING", testService.getStatus());
        assertEquals(date, testService.getStartDate());
    }

    @Test
    void testAllServiceTypes() {
        String[] types = {"DATA", "VOICE", "SMS", "ROAMING", "INTERNATIONAL", "PREMIUM"};
        
        for (String type : types) {
            service.setServiceType(type);
            assertEquals(type, service.getServiceType());
        }
    }

    @Test
    void testAllStatuses() {
        String[] statuses = {"ACTIVE", "INACTIVE", "SUSPENDED", "PENDING", "CANCELLED", "TERMINATED"};
        
        for (String status : statuses) {
            service.setStatus(status);
            assertEquals(status, service.getStatus());
        }
    }

    @Test
    void testCustomerRelationship() {
        Customer newCustomer = new Customer();
        newCustomer.setCustomerId(2L);
        newCustomer.setFullName("Jane Doe");
        
        service.setCustomer(newCustomer);
        assertEquals(newCustomer, service.getCustomer());
        assertEquals(2L, service.getCustomer().getCustomerId());
    }

    @Test
    void testNullCustomer() {
        Service serviceWithNullCustomer = new Service();
        serviceWithNullCustomer.setServiceId(1L);
        serviceWithNullCustomer.setCustomer(null);
        
        assertNull(serviceWithNullCustomer.getCustomer());
    }

    @Test
    void testDifferentStartDates() {
        LocalDate today = LocalDate.now();
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        LocalDate futureDate = LocalDate.now().plusYears(1);
        
        service.setStartDate(today);
        assertEquals(today, service.getStartDate());
        
        service.setStartDate(pastDate);
        assertEquals(pastDate, service.getStartDate());
        
        service.setStartDate(futureDate);
        assertEquals(futureDate, service.getStartDate());
    }

    @Test
    void testServiceActive() {
        service.setStatus("ACTIVE");
        assertEquals("ACTIVE", service.getStatus());
    }

    @Test
    void testServiceInactive() {
        service.setStatus("INACTIVE");
        assertEquals("INACTIVE", service.getStatus());
    }

    @Test
    void testServiceSuspended() {
        service.setStatus("SUSPENDED");
        assertEquals("SUSPENDED", service.getStatus());
    }
}
