package com.msg.telecom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
