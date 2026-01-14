package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testUserDtoGettersAndSetters() {
        UserDto dto = new UserDto();
        
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@msgtel.com");
        dto.setRole("ADMIN");
        
        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@msgtel.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
    }

    @Test
    void testUserDtoEquality() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setUsername("user");
        dto1.setEmail("user@test.com");
        dto1.setRole("CUSTOMER");

        UserDto dto2 = new UserDto();
        dto2.setUserId(1L);
        dto2.setUsername("user");
        dto2.setEmail("user@test.com");
        dto2.setRole("CUSTOMER");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testUserDtoInequality() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setUsername("user1");

        UserDto dto2 = new UserDto();
        dto2.setUserId(2L);
        dto2.setUsername("user2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserDtoToString() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        dto.setUsername("testuser");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testUserDtoNullValues() {
        UserDto dto = new UserDto();
        
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getRole());
    }

    @Test
    void testUserDtoEqualityWithSelf() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        dto.setUsername("test");
        assertEquals(dto, dto);
    }

    @Test
    void testUserDtoNotEqualToNull() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        assertNotEquals(null, dto);
    }

    @Test
    void testUserDtoNotEqualToDifferentClass() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void testUserDtoHashCodeConsistency() {
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        dto.setUsername("test");
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testUserDtoInequalityByUserId() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setUsername("same");

        UserDto dto2 = new UserDto();
        dto2.setUserId(2L);
        dto2.setUsername("same");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserDtoInequalityByUsername() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setUsername("user1");

        UserDto dto2 = new UserDto();
        dto2.setUserId(1L);
        dto2.setUsername("user2");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserDtoInequalityByEmail() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setEmail("email1@test.com");

        UserDto dto2 = new UserDto();
        dto2.setUserId(1L);
        dto2.setEmail("email2@test.com");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserDtoInequalityByRole() {
        UserDto dto1 = new UserDto();
        dto1.setUserId(1L);
        dto1.setRole("ADMIN");

        UserDto dto2 = new UserDto();
        dto2.setUserId(1L);
        dto2.setRole("CUSTOMER");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testUserDtoAllRoles() {
        String[] roles = {"ADMIN", "CUSTOMER", "OPERATOR"};
        for (String role : roles) {
            UserDto dto = new UserDto();
            dto.setRole(role);
            assertEquals(role, dto.getRole());
        }
    }

    @Test
    void testUserDtoWithEmptyStrings() {
        UserDto dto = new UserDto();
        dto.setUsername("");
        dto.setEmail("");
        dto.setRole("");
        
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getRole());
    }

    @Test
    void testUserDtoWithSpecialCharacters() {
        UserDto dto = new UserDto();
        dto.setUsername("user_name-123");
        dto.setEmail("test+special@msgtel.com");
        
        assertEquals("user_name-123", dto.getUsername());
        assertEquals("test+special@msgtel.com", dto.getEmail());
    }

    @Test
    void testUserDtoWithMaxLongId() {
        UserDto dto = new UserDto();
        dto.setUserId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dto.getUserId());
    }
}
