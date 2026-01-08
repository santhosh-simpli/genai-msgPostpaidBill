package com.msg.telecom.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testRegisterRequestNoArgsConstructor() {
        RegisterRequest request = new RegisterRequest();
        
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertEquals("CUSTOMER", request.getRole()); // Default value
    }

    @Test
    void testRegisterRequestAllArgsConstructor() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123", "ADMIN");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("ADMIN", request.getRole());
    }

    @Test
    void testRegisterRequestGettersAndSetters() {
        RegisterRequest request = new RegisterRequest();
        
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("securepass");
        request.setRole("CUSTOMER");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("new@example.com", request.getEmail());
        assertEquals("securepass", request.getPassword());
        assertEquals("CUSTOMER", request.getRole());
    }

    @Test
    void testRegisterRequestEquality() {
        RegisterRequest request1 = new RegisterRequest("user", "user@test.com", "pass123", "ADMIN");
        RegisterRequest request2 = new RegisterRequest("user", "user@test.com", "pass123", "ADMIN");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testRegisterRequestInequality() {
        RegisterRequest request1 = new RegisterRequest("user1", "user1@test.com", "pass1", "ADMIN");
        RegisterRequest request2 = new RegisterRequest("user2", "user2@test.com", "pass2", "CUSTOMER");

        assertNotEquals(request1, request2);
    }

    @Test
    void testRegisterRequestToString() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password", "ADMIN");
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
    }

    @Test
    void testValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest("validuser", "valid@example.com", "password123", "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRegisterRequestBlankUsername() {
        RegisterRequest request = new RegisterRequest("", "valid@example.com", "password123", "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRegisterRequestInvalidEmail() {
        RegisterRequest request = new RegisterRequest("validuser", "invalid-email", "password123", "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRegisterRequestShortPassword() {
        RegisterRequest request = new RegisterRequest("validuser", "valid@example.com", "12345", "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRegisterRequestShortUsername() {
        RegisterRequest request = new RegisterRequest("ab", "valid@example.com", "password123", "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testRegisterRequestNullPassword() {
        RegisterRequest request = new RegisterRequest("validuser", "valid@example.com", null, "CUSTOMER");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}
