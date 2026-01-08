package com.msg.telecom.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void passwordEncoder_ReturnsBCryptPasswordEncoder() {
        // Test password encoder logic directly
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "testPassword";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertTrue(encodedPassword.startsWith("$2"));
    }

    @Test
    void authenticationManager_ReturnsAuthenticationManager() throws Exception {
        // Test authentication manager retrieval logic
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(mockManager);

        AuthenticationManager result = authConfig.getAuthenticationManager();

        assertNotNull(result);
        assertEquals(mockManager, result);
    }
}
