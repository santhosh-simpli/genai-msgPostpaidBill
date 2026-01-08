package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsageRecordDtoTest {

    @Test
    void testUsageRecordDtoGettersAndSetters() {
        UsageRecordDto dto = new UsageRecordDto();
        
        dto.setUsageId(1L);
        dto.setServiceId(10L);
        dto.setAmount(150.5);
        dto.setUsageType("GB");
        dto.setUsageDate("2026-01-04");
        
        assertEquals(1L, dto.getUsageId());
        assertEquals(10L, dto.getServiceId());
        assertEquals(150.5, dto.getAmount());
        assertEquals("GB", dto.getUsageType());
        assertEquals("2026-01-04", dto.getUsageDate());
    }

    @Test
    void testUsageRecordDtoEquality() {
        UsageRecordDto dto1 = new UsageRecordDto();
        dto1.setUsageId(1L);
        dto1.setServiceId(5L);
        dto1.setAmount(100.0);
        dto1.setUsageType("Minutes");
        dto1.setUsageDate("2026-01-01");

        UsageRecordDto dto2 = new UsageRecordDto();
        dto2.setUsageId(1L);
        dto2.setServiceId(5L);
        dto2.setAmount(100.0);
        dto2.setUsageType("Minutes");
        dto2.setUsageDate("2026-01-01");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testUsageRecordDtoInequality() {
        UsageRecordDto dto1 = new UsageRecordDto();
        dto1.setUsageId(1L);
        dto1.setAmount(100.0);

        UsageRecordDto dto2 = new UsageRecordDto();
        dto2.setUsageId(2L);
        dto2.setAmount(200.0);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUsageRecordDtoToString() {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setUsageId(1L);
        dto.setAmount(500.0);
        dto.setUsageType("Hours");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("500.0"));
        assertTrue(toString.contains("Hours"));
    }

    @Test
    void testUsageRecordDtoNullValues() {
        UsageRecordDto dto = new UsageRecordDto();
        
        assertNull(dto.getUsageId());
        assertNull(dto.getServiceId());
        assertNull(dto.getAmount());
        assertNull(dto.getUsageType());
        assertNull(dto.getUsageDate());
    }

    @Test
    void testUsageRecordDtoWithZeroUsage() {
        UsageRecordDto dto = new UsageRecordDto();
        dto.setAmount(0.0);
        
        assertEquals(0.0, dto.getAmount());
    }

    @Test
    void testUsageRecordDtoAllUsageTypes() {
        String[] usageTypes = {"Minutes", "GB", "Hours", "Units"};
        
        for (String usageType : usageTypes) {
            UsageRecordDto dto = new UsageRecordDto();
            dto.setUsageType(usageType);
            assertEquals(usageType, dto.getUsageType());
        }
    }
}
