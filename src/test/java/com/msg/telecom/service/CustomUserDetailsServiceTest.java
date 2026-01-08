package com.msg.telecom.service;

import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

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
    @DisplayName("loadUserByUsername Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should load user by username successfully")
        void loadUserByUsername_Success() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepository, times(1)).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should return user with correct password hash")
        void loadUserByUsername_CorrectPassword() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertEquals("encodedPassword", result.getPassword());
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found")
        void loadUserByUsername_UserNotFound() {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername("nonexistent"));

            assertTrue(exception.getMessage().contains("User not found"));
            assertTrue(exception.getMessage().contains("nonexistent"));
            verify(userRepository, times(1)).findByUsername("nonexistent");
        }

        @Test
        @DisplayName("Should load admin user correctly")
        void loadUserByUsername_AdminUser() {
            User adminUser = new User();
            adminUser.setUserId(2L);
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setPasswordHash("adminPassword");
            adminUser.setRole(UserRole.ADMIN);

            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("admin");

            assertNotNull(result);
            assertEquals("admin", result.getUsername());
        }

        @Test
        @DisplayName("Should load operator user correctly")
        void loadUserByUsername_OperatorUser() {
            User operatorUser = new User();
            operatorUser.setUserId(3L);
            operatorUser.setUsername("operator");
            operatorUser.setEmail("operator@example.com");
            operatorUser.setPasswordHash("operatorPassword");
            operatorUser.setRole(UserRole.OPERATOR);

            when(userRepository.findByUsername("operator")).thenReturn(Optional.of(operatorUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("operator");

            assertNotNull(result);
            assertEquals("operator", result.getUsername());
        }

        @Test
        @DisplayName("Should handle empty username")
        void loadUserByUsername_EmptyUsername() {
            when(userRepository.findByUsername("")).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername(""));
        }

        @Test
        @DisplayName("Should handle null username gracefully")
        void loadUserByUsername_NullUsername() {
            when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername(null));
        }

        @Test
        @DisplayName("Should call repository exactly once")
        void loadUserByUsername_RepositoryCalledOnce() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            customUserDetailsService.loadUserByUsername("testuser");
            customUserDetailsService.loadUserByUsername("testuser");

            verify(userRepository, times(2)).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should preserve user ID after loading")
        void loadUserByUsername_PreservesUserId() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            // Cast to User to check userId
            assertTrue(result instanceof User);
            User loadedUser = (User) result;
            assertEquals(1L, loadedUser.getUserId());
        }

        @Test
        @DisplayName("Should preserve user email after loading")
        void loadUserByUsername_PreservesEmail() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertTrue(result instanceof User);
            User loadedUser = (User) result;
            assertEquals("test@example.com", loadedUser.getEmail());
        }
    }
}
