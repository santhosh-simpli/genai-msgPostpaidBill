package com.msg.telecom.controller;

import com.msg.telecom.dto.AuthResponse;
import com.msg.telecom.dto.LoginRequest;
import com.msg.telecom.dto.RegisterRequest;
import com.msg.telecom.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController authController;

    private AuthResponse createAuthResponse(String token, Long id, String username, String email, String role) {
        return new AuthResponse(token, id, username, email, role);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        
        AuthResponse expectedResponse = createAuthResponse("jwt-token-123", 1L, "testuser", "test@msgtel.com", "CUSTOMER");
        when(authService.login(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token-123", response.getBody().getToken());
        assertEquals("Bearer", response.getBody().getType());
    }

    @Test
    void login_WithValidCredentials_CallsAuthService() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");
        
        AuthResponse expectedResponse = createAuthResponse("admin-token", 1L, "admin", "admin@msgtel.com", "ADMIN");
        when(authService.login(request)).thenReturn(expectedResponse);

        authController.login(request);
        
        verify(authService, times(1)).login(request);
    }

    @Test
    void register_Success_ReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@msgtel.com");
        request.setPassword("password123");
        
        AuthResponse expectedResponse = createAuthResponse("new-user-token", 2L, "newuser", "newuser@msgtel.com", "CUSTOMER");
        when(authService.register(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new-user-token", response.getBody().getToken());
    }

    @Test
    void register_WithRole_ReturnsAuthResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@msgtel.com");
        request.setPassword("adminpass");
        request.setRole("ADMIN");
        
        AuthResponse expectedResponse = createAuthResponse("admin-new-token", 3L, "adminuser", "admin@msgtel.com", "ADMIN");
        when(authService.register(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void register_CallsAuthService() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testregister");
        request.setEmail("test@msgtel.com");
        request.setPassword("testpass");
        
        AuthResponse expectedResponse = createAuthResponse("test-token", 4L, "testregister", "test@msgtel.com", "CUSTOMER");
        when(authService.register(request)).thenReturn(expectedResponse);

        authController.register(request);
        
        verify(authService, times(1)).register(request);
    }

    @Test
    void login_ReturnsResponseWithBearerType() {
        LoginRequest request = new LoginRequest();
        request.setUsername("bearer");
        request.setPassword("password");
        
        AuthResponse expectedResponse = createAuthResponse("token", 5L, "bearer", "bearer@msgtel.com", "CUSTOMER");
        when(authService.login(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);
        
        assertEquals("Bearer", response.getBody().getType());
    }

    @Test
    void register_ReturnsResponseWithBearerType() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newbearer");
        request.setEmail("bearer@msgtel.com");
        request.setPassword("password");
        
        AuthResponse expectedResponse = createAuthResponse("token", 6L, "newbearer", "bearer@msgtel.com", "CUSTOMER");
        when(authService.register(request)).thenReturn(expectedResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);
        
        assertEquals("Bearer", response.getBody().getType());
    }

    @Test
    void login_WithDifferentUsers_ReturnsCorrectTokens() {
        LoginRequest request1 = new LoginRequest();
        request1.setUsername("user1");
        request1.setPassword("pass1");
        
        LoginRequest request2 = new LoginRequest();
        request2.setUsername("user2");
        request2.setPassword("pass2");
        
        AuthResponse response1 = createAuthResponse("token1", 1L, "user1", "user1@msgtel.com", "CUSTOMER");
        AuthResponse response2 = createAuthResponse("token2", 2L, "user2", "user2@msgtel.com", "CUSTOMER");
        
        when(authService.login(request1)).thenReturn(response1);
        when(authService.login(request2)).thenReturn(response2);

        ResponseEntity<AuthResponse> result1 = authController.login(request1);
        ResponseEntity<AuthResponse> result2 = authController.login(request2);
        
        assertEquals("token1", result1.getBody().getToken());
        assertEquals("token2", result2.getBody().getToken());
    }
}
