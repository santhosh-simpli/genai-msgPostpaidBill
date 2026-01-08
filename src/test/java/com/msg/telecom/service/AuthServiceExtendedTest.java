package com.msg.telecom.service;

import com.msg.telecom.dto.AuthResponse;
import com.msg.telecom.dto.LoginRequest;
import com.msg.telecom.dto.RegisterRequest;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.UserRepository;
import com.msg.telecom.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthService Extended Tests")
class AuthServiceExtendedTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setRole(UserRole.CUSTOMER);
    }

    @Nested
    @DisplayName("login Extended Tests")
    class LoginExtendedTests {

        @Test
        @DisplayName("Should return correct user ID in response")
        void login_CorrectUserId() {
            LoginRequest request = new LoginRequest("testuser", "password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            AuthResponse response = authService.login(request);

            assertEquals(1L, response.getId());
        }

        @Test
        @DisplayName("Should login admin user successfully")
        void login_AdminUser() {
            User adminUser = new User();
            adminUser.setUserId(2L);
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setRole(UserRole.ADMIN);

            LoginRequest request = new LoginRequest("admin", "adminPass");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("admin-token");
            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

            AuthResponse response = authService.login(request);

            assertEquals("ADMIN", response.getRole());
            assertEquals("admin", response.getUsername());
        }

        @Test
        @DisplayName("Should login operator user successfully")
        void login_OperatorUser() {
            User operatorUser = new User();
            operatorUser.setUserId(3L);
            operatorUser.setUsername("operator");
            operatorUser.setEmail("operator@example.com");
            operatorUser.setRole(UserRole.OPERATOR);

            LoginRequest request = new LoginRequest("operator", "operatorPass");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("operator-token");
            when(userRepository.findByUsername("operator")).thenReturn(Optional.of(operatorUser));

            AuthResponse response = authService.login(request);

            assertEquals("OPERATOR", response.getRole());
        }

        @Test
        @DisplayName("Should throw exception for bad credentials")
        void login_BadCredentials() {
            LoginRequest request = new LoginRequest("testuser", "wrongpassword");

            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThrows(BadCredentialsException.class, () -> authService.login(request));
            verify(jwtTokenProvider, never()).generateToken(any());
        }

        @Test
        @DisplayName("Should verify authentication manager called")
        void login_AuthenticationManagerCalled() {
            LoginRequest request = new LoginRequest("testuser", "password");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            authService.login(request);

            verify(authenticationManager, times(1)).authenticate(any());
        }

        @Test
        @DisplayName("Should return email in response")
        void login_EmailInResponse() {
            LoginRequest request = new LoginRequest("testuser", "password");

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            AuthResponse response = authService.login(request);

            assertEquals("test@example.com", response.getEmail());
        }
    }

    @Nested
    @DisplayName("register Extended Tests")
    class RegisterExtendedTests {

        @Test
        @DisplayName("Should register admin user")
        void register_AdminUser() {
            RegisterRequest request = new RegisterRequest("newadmin", "admin@example.com", "password", "ADMIN");

            User savedUser = new User();
            savedUser.setUserId(10L);
            savedUser.setUsername("newadmin");
            savedUser.setEmail("admin@example.com");
            savedUser.setRole(UserRole.ADMIN);

            when(userRepository.existsByUsername("newadmin")).thenReturn(false);
            when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("admin-token");

            AuthResponse response = authService.register(request);

            assertEquals("ADMIN", response.getRole());
            assertEquals("newadmin", response.getUsername());
        }

        @Test
        @DisplayName("Should register operator user")
        void register_OperatorUser() {
            RegisterRequest request = new RegisterRequest("newoperator", "operator@example.com", "password",
                    "OPERATOR");

            User savedUser = new User();
            savedUser.setUserId(11L);
            savedUser.setUsername("newoperator");
            savedUser.setEmail("operator@example.com");
            savedUser.setRole(UserRole.OPERATOR);

            when(userRepository.existsByUsername("newoperator")).thenReturn(false);
            when(userRepository.existsByEmail("operator@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("operator-token");

            AuthResponse response = authService.register(request);

            assertEquals("OPERATOR", response.getRole());
        }

        @Test
        @DisplayName("Should throw exception for duplicate username")
        void register_DuplicateUsername() {
            RegisterRequest request = new RegisterRequest("existinguser", "new@example.com", "password", "CUSTOMER");

            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.register(request));

            assertTrue(exception.getMessage().contains("Username already exists"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception for duplicate email")
        void register_DuplicateEmail() {
            RegisterRequest request = new RegisterRequest("newuser", "existing@example.com", "password", "CUSTOMER");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.register(request));

            assertTrue(exception.getMessage().contains("Email already exists"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should encode password before saving")
        void register_PasswordEncoded() {
            RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "plainPassword", "CUSTOMER");

            User savedUser = new User();
            savedUser.setUserId(12L);
            savedUser.setUsername("newuser");
            savedUser.setEmail("new@example.com");
            savedUser.setRole(UserRole.CUSTOMER);

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

            authService.register(request);

            verify(passwordEncoder, times(1)).encode("plainPassword");
        }

        @Test
        @DisplayName("Should generate token after successful registration")
        void register_TokenGenerated() {
            RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password", "CUSTOMER");

            User savedUser = new User();
            savedUser.setUserId(13L);
            savedUser.setUsername("newuser");
            savedUser.setEmail("new@example.com");
            savedUser.setRole(UserRole.CUSTOMER);

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("new-user-token");

            AuthResponse response = authService.register(request);

            assertEquals("new-user-token", response.getToken());
            verify(jwtTokenProvider, times(1)).generateToken(authentication);
        }

        @Test
        @DisplayName("Should return user ID in response after registration")
        void register_UserIdInResponse() {
            RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "password", "CUSTOMER");

            User savedUser = new User();
            savedUser.setUserId(99L);
            savedUser.setUsername("newuser");
            savedUser.setEmail("new@example.com");
            savedUser.setRole(UserRole.CUSTOMER);

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

            AuthResponse response = authService.register(request);

            assertEquals(99L, response.getId());
        }
    }
}
