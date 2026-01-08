package com.msg.telecom.controller;

import com.msg.telecom.model.User;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> userDtos = users.stream().map(this::toDto).toList();
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toDto(user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        User created = userService.createUser(user);
        return ResponseEntity.ok(toDto(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = toEntity(userDto);
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    private User toEntity(UserDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Convert role string to UserRole enum
        if (dto.getRole() != null) {
            try {
                user.setRole(com.msg.telecom.model.UserRole.valueOf(dto.getRole()));
            } catch (IllegalArgumentException e) {
                // Default to CUSTOMER if invalid role
                user.setRole(com.msg.telecom.model.UserRole.CUSTOMER);
            }
        }
        return user;
    }
}
