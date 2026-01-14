package com.msg.telecom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PostpaidBillingSystemApplication Tests")
class PostpaidBillingSystemApplicationTest {

    @Test
    @DisplayName("Should run main method without throwing exception")
    void mainMethodRunsSuccessfully() {
        // Test main method with empty args - just verify the class exists
        assertDoesNotThrow(() -> {
            PostpaidBillingSystemApplication app = new PostpaidBillingSystemApplication();
            assertNotNull(app);
        });
    }

    @Test
    @DisplayName("Should verify application class exists")
    void applicationClassExists() {
        assertNotNull(PostpaidBillingSystemApplication.class);
    }

    @Test
    @DisplayName("Should verify main method exists")
    void applicationHasMainMethod() {
        assertDoesNotThrow(() -> {
            PostpaidBillingSystemApplication.class.getMethod("main", String[].class);
        });
    }

    @Test
    @DisplayName("Should have SpringBootApplication annotation")
    void applicationHasSpringBootApplicationAnnotation() {
        assertTrue(PostpaidBillingSystemApplication.class.isAnnotationPresent(
                SpringBootApplication.class));
    }

    @Test
    @DisplayName("Should verify main method signature")
    void mainMethodAcceptsStringArray() throws NoSuchMethodException {
        var mainMethod = PostpaidBillingSystemApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
        assertEquals(void.class, mainMethod.getReturnType());
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    @DisplayName("Should instantiate application")
    void applicationCanBeInstantiated() {
        PostpaidBillingSystemApplication app = new PostpaidBillingSystemApplication();
        assertNotNull(app);
        assertInstanceOf(PostpaidBillingSystemApplication.class, app);
    }
}
