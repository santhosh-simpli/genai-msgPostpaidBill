package com.msg.telecom.controller;

import com.msg.telecom.model.User;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing User resources.
 * <p>
 * This controller handles all HTTP requests related to user management
 * including CRUD operations. Only ADMIN users have access to these endpoints.
 * </p>
 * <p>
 * When user data is updated (especially email), the changes are automatically
 * synchronized to linked Customer entities to maintain data consistency.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all users from the system.
     * <p>
     * Users are returned in descending order by ID (most recent first).
     * </p>
     *
     * @return ResponseEntity containing list of UserDto objects
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream().map(this::toDto).toList();
        log.debug("Retrieved {} users", userDtos.size());
        return ResponseEntity.ok(userDtos);
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id The user's unique identifier
     * @return ResponseEntity containing the UserDto
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toDto(user));
    }

    /**
     * Creates a new user.
     * <p>
     * The password will be automatically encrypted before storage.
     * </p>
     *
     * @param userDto The user data to create
     * @return ResponseEntity containing the created UserDto
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        User created = userService.createUser(user);
        log.info("Created new user with ID: {}", created.getUserId());
        return ResponseEntity.ok(toDto(created));
    }

    /**
     * Updates an existing user's information.
     * <p>
     * When user data is updated (especially email), the changes are automatically
     * synchronized to all linked Customer entities to maintain data consistency
     * across the application.
     * </p>
     *
     * @param id      The user's unique identifier
     * @param userDto The user data containing updated values
     * @return ResponseEntity containing the updated UserDto
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        User updated = userService.updateUser(id, user);
        log.info("Updated user with ID: {}", id);
        return ResponseEntity.ok(toDto(updated));
    }

    /**
     * Deletes a user from the system.
     *
     * @param id The user's unique identifier
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Converts a User entity to UserDto.
     *
     * @param user The user entity to convert
     * @return UserDto with relevant data
     */
    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    /**
     * Converts a UserDto to User entity.
     * <p>
     * Handles role string to UserRole enum conversion with fallback to CUSTOMER.
     * </p>
     *
     * @param dto The UserDto to convert
     * @return User entity
     */
    private User toEntity(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        // Convert role string to UserRole enum with error handling
        if (dto.getRole() != null) {
            try {
                user.setRole(com.msg.telecom.model.UserRole.valueOf(dto.getRole()));
            } catch (IllegalArgumentException e) {
                // Default to CUSTOMER if invalid role provided
                log.warn("Invalid role '{}' provided, defaulting to CUSTOMER", dto.getRole());
                user.setRole(com.msg.telecom.model.UserRole.CUSTOMER);
            }
        }
        return user;
    }
}
