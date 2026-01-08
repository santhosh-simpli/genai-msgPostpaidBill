package com.msg.telecom.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_WithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void extendsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }

    @Test
    void testCatchException() {
        try {
            throw new ResourceNotFoundException("Caught exception");
        } catch (ResourceNotFoundException e) {
            assertEquals("Caught exception", e.getMessage());
        }
    }

    @Test
    void testExceptionWithEmptyMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void testDifferentResourceTypes() {
        String[] resourceTypes = {"Customer", "Invoice", "Payment", "Service", "UsageRecord", "User"};
        
        for (String resource : resourceTypes) {
            String message = resource + " with id 1 not found";
            ResourceNotFoundException exception = new ResourceNotFoundException(message);
            assertTrue(exception.getMessage().contains(resource));
        }
    }

    @Test
    void testExceptionInheritance() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionStackTrace() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Stack trace test");
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testExceptionToString() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test message");
        String toString = exception.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ResourceNotFoundException"));
    }
}
