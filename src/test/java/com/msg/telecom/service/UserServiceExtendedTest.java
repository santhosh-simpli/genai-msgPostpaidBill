package com.msg.telecom.service;

import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.CustomerRepository;
import com.msg.telecom.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserService Extended Tests")
class UserServiceExtendedTest {

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

    @Nested
    @DisplayName("getAllUsers Extended Tests")
    class GetAllUsersExtendedTests {

        @Test
        @DisplayName("Should return users with different roles")
        void getAllUsers_DifferentRoles() {
            User adminUser = new User();
            adminUser.setUserId(2L);
            adminUser.setUsername("admin");
            adminUser.setRole(UserRole.ADMIN);

            User operatorUser = new User();
            operatorUser.setUserId(3L);
            operatorUser.setUsername("operator");
            operatorUser.setRole(UserRole.OPERATOR);

            when(userRepository.findAllByOrderByUserIdDesc())
                    .thenReturn(Arrays.asList(testUser, adminUser, operatorUser));

            List<User> result = userService.getAllUsers();

            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(u -> u.getRole() == UserRole.CUSTOMER));
            assertTrue(result.stream().anyMatch(u -> u.getRole() == UserRole.ADMIN));
            assertTrue(result.stream().anyMatch(u -> u.getRole() == UserRole.OPERATOR));
        }

