package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceDtoTest {

    @Test
    void testServiceDtoGettersAndSetters() {
        ServiceDto dto = new ServiceDto();
        
        dto.setServiceId(1L);
        dto.setServiceType("Mobile");
        dto.setStatus("Active");
        dto.setCustomerId(10L);
        
        assertEquals(1L, dto.getServiceId());
        assertEquals("Mobile", dto.getServiceType());
        assertEquals("Active", dto.getStatus());
        assertEquals(10L, dto.getCustomerId());
    }

    @Test
    void testServiceDtoEquality() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setServiceType("Broadband");
        dto1.setStatus("Active");
        dto1.setCustomerId(5L);

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(1L);
        dto2.setServiceType("Broadband");
        dto2.setStatus("Active");
        dto2.setCustomerId(5L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testServiceDtoInequality() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setServiceType("Mobile");

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(2L);
        dto2.setServiceType("Broadband");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testServiceDtoToString() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(1L);
        dto.setServiceType("VoIP");
        dto.setStatus("Suspended");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("VoIP"));
        assertTrue(toString.contains("Suspended"));
    }

    @Test
    void testServiceDtoNullValues() {
        ServiceDto dto = new ServiceDto();
        
        assertNull(dto.getServiceId());
        assertNull(dto.getServiceType());
        assertNull(dto.getStatus());
        assertNull(dto.getCustomerId());
    }

    @Test
    void testServiceDtoAllServiceTypes() {
        String[] serviceTypes = {"Mobile", "Broadband", "Cable TV", "VoIP"};
        
        for (String type : serviceTypes) {
            ServiceDto dto = new ServiceDto();
            dto.setServiceType(type);
            assertEquals(type, dto.getServiceType());
        }
    }

    @Test
    void testServiceDtoAllStatuses() {
        String[] statuses = {"Active", "Inactive", "Suspended", "Cancelled"};
        
        for (String status : statuses) {
            ServiceDto dto = new ServiceDto();
            dto.setStatus(status);
            assertEquals(status, dto.getStatus());
        }
    }

    @Test
    void testServiceDtoEqualityWithSelf() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(1L);
        dto.setServiceType("Mobile");
        assertEquals(dto, dto);
    }

    @Test
    void testServiceDtoNotEqualToNull() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(1L);
        assertNotEquals(null, dto);
    }

    @Test
    void testServiceDtoNotEqualToDifferentClass() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(1L);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void testServiceDtoHashCodeConsistency() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(1L);
        dto.setServiceType("Mobile");
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testServiceDtoInequalityByServiceId() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setServiceType("Mobile");

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(2L);
        dto2.setServiceType("Mobile");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testServiceDtoInequalityByServiceType() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setServiceType("Mobile");

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(1L);
        dto2.setServiceType("Broadband");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testServiceDtoInequalityByStatus() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setStatus("Active");

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(1L);
        dto2.setStatus("Inactive");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testServiceDtoInequalityByCustomerId() {
        ServiceDto dto1 = new ServiceDto();
        dto1.setServiceId(1L);
        dto1.setCustomerId(100L);

        ServiceDto dto2 = new ServiceDto();
        dto2.setServiceId(1L);
        dto2.setCustomerId(200L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testServiceDtoWithEmptyStrings() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceType("");
        dto.setStatus("");
        
        assertEquals("", dto.getServiceType());
        assertEquals("", dto.getStatus());
    }

    @Test
    void testServiceDtoWithMaxLongId() {
        ServiceDto dto = new ServiceDto();
        dto.setServiceId(Long.MAX_VALUE);
        dto.setCustomerId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getServiceId());
        assertEquals(Long.MAX_VALUE, dto.getCustomerId());
    }
}
