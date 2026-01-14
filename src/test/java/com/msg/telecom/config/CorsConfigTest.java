package com.msg.telecom.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    public void testCorsFilter() {
        // Act
        CorsFilter corsFilter = corsConfig.corsFilter();

        // Assert
        assertNotNull(corsFilter);
        // Additional assertions to validate the CORS configuration
    }

    @Test
    public void testCorsFilter_EmptyConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        CorsFilter corsFilter = new CorsFilter(source);
        assertNotNull(corsFilter);
    }

    @Test
    public void testCorsFilter_InvalidPath() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/invalid-path", configuration);

        CorsFilter corsFilter = new CorsFilter(source);
        assertNotNull(corsFilter);
    }
}
