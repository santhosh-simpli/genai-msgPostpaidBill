package com.msg.telecom.service;

import com.msg.telecom.dto.AuthResponse;
import com.msg.telecom.dto.LoginRequest;
import com.msg.telecom.dto.RegisterRequest;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.UserRepository;
import com.msg.telecom.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setRole(UserRole.CUSTOMER);
        
        loginRequest = new LoginRequest("testuser", "password123");
        registerRequest = new RegisterRequest("newuser", "new@example.com", "password123", "CUSTOMER");
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        AuthResponse response = authService.login(loginRequest);
        
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtTokenProvider, times(1)).generateToken(any());
    }

    @Test
    void login_UserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_AuthenticationFailed() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Bad credentials"));
        
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void register_Success() {
        User newUser = new User();
        newUser.setUserId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setRole(UserRole.CUSTOMER);
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        
        AuthResponse response = authService.register(registerRequest);
        
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("newuser", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void register_UsernameExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertTrue(ex.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_AdminRole() {
        RegisterRequest adminRequest = new RegisterRequest("admin", "admin@example.com", "password123", "ADMIN");
        User adminUser = new User();
        adminUser.setUserId(3L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(UserRole.ADMIN);
        
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        
        AuthResponse response = authService.register(adminRequest);
        
        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_AuthenticationAfterRegistration() {
        User newUser = new User();
        newUser.setUserId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setRole(UserRole.CUSTOMER);
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
        
        authService.register(registerRequest);
        
        // Verify that authentication happens after user is saved
        verify(userRepository, times(1)).save(any(User.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("user", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mockToken");
        User mockUser = User.builder()
                .username("user")
                .passwordHash("hashedPassword")
                .email("user@example.com")
                .role(UserRole.CUSTOMER)
                .build();
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        assertEquals("user", response.getUsername());
    }

    @Test
    void testLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("user", "password");
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));
        when(jwtTokenProvider.generateToken(any())).thenReturn("mockToken");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }
}
