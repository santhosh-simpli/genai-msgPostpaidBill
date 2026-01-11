package com.msg.telecom.service;

import com.msg.telecom.dto.AuthResponse;
import com.msg.telecom.dto.LoginRequest;
import com.msg.telecom.dto.RegisterRequest;
import com.msg.telecom.model.User;
import com.msg.telecom.model.UserRole;
import com.msg.telecom.repository.UserRepository;
import com.msg.telecom.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling authentication operations.
 * <p>
 * This service manages user authentication including login and registration
 * functionality. It integrates with Spring Security for secure password
 * handling and JWT token generation.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates a user and generates a JWT token.
     * <p>
     * This method validates user credentials against the stored data and
     * generates a JWT token for subsequent API requests.
     * </p>
     *
     * @param request The login request containing username and password
     * @return AuthResponse containing JWT token and user details
     * @throws RuntimeException if authentication fails or user not found
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        // Authenticate using Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Generate JWT token from successful authentication
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Retrieve user details for response
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate user role is set
        if (user.getRole() == null) {
            throw new RuntimeException("User role is not set");
        }

        log.info("Successful login for user: {} with role: {}", user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    /**
     * Registers a new user in the system.
     * <p>
     * This method creates a new user account with encrypted password,
     * validates that username and email are unique, and automatically
     * authenticates the user returning a JWT token.
     * </p>
     *
     * @param request The registration request containing user details
     * @return AuthResponse containing JWT token and user details
     * @throws RuntimeException if username or email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());
        
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user with encrypted password
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole()));

        user = userRepository.save(user);
        log.info("Created new user with ID: {} and role: {}", user.getUserId(), user.getRole());

        // Automatically authenticate the new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
