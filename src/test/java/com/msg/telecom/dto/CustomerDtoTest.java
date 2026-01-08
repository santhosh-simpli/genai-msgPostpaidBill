package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDtoTest {

    @Test
    void testCustomerDtoGettersAndSetters() {
        CustomerDto dto = new CustomerDto();
        
        dto.setCustomerId(1L);
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setUserId(10L);
        
        assertEquals(1L, dto.getCustomerId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals(10L, dto.getUserId());
    }

    @Test
    void testCustomerDtoEquality() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setName("Customer");
        dto1.setEmail("customer@test.com");
        dto1.setPhoneNumber("123456");
        dto1.setUserId(5L);

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(1L);
        dto2.setName("Customer");
        dto2.setEmail("customer@test.com");
        dto2.setPhoneNumber("123456");
        dto2.setUserId(5L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testCustomerDtoInequality() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setPhoneNumber("111");

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(2L);
        dto2.setPhoneNumber("222");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerDtoToString() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(1L);
        dto.setName("Test Customer");
        dto.setPhoneNumber("9876543210");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Customer"));
        assertTrue(toString.contains("9876543210"));
    }

    @Test
    void testCustomerDtoNullValues() {
        CustomerDto dto = new CustomerDto();
        
        assertNull(dto.getCustomerId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getUserId());
    }

    @Test
    void testCustomerDtoEqualityWithSelf() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(1L);
        dto.setName("Test");
        assertEquals(dto, dto);
    }

    @Test
    void testCustomerDtoNotEqualToNull() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(1L);
        assertNotEquals(null, dto);
    }

    @Test
    void testCustomerDtoNotEqualToDifferentClass() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(1L);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void testCustomerDtoHashCodeConsistency() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(1L);
        dto.setName("Test");
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testCustomerDtoInequalityByCustomerId() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setName("Same");
        dto1.setEmail("same@test.com");

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(2L);
        dto2.setName("Same");
        dto2.setEmail("same@test.com");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerDtoInequalityByName() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setName("Name1");

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(1L);
        dto2.setName("Name2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerDtoInequalityByEmail() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setEmail("email1@test.com");

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(1L);
        dto2.setEmail("email2@test.com");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerDtoInequalityByUserId() {
        CustomerDto dto1 = new CustomerDto();
        dto1.setCustomerId(1L);
        dto1.setUserId(100L);

        CustomerDto dto2 = new CustomerDto();
        dto2.setCustomerId(1L);
        dto2.setUserId(200L);

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testCustomerDtoWithEmptyStrings() {
        CustomerDto dto = new CustomerDto();
        dto.setName("");
        dto.setEmail("");
        dto.setPhoneNumber("");
        
        assertEquals("", dto.getName());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getPhoneNumber());
    }

    @Test
    void testCustomerDtoWithSpecialCharacters() {
        CustomerDto dto = new CustomerDto();
        dto.setName("O'Brien-Smith");
        dto.setEmail("test+special@example.com");
        dto.setPhoneNumber("+1-555-123-4567");
        
        assertEquals("O'Brien-Smith", dto.getName());
        assertEquals("test+special@example.com", dto.getEmail());
        assertEquals("+1-555-123-4567", dto.getPhoneNumber());
    }

    @Test
    void testCustomerDtoWithLongValues() {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(Long.MAX_VALUE);
        dto.setUserId(Long.MAX_VALUE);
        
        assertEquals(Long.MAX_VALUE, dto.getCustomerId());
        assertEquals(Long.MAX_VALUE, dto.getUserId());
    }
}
