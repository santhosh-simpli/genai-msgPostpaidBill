package com.msg.telecom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UsageRecordModelTest {

    private UsageRecord usageRecord;
    private Service testService;

    @BeforeEach
    void setUp() {
        Customer testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setFullName("John Doe");

        testService = new Service();
        testService.setServiceId(1L);
        testService.setCustomer(testCustomer);
        testService.setServiceType("Mobile");
        testService.setStatus("Active");

        usageRecord = new UsageRecord();
        usageRecord.setUsageId(1L);
        usageRecord.setService(testService);
        usageRecord.setUsageDate(LocalDate.of(2024, 1, 15));
        usageRecord.setUsageAmount(100.5);
        usageRecord.setUnit("mins");
    }

    @Test
    void testUsageRecordGettersAndSetters() {
        assertEquals(1L, usageRecord.getUsageId());
        assertEquals(testService, usageRecord.getService());
        assertEquals(LocalDate.of(2024, 1, 15), usageRecord.getUsageDate());
        assertEquals(100.5, usageRecord.getUsageAmount());
        assertEquals("mins", usageRecord.getUnit());
    }

    @Test
    void testUsageRecordBuilder() {
        UsageRecord builtRecord = UsageRecord.builder()
                .usageId(2L)
                .service(testService)
                .usageDate(LocalDate.of(2024, 2, 15))
                .usageAmount(200.0)
                .unit("GB")
                .build();

        assertEquals(2L, builtRecord.getUsageId());
        assertEquals(200.0, builtRecord.getUsageAmount());
        assertEquals("GB", builtRecord.getUnit());
    }

    @Test
    void testUsageRecordNoArgsConstructor() {
        UsageRecord emptyRecord = new UsageRecord();
        assertNull(emptyRecord.getUsageId());
        assertNull(emptyRecord.getUsageAmount());
        assertNull(emptyRecord.getUnit());
    }

    @Test
    void testUsageRecordAllArgsConstructor() {
        UsageRecord fullRecord = new UsageRecord(3L, testService,
                LocalDate.of(2024, 3, 15), 300.0, "msgs");

        assertEquals(3L, fullRecord.getUsageId());
        assertEquals(300.0, fullRecord.getUsageAmount());
        assertEquals("msgs", fullRecord.getUnit());
    }

    @Test
    void testEqualsAndHashCode() {
        UsageRecord record1 = UsageRecord.builder()
                .usageId(1L)
                .usageAmount(100.5)
                .unit("mins")
                .build();
        UsageRecord record2 = UsageRecord.builder()
                .usageId(1L)
                .usageAmount(100.5)
                .unit("mins")
                .build();

        assertEquals(record1, record2);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void testNotEquals() {
        UsageRecord record1 = UsageRecord.builder()
                .usageId(1L)
                .usageAmount(100.0)
                .build();
        UsageRecord record2 = UsageRecord.builder()
                .usageId(2L)
                .usageAmount(200.0)
                .build();

        assertNotEquals(record1, record2);
    }

    @Test
    void testToString() {
        String toString = usageRecord.toString();
        assertTrue(toString.contains("100.5"));
        assertTrue(toString.contains("mins"));
    }

    @Test
    void testUnitTypes() {
        usageRecord.setUnit("mins");
        assertEquals("mins", usageRecord.getUnit());

        usageRecord.setUnit("GB");
        assertEquals("GB", usageRecord.getUnit());

        usageRecord.setUnit("msgs");
        assertEquals("msgs", usageRecord.getUnit());

        usageRecord.setUnit("hours");
        assertEquals("hours", usageRecord.getUnit());
    }

    @Test
    void testServiceRelationship() {
        assertNotNull(usageRecord.getService());
        assertEquals(1L, usageRecord.getService().getServiceId());
        assertEquals("Mobile", usageRecord.getService().getServiceType());
    }

    @Test
    void testUsageDateManipulation() {
        LocalDate today = LocalDate.now();
        usageRecord.setUsageDate(today);
        assertEquals(today, usageRecord.getUsageDate());
    }

    @Test
    void testUsageAmountPrecision() {
        usageRecord.setUsageAmount(123.456);
        assertEquals(123.456, usageRecord.getUsageAmount());

        usageRecord.setUsageAmount(0.001);
        assertEquals(0.001, usageRecord.getUsageAmount());
    }
}
