package com.msg.telecom.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateResourceExceptionTest {

    @Test
    void constructor_WithMessage() {
        String message = "Duplicate resource";
        DuplicateResourceException exception = new DuplicateResourceException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void extendsRuntimeException() {
        DuplicateResourceException exception = new DuplicateResourceException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowException() {
        assertThrows(DuplicateResourceException.class, () -> {
            throw new DuplicateResourceException("Test exception");
        });
    }

    @Test
    void testCatchException() {
        try {
            throw new DuplicateResourceException("Caught exception");
        } catch (DuplicateResourceException e) {
            assertEquals("Caught exception", e.getMessage());
        }
    }

    @Test
    void testExceptionWithEmptyMessage() {
        DuplicateResourceException exception = new DuplicateResourceException("");
        assertEquals("", exception.getMessage());
    }

    @Test
    void testExceptionWithNullMessage() {
        DuplicateResourceException exception = new DuplicateResourceException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void testDuplicateUserMessage() {
        String message = "User with username 'johndoe' already exists";
        DuplicateResourceException exception = new DuplicateResourceException(message);
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testDuplicateEmailMessage() {
        String message = "User with email 'test@test.com' already exists";
        DuplicateResourceException exception = new DuplicateResourceException(message);
        assertTrue(exception.getMessage().contains("email"));
    }

    @Test
    void testExceptionInheritance() {
        DuplicateResourceException exception = new DuplicateResourceException("Test");
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionStackTrace() {
        DuplicateResourceException exception = new DuplicateResourceException("Stack trace test");
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testExceptionToString() {
        DuplicateResourceException exception = new DuplicateResourceException("Test message");
        String toString = exception.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DuplicateResourceException"));
    }
}
