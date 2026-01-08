package com.msg.telecom.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testLoginRequestNoArgsConstructor() {
        LoginRequest request = new LoginRequest();
        
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testLoginRequestAllArgsConstructor() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testLoginRequestGettersAndSetters() {
        LoginRequest request = new LoginRequest();
        
        request.setUsername("newuser");
        request.setPassword("newpassword");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("newpassword", request.getPassword());
    }

    @Test
    void testLoginRequestEquality() {
        LoginRequest request1 = new LoginRequest("user", "pass123");
        LoginRequest request2 = new LoginRequest("user", "pass123");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testLoginRequestInequality() {
        LoginRequest request1 = new LoginRequest("user1", "pass1");
        LoginRequest request2 = new LoginRequest("user2", "pass2");

        assertNotEquals(request1, request2);
    }

    @Test
    void testLoginRequestToString() {
        LoginRequest request = new LoginRequest("testuser", "password");
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("validuser", "validpassword");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testLoginRequestBlankUsername() {
        LoginRequest request = new LoginRequest("", "validpassword");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testLoginRequestBlankPassword() {
        LoginRequest request = new LoginRequest("validuser", "");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testLoginRequestNullUsername() {
        LoginRequest request = new LoginRequest(null, "validpassword");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testLoginRequestNullPassword() {
        LoginRequest request = new LoginRequest("validuser", null);
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testLoginRequestBothNull() {
        LoginRequest request = new LoginRequest(null, null);
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }
}
