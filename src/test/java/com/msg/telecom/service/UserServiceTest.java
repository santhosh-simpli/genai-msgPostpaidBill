package com.msg.telecom.service;

import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.CustomerRepository;
import com.msg.telecom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, customerRepository, passwordEncoder);
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@msgtel.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setRole(UserRole.CUSTOMER);
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAllByOrderByUserIdDesc()).thenReturn(List.of(testUser));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAllByOrderByUserIdDesc();
    }

    @Test
    void getAllUsers_ReturnsEmptyList() {
        when(userRepository.findAllByOrderByUserIdDesc()).thenReturn(Collections.emptyList());
        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findAllByOrderByUserIdDesc();
    }

    @Test
    void getUserById_Found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(1L);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByUsername_Found() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        User result = userService.getUserByUsername("testuser");
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserByUsername("nonexistent"));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void createUser_Success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@msgtel.com");
        newUser.setPasswordHash("rawPassword");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@msgtel.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User created = userService.createUser(newUser);
        assertEquals("newuser", created.getUsername());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UsernameExists() {
        User user = new User();
        user.setUsername("existinguser");
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertTrue(ex.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailExists() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@msgtel.com");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@msgtel.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));
        assertTrue(ex.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success_AllFields() {
        User updateDetails = new User();
        updateDetails.setUsername("updateduser");
        updateDetails.setEmail("updated@msgtel.com");
        updateDetails.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@msgtel.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

        User updated = userService.updateUser(1L, updateDetails);
        assertNotNull(updated);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_SameUsername() {
        User updateDetails = new User();
        updateDetails.setUsername("testuser"); // Same as existing
        updateDetails.setEmail("newemail@msgtel.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@msgtel.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

        User updated = userService.updateUser(1L, updateDetails);
        assertNotNull(updated);
        verify(userRepository, never()).existsByUsername(anyString());
    }

    @Test
    void updateUser_SameEmail() {
        User updateDetails = new User();
        updateDetails.setUsername("newusername");
        updateDetails.setEmail("test@msgtel.com"); // Same as existing

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

        User updated = userService.updateUser(1L, updateDetails);
        assertNotNull(updated);
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void updateUser_UsernameAlreadyExists() {
        User updateDetails = new User();
        updateDetails.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDetails));
        assertTrue(ex.getMessage().contains("Username already exists"));
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        User updateDetails = new User();
        updateDetails.setUsername(null);
        updateDetails.setEmail("existing@msgtel.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@msgtel.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updateDetails));
        assertTrue(ex.getMessage().contains("Email already exists"));
    }

    @Test
    void updateUser_RoleOnly() {
        User updateDetails = new User();
        updateDetails.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

        User updated = userService.updateUser(1L, updateDetails);
        assertNotNull(updated);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NullRole() {
        User updateDetails = new User();
        updateDetails.setUsername(null);
        updateDetails.setEmail(null);
        updateDetails.setRole(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

        User updated = userService.updateUser(1L, updateDetails);
        assertNotNull(updated);
    }

    @Test
    void updateUser_NotFound() {
        User updateDetails = new User();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, updateDetails));
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NonExistent() {
        doNothing().when(userRepository).deleteById(999L);

        assertDoesNotThrow(() -> userService.deleteUser(999L));
        verify(userRepository, times(1)).deleteById(999L);
    }

    @Test
    void deleteUser_WithDependencies() {
        doThrow(new RuntimeException("Cannot delete user with active dependencies"))
                .when(userRepository).deleteById(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        assertTrue(ex.getMessage().contains("Cannot delete user with active dependencies"));
        verify(userRepository, times(1)).deleteById(1L);
    }
}
