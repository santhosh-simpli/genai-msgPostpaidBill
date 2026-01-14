package com.msg.telecom.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void testAuthResponseNoArgsConstructor() {
        AuthResponse response = new AuthResponse();
        
        assertNull(response.getToken());
        assertEquals("Bearer", response.getType());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testAuthResponseAllArgsConstructor() {
        AuthResponse response = new AuthResponse("token123", "Bearer", 1L, "testuser", "test@msgtel.com", "ADMIN");
        
        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@msgtel.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void testAuthResponseCustomConstructor() {
        AuthResponse response = new AuthResponse("jwt-token", 5L, "user", "user@test.com", "CUSTOMER");
        
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType()); // Default value
        assertEquals(5L, response.getId());
        assertEquals("user", response.getUsername());
        assertEquals("user@test.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void testAuthResponseGettersAndSetters() {
        AuthResponse response = new AuthResponse();
        
        response.setToken("new-token");
        response.setType("Custom");
        response.setId(10L);
        response.setUsername("newuser");
        response.setEmail("new@msgtel.com");
        response.setRole("ADMIN");
        
        assertEquals("new-token", response.getToken());
        assertEquals("Custom", response.getType());
        assertEquals(10L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("new@msgtel.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void testAuthResponseEquality() {
        AuthResponse response1 = new AuthResponse("token", 1L, "user", "user@test.com", "ADMIN");
        AuthResponse response2 = new AuthResponse("token", 1L, "user", "user@test.com", "ADMIN");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testAuthResponseInequality() {
        AuthResponse response1 = new AuthResponse("token1", 1L, "user1", "user1@test.com", "ADMIN");
        AuthResponse response2 = new AuthResponse("token2", 2L, "user2", "user2@test.com", "CUSTOMER");

        assertNotEquals(response1, response2);
    }

    @Test
    void testAuthResponseToString() {
        AuthResponse response = new AuthResponse("jwt-token-xyz", 1L, "testuser", "test@msgtel.com", "ADMIN");
        
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@msgtel.com"));
        assertTrue(toString.contains("ADMIN"));
    }

    @Test
    void testAuthResponseTokenValidation() {
        AuthResponse response = new AuthResponse();
        response.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test");
        
        assertNotNull(response.getToken());
        assertTrue(response.getToken().startsWith("eyJ"));
    }

    @Test
    void testAuthResponseDefaultType() {
        AuthResponse response = new AuthResponse();
        assertEquals("Bearer", response.getType());
    }
}
