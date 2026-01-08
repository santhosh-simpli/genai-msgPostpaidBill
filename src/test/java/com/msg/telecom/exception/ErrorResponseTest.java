package com.msg.telecom.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertNull(errorResponse.getTimestamp());
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getError());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(timestamp, 404, "Not Found", "Resource not found", "/api/test");
        
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals("/api/test", errorResponse.getPath());
    }

    @Test
    void testGettersAndSetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 15, 10, 30);
        
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(500);
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("Something went wrong");
        errorResponse.setPath("/api/error");
        
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("Something went wrong", errorResponse.getMessage());
        assertEquals("/api/error", errorResponse.getPath());
    }

    @Test
    void testEquality() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 15, 10, 30);
        
        ErrorResponse error1 = new ErrorResponse(timestamp, 400, "Bad Request", "Invalid input", "/api/test");
        ErrorResponse error2 = new ErrorResponse(timestamp, 400, "Bad Request", "Invalid input", "/api/test");
        
        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void testInequality() {
        LocalDateTime timestamp = LocalDateTime.now();
        
        ErrorResponse error1 = new ErrorResponse(timestamp, 400, "Bad Request", "Invalid input", "/api/test");
        ErrorResponse error2 = new ErrorResponse(timestamp, 500, "Internal Error", "Server error", "/api/other");
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testToString() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 15, 10, 30);
        ErrorResponse errorResponse = new ErrorResponse(timestamp, 404, "Not Found", "Resource not found", "/api/test");
        
        String toString = errorResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
        assertTrue(toString.contains("/api/test"));
    }

    @Test
    void testDifferentStatusCodes() {
        int[] statusCodes = {200, 201, 204, 400, 401, 403, 404, 405, 409, 422, 500, 502, 503};
        
        for (int status : statusCodes) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatus(status);
            assertEquals(status, errorResponse.getStatus());
        }
    }

    @Test
    void testDifferentErrorTypes() {
        String[] errorTypes = {"Bad Request", "Unauthorized", "Forbidden", "Not Found", 
                              "Method Not Allowed", "Conflict", "Internal Server Error", 
                              "Service Unavailable", "Validation Error"};
        
        for (String error : errorTypes) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(error);
            assertEquals(error, errorResponse.getError());
        }
    }

    @Test
    void testDifferentPaths() {
        String[] paths = {"/api/customers", "/api/invoices", "/api/payments", 
                         "/api/services", "/api/users", "/api/usage-records",
                         "/api/auth/login", "/api/auth/register"};
        
        for (String path : paths) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setPath(path);
            assertEquals(path, errorResponse.getPath());
        }
    }

    @Test
    void testNullValues() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(null);
        errorResponse.setError(null);
        errorResponse.setMessage(null);
        errorResponse.setPath(null);
        
        assertNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getError());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
    }

    @Test
    void testEmptyStrings() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("");
        errorResponse.setMessage("");
        errorResponse.setPath("");
        
        assertEquals("", errorResponse.getError());
        assertEquals("", errorResponse.getMessage());
        assertEquals("", errorResponse.getPath());
    }

    @Test
    void testLongMessage() {
        String longMessage = "A".repeat(1000);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(longMessage);
        
        assertEquals(1000, errorResponse.getMessage().length());
        assertEquals(longMessage, errorResponse.getMessage());
    }
}
