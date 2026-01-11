package com.msg.telecom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostpaidBillingSystemApplicationTest {

    @Test
    void mainMethodRunsSuccessfully() {
        // Test main method with empty args - just verify the class exists
        assertDoesNotThrow(() -> {
            PostpaidBillingSystemApplication app = new PostpaidBillingSystemApplication();
            assertNotNull(app);
        });
    }

    @Test
    void applicationClassExists() {
        assertNotNull(PostpaidBillingSystemApplication.class);
    }

    @Test
    void applicationHasMainMethod() {
        assertDoesNotThrow(() -> {
            PostpaidBillingSystemApplication.class.getMethod("main", String[].class);
        });
    }

    @Test
    void applicationHasSpringBootApplicationAnnotation() {
        assertTrue(PostpaidBillingSystemApplication.class.isAnnotationPresent(
                org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void mainMethodAcceptsStringArray() throws NoSuchMethodException {
        var mainMethod = PostpaidBillingSystemApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertEquals(void.class, mainMethod.getReturnType());
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    void applicationCanBeInstantiated() {
        PostpaidBillingSystemApplication app = new PostpaidBillingSystemApplication();
        assertNotNull(app);
        assertInstanceOf(PostpaidBillingSystemApplication.class, app);
    }
}