        @Test
        @DisplayName("Should return many users")
        void getAllUsers_ManyUsers() {
            List<User> manyUsers = Arrays.asList(
                    testUser,
                    new User(), new User(), new User(), new User(),
                    new User(), new User(), new User(), new User(), new User());

            when(userRepository.findAllByOrderByUserIdDesc()).thenReturn(manyUsers);

            List<User> result = userService.getAllUsers();

            assertEquals(10, result.size());
        }
    }

    @Nested
    @DisplayName("getUserById Extended Tests")
    class GetUserByIdExtendedTests {

        @Test
        @DisplayName("Should return user with all fields")
        void getUserById_AllFields() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            User result = userService.getUserById(1L);

            assertEquals(1L, result.getUserId());
            assertEquals("testuser", result.getUsername());
            assertEquals("test@msgtel.com", result.getEmail());
            assertEquals(UserRole.CUSTOMER, result.getRole());
        }

        @Test
        @DisplayName("Should throw exception with user ID in message")
        void getUserById_NotFound_MessageContainsId() {
            when(userRepository.findById(567L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.getUserById(567L));

            assertTrue(exception.getMessage().contains("567"));
        }
    }

    @Nested
    @DisplayName("getUserByUsername Extended Tests")
    class GetUserByUsernameExtendedTests {

        @Test
        @DisplayName("Should find admin by username")
        void getUserByUsername_Admin() {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setRole(UserRole.ADMIN);

            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

            User result = userService.getUserByUsername("admin");

            assertEquals("admin", result.getUsername());
            assertEquals(UserRole.ADMIN, result.getRole());
        }

        @Test
        @DisplayName("Should throw exception with username in message")
        void getUserByUsername_NotFound_MessageContainsUsername() {
            when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.getUserByUsername("unknownuser"));

            assertTrue(exception.getMessage().contains("unknownuser"));
        }

        @Test
        @DisplayName("Should handle special characters in username")
        void getUserByUsername_SpecialCharacters() {
            when(userRepository.findByUsername("user@123")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> userService.getUserByUsername("user@123"));
        }
    }

    @Nested
    @DisplayName("updateUser Extended Tests")
    class UpdateUserExtendedTests {

        @Test
        @DisplayName("Should update username only")
        void updateUser_UsernameOnly() {
            User updateDetails = new User();
            updateDetails.setUsername("newusername");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("newusername")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            assertEquals("newusername", result.getUsername());
        }

        @Test
        @DisplayName("Should update email only")
        void updateUser_EmailOnly() {
            User updateDetails = new User();
            updateDetails.setEmail("newemail@msgtel.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("newemail@msgtel.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            assertEquals("newemail@msgtel.com", result.getEmail());
        }

        @Test
        @DisplayName("Should update role only")
        void updateUser_RoleOnly() {
            User updateDetails = new User();
            updateDetails.setRole(UserRole.ADMIN);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            assertEquals(UserRole.ADMIN, result.getRole());
        }

        @Test
        @DisplayName("Should throw exception when new username already exists")
        void updateUser_UsernameExists() {
            User updateDetails = new User();
            updateDetails.setUsername("existinguser");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.updateUser(1L, updateDetails));

            assertTrue(exception.getMessage().contains("Username already exists"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when new email already exists")
        void updateUser_EmailExists() {
            User updateDetails = new User();
            updateDetails.setEmail("existing@msgtel.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("existing@msgtel.com")).thenReturn(true);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> userService.updateUser(1L, updateDetails));

            assertTrue(exception.getMessage().contains("Email already exists"));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should not check username if not changed")
        void updateUser_SameUsername() {
            User updateDetails = new User();
            updateDetails.setUsername("testuser"); // Same as existing
            updateDetails.setRole(UserRole.ADMIN);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            verify(userRepository, never()).existsByUsername(any());
            assertEquals(UserRole.ADMIN, result.getRole());
        }

        @Test
        @DisplayName("Should not check email if not changed")
        void updateUser_SameEmail() {
            User updateDetails = new User();
            updateDetails.setEmail("test@msgtel.com"); // Same as existing
            updateDetails.setRole(UserRole.OPERATOR);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            verify(userRepository, never()).existsByEmail(any());
            assertEquals(UserRole.OPERATOR, result.getRole());
        }

        @Test
        @DisplayName("Should update all fields at once")
        void updateUser_AllFields() {
            User updateDetails = new User();
            updateDetails.setUsername("brandnewuser");
            updateDetails.setEmail("brandnew@msgtel.com");
            updateDetails.setRole(UserRole.ADMIN);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("brandnewuser")).thenReturn(false);
            when(userRepository.existsByEmail("brandnew@msgtel.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Collections.emptyList());

            User result = userService.updateUser(1L, updateDetails);

            assertEquals("brandnewuser", result.getUsername());
            assertEquals("brandnew@msgtel.com", result.getEmail());
            assertEquals(UserRole.ADMIN, result.getRole());
        }

        @Test
        @DisplayName("Should throw exception for non-existent user")
        void updateUser_UserNotFound() {
            User updateDetails = new User();
            updateDetails.setUsername("newuser");

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> userService.updateUser(999L, updateDetails));
        }
    }

    @Nested
    @DisplayName("deleteUser Extended Tests")
    class DeleteUserExtendedTests {

        @Test
        @DisplayName("Should delete existing user")
        void deleteUser_Existing() {
            doNothing().when(userRepository).deleteById(1L);

            assertDoesNotThrow(() -> userService.deleteUser(1L));

            verify(userRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should handle deleting non-existent user")
        void deleteUser_NonExistent() {
            doNothing().when(userRepository).deleteById(9999L);

            assertDoesNotThrow(() -> userService.deleteUser(9999L));
        }

        @Test
        @DisplayName("Should call repository with correct ID")
        void deleteUser_CorrectId() {
            doNothing().when(userRepository).deleteById(42L);

            userService.deleteUser(42L);

            verify(userRepository).deleteById(42L);
        }
    }

    @Nested
    @DisplayName("createUser Extended Tests")
    class CreateUserExtendedTests {

        @Test
        @DisplayName("Should create admin user")
        void createUser_Admin() {
            User newUser = new User();
            newUser.setUsername("newadmin");
            newUser.setEmail("admin@test.com");
            newUser.setPasswordHash("password");
            newUser.setRole(UserRole.ADMIN);

            when(userRepository.existsByUsername("newadmin")).thenReturn(false);
            when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setUserId(100L);
                return saved;
            });

            User result = userService.createUser(newUser);

            assertEquals(UserRole.ADMIN, result.getRole());
            assertEquals("newadmin", result.getUsername());
        }

        @Test
        @DisplayName("Should create operator user")
        void createUser_Operator() {
            User newUser = new User();
            newUser.setUsername("newoperator");
            newUser.setEmail("operator@test.com");
            newUser.setPasswordHash("password");
            newUser.setRole(UserRole.OPERATOR);

            when(userRepository.existsByUsername("newoperator")).thenReturn(false);
            when(userRepository.existsByEmail("operator@test.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            User result = userService.createUser(newUser);

            assertEquals(UserRole.OPERATOR, result.getRole());
        }

        @Test
        @DisplayName("Should encode password on create")
        void createUser_EncodesPassword() {
            User newUser = new User();
            newUser.setUsername("user");
            newUser.setEmail("user@test.com");
            newUser.setPasswordHash("plaintext");

            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(userRepository.existsByEmail(any())).thenReturn(false);
            when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            userService.createUser(newUser);

            verify(passwordEncoder, times(1)).encode("plaintext");
        }
    }
}
