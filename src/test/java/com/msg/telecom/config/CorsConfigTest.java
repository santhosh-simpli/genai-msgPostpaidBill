package com.msg.telecom.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CorsConfigTest {

    @Autowired
    private CorsConfig corsConfig;

    @Test
    public void testCorsFilter() {
        // Act
        CorsFilter corsFilter = corsConfig.corsFilter();

        // Assert
        assertNotNull(corsFilter);
        // Additional assertions to validate the CORS configuration
    }
}