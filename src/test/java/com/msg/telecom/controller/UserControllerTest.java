package com.msg.telecom.controller;

import com.msg.telecom.dto.UserDto;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("john");
        testUser.setEmail("john@msgtel.com");
        testUser.setRole(UserRole.ADMIN);
        
        testUserDto = new UserDto();
        testUserDto.setUserId(1L);
        testUserDto.setUsername("john");
        testUserDto.setEmail("john@msgtel.com");
        testUserDto.setRole("ADMIN");
    }

    @Test
    void getAllUsers_ReturnsUserDtoList() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("john", response.getBody().get(0).getUsername());
    }

    @Test
    void getAllUsers_ReturnsEmptyList() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllUsers_MultipleUsers_ReturnsAll() {
        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("jane");
        user2.setEmail("jane@msgtel.com");
        user2.setRole(UserRole.CUSTOMER);
        
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser, user2));

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getUserById_ReturnsUserDto() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<UserDto> response = userController.getUserById(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john", response.getBody().getUsername());
        assertEquals("john@msgtel.com", response.getBody().getEmail());
        assertEquals("ADMIN", response.getBody().getRole());
    }

    @Test
    void getUserById_CustomerRole_ReturnsUserDto() {
        User customerUser = new User();
        customerUser.setUserId(2L);
        customerUser.setUsername("alice");
        customerUser.setEmail("alice@msgtel.com");
        customerUser.setRole(UserRole.CUSTOMER);
        
        when(userService.getUserById(2L)).thenReturn(customerUser);

        ResponseEntity<UserDto> response = userController.getUserById(2L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("alice", response.getBody().getUsername());
        assertEquals("CUSTOMER", response.getBody().getRole());
    }

    @Test
    void getUserById_NullRole_ReturnsUserDtoWithNullRole() {
        User userWithNullRole = new User();
        userWithNullRole.setUserId(3L);
        userWithNullRole.setUsername("noRole");
        userWithNullRole.setEmail("noRole@msgtel.com");
        userWithNullRole.setRole(null);
        
        when(userService.getUserById(3L)).thenReturn(userWithNullRole);

        ResponseEntity<UserDto> response = userController.getUserById(3L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getRole());
    }

    @Test
    void createUser_ReturnsCreatedUserDto() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<UserDto> response = userController.createUser(testUserDto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john", response.getBody().getUsername());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_WithAllFields_ReturnsCreatedUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername("bob");
        dto.setEmail("bob@msgtel.com");
        dto.setRole("CUSTOMER");
        
        User createdUser = new User();
        createdUser.setUserId(4L);
        createdUser.setUsername("bob");
        createdUser.setEmail("bob@msgtel.com");
        createdUser.setRole(UserRole.CUSTOMER);
        
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        ResponseEntity<UserDto> response = userController.createUser(dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("bob", response.getBody().getUsername());
        assertEquals("bob@msgtel.com", response.getBody().getEmail());
    }

    @Test
    void updateUser_ReturnsUpdatedUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername("eve");
        dto.setEmail("eve@msgtel.com");
        
        User updatedUser = new User();
        updatedUser.setUserId(4L);
        updatedUser.setUsername("eve");
        updatedUser.setEmail("eve@msgtel.com");
        updatedUser.setRole(UserRole.CUSTOMER);
        
        when(userService.updateUser(eq(4L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<UserDto> response = userController.updateUser(4L, dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("eve", response.getBody().getUsername());
        assertEquals("eve@msgtel.com", response.getBody().getEmail());
    }

    @Test
    void updateUser_WithAllFields_ReturnsUpdatedUserDto() {
        UserDto dto = new UserDto();
        dto.setUserId(5L);
        dto.setUsername("updated");
        dto.setEmail("updated@msgtel.com");
        dto.setRole("ADMIN");
        
        User updatedUser = new User();
        updatedUser.setUserId(5L);
        updatedUser.setUsername("updated");
        updatedUser.setEmail("updated@msgtel.com");
        updatedUser.setRole(UserRole.ADMIN);
        
        when(userService.updateUser(eq(5L), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<UserDto> response = userController.updateUser(5L, dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updated", response.getBody().getUsername());
        assertEquals("ADMIN", response.getBody().getRole());
    }

    @Test
    void deleteUser_ReturnsOk() {
        doNothing().when(userService).deleteUser(5L);
        
        ResponseEntity<Void> response = userController.deleteUser(5L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).deleteUser(5L);
    }

    @Test
    void deleteUser_NonExistent_ReturnsOk() {
        doNothing().when(userService).deleteUser(999L);
        
        ResponseEntity<Void> response = userController.deleteUser(999L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).deleteUser(999L);
    }
}
