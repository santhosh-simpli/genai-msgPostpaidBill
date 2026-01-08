package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsageRecordTest {

    private UsageRecord usageRecord;
    private Service service;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        customer.setFullName("John Doe");
        
        service = new Service();
        service.setServiceId(1L);
        service.setCustomer(customer);
        service.setServiceType("DATA");
        service.setStatus("ACTIVE");
        
        usageRecord = new UsageRecord();
        usageRecord.setUsageId(1L);
        usageRecord.setService(service);
        usageRecord.setUsageDate(LocalDate.of(2026, 1, 15));
        usageRecord.setUsageAmount(5.5);
        usageRecord.setUnit("GB");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, usageRecord.getUsageId());
        assertEquals(service, usageRecord.getService());
        assertEquals(LocalDate.of(2026, 1, 15), usageRecord.getUsageDate());
        assertEquals(5.5, usageRecord.getUsageAmount());
        assertEquals("GB", usageRecord.getUnit());
    }

    @Test
    void testEquality() {
        UsageRecord record1 = new UsageRecord();
        record1.setUsageId(1L);
        record1.setUsageAmount(5.5);
        record1.setUnit("GB");

        UsageRecord record2 = new UsageRecord();
        record2.setUsageId(1L);
        record2.setUsageAmount(5.5);
        record2.setUnit("GB");

        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void testInequality() {
        UsageRecord record1 = new UsageRecord();
        record1.setUsageId(1L);
        record1.setUsageAmount(5.5);

        UsageRecord record2 = new UsageRecord();
        record2.setUsageId(2L);
        record2.setUsageAmount(10.0);

        assertNotEquals(record1, record2);
    }

    @Test
    void testToString() {
        String toString = usageRecord.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("5.5"));
        assertTrue(toString.contains("GB"));
    }

    @Test
    void testNoArgsConstructor() {
        UsageRecord emptyRecord = new UsageRecord();
        assertNull(emptyRecord.getUsageId());
        assertNull(emptyRecord.getService());
        assertNull(emptyRecord.getUsageDate());
        assertNull(emptyRecord.getUsageAmount());
        assertNull(emptyRecord.getUnit());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate date = LocalDate.of(2026, 2, 20);
        UsageRecord fullRecord = new UsageRecord(2L, service, date, 120.0, "Minutes");
        
        assertEquals(2L, fullRecord.getUsageId());
        assertEquals(service, fullRecord.getService());
        assertEquals(date, fullRecord.getUsageDate());
        assertEquals(120.0, fullRecord.getUsageAmount());
        assertEquals("Minutes", fullRecord.getUnit());
    }

    @Test
    void testBuilder() {
        LocalDate date = LocalDate.of(2026, 3, 10);
        UsageRecord builtRecord = UsageRecord.builder()
                .usageId(3L)
                .service(service)
                .usageDate(date)
                .usageAmount(50.0)
                .unit("SMS")
                .build();
        
        assertEquals(3L, builtRecord.getUsageId());
        assertEquals(service, builtRecord.getService());
        assertEquals(date, builtRecord.getUsageDate());
        assertEquals(50.0, builtRecord.getUsageAmount());
        assertEquals("SMS", builtRecord.getUnit());
    }

    @Test
    void testSetters() {
        UsageRecord testRecord = new UsageRecord();
        LocalDate date = LocalDate.of(2026, 4, 5);
        
        testRecord.setUsageId(100L);
        testRecord.setService(service);
        testRecord.setUsageDate(date);
        testRecord.setUsageAmount(200.0);
        testRecord.setUnit("Hours");
        
        assertEquals(100L, testRecord.getUsageId());
        assertEquals(service, testRecord.getService());
        assertEquals(date, testRecord.getUsageDate());
        assertEquals(200.0, testRecord.getUsageAmount());
        assertEquals("Hours", testRecord.getUnit());
    }

    @Test
    void testAllUnits() {
        String[] units = {"GB", "MB", "KB", "Minutes", "SMS", "Hours", "Calls", "Messages"};
        
        for (String unit : units) {
            usageRecord.setUnit(unit);
            assertEquals(unit, usageRecord.getUnit());
        }
    }

    @Test
    void testZeroUsageAmount() {
        usageRecord.setUsageAmount(0.0);
        assertEquals(0.0, usageRecord.getUsageAmount());
    }

    @Test
    void testLargeUsageAmount() {
        usageRecord.setUsageAmount(999999.99);
        assertEquals(999999.99, usageRecord.getUsageAmount());
    }

    @Test
    void testSmallUsageAmount() {
        usageRecord.setUsageAmount(0.001);
        assertEquals(0.001, usageRecord.getUsageAmount());
    }

    @Test
    void testServiceRelationship() {
        Service newService = new Service();
        newService.setServiceId(2L);
        newService.setServiceType("VOICE");
        
        usageRecord.setService(newService);
        assertEquals(newService, usageRecord.getService());
        assertEquals(2L, usageRecord.getService().getServiceId());
    }

    @Test
    void testNullService() {
        UsageRecord recordWithNullService = new UsageRecord();
        recordWithNullService.setUsageId(1L);
        recordWithNullService.setService(null);
        
        assertNull(recordWithNullService.getService());
    }

    @Test
    void testDifferentUsageDates() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        
        usageRecord.setUsageDate(today);
        assertEquals(today, usageRecord.getUsageDate());
        
        usageRecord.setUsageDate(yesterday);
        assertEquals(yesterday, usageRecord.getUsageDate());
        
        usageRecord.setUsageDate(lastMonth);
        assertEquals(lastMonth, usageRecord.getUsageDate());
    }

    @Test
    void testDataUsage() {
        usageRecord.setUsageAmount(10.5);
        usageRecord.setUnit("GB");
        assertEquals(10.5, usageRecord.getUsageAmount());
        assertEquals("GB", usageRecord.getUnit());
    }

    @Test
    void testVoiceUsage() {
        usageRecord.setUsageAmount(300.0);
        usageRecord.setUnit("Minutes");
        assertEquals(300.0, usageRecord.getUsageAmount());
        assertEquals("Minutes", usageRecord.getUnit());
    }

    @Test
    void testSmsUsage() {
        usageRecord.setUsageAmount(100.0);
        usageRecord.setUnit("SMS");
        assertEquals(100.0, usageRecord.getUsageAmount());
        assertEquals("SMS", usageRecord.getUnit());
    }
}
