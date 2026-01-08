package com.msg.telecom.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    @Test
    void handleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("Not Found", response.getBody().getError());
    }

    @Test
    void handleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate resource");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Duplicate resource", response.getBody().getMessage());
        assertEquals("Conflict", response.getBody().getError());
    }

    @Test
    void handleValidationExceptions() {
        FieldError fieldError = new FieldError("object", "field", "error message");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertTrue(response.getBody().containsKey("errors"));
    }

    @Test
    void handleGlobalException() {
        Exception ex = new Exception("Generic error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Generic error", response.getBody().getMessage());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void handleResourceNotFoundException_VerifyTimestampAndPath() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertNotNull(response.getBody().getTimestamp());
        assertEquals("uri=/test", response.getBody().getPath());
    }

    @Test
    void handleDuplicateResourceException_VerifyTimestampAndPath() {
        DuplicateResourceException ex = new DuplicateResourceException("User already exists");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, webRequest);

        assertNotNull(response.getBody().getTimestamp());
        assertEquals("uri=/test", response.getBody().getPath());
    }

    @Test
    void handleGlobalException_VerifyTimestampAndPath() {
        Exception ex = new RuntimeException("Unexpected error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertNotNull(response.getBody().getTimestamp());
        assertEquals("uri=/test", response.getBody().getPath());
    }

    @Test
    void handleValidationExceptions_MultipleErrors() {
        FieldError fieldError1 = new FieldError("user", "username", "Username is required");
        FieldError fieldError2 = new FieldError("user", "email", "Email is invalid");
        FieldError fieldError3 = new FieldError("user", "password", "Password too short");
        
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2, fieldError3));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(3, errors.size());
        assertEquals("Username is required", errors.get("username"));
        assertEquals("Email is invalid", errors.get("email"));
        assertEquals("Password too short", errors.get("password"));
    }

    @Test
    void handleValidationExceptions_VerifyAllResponseFields() {
        FieldError fieldError = new FieldError("object", "field", "error message");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertNotNull(response.getBody().get("timestamp"));
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed", response.getBody().get("error"));
        assertEquals("uri=/test", response.getBody().get("path"));
    }

    @Test
    void handleResourceNotFoundException_CustomerNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer with id 123 not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Customer"));
        assertTrue(response.getBody().getMessage().contains("123"));
    }

    @Test
    void handleResourceNotFoundException_InvoiceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Invoice with id 456 not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invoice"));
    }

    @Test
    void handleDuplicateResourceException_DuplicateUsername() {
        DuplicateResourceException ex = new DuplicateResourceException("User with username 'johndoe' already exists");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("johndoe"));
    }

    @Test
    void handleDuplicateResourceException_DuplicateEmail() {
        DuplicateResourceException ex = new DuplicateResourceException("User with email 'test@test.com' already exists");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(ex, webRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("email"));
    }

    @Test
    void handleGlobalException_NullPointerException() {
        NullPointerException ex = new NullPointerException("Null value encountered");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Null value encountered", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_IllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_WithDifferentPath() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/customers");
        Exception ex = new Exception("Error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals("uri=/api/customers", response.getBody().getPath());
    }
}
