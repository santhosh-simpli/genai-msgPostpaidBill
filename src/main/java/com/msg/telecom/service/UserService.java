package com.msg.telecom.service;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.User;
import com.msg.telecom.repository.CustomerRepository;
import com.msg.telecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing User entities and authentication-related
 * operations.
 * <p>
 * This service handles CRUD operations for users and ensures data consistency
 * by synchronizing email changes with linked Customer entities.
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
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users ordered by user ID descending (most recent first)
     */
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByUserIdDesc();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The user's unique identifier
     * @return The user entity
     * @throws RuntimeException if user is not found
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The user's username
     * @return The user entity
     * @throws RuntimeException if user is not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /**
     * Creates a new user with encrypted password.
     * <p>
     * Validates that the username and email are unique before creating.
     * </p>
     *
     * @param user The user entity to create
     * @return The created user with generated ID
     * @throws RuntimeException if username or email already exists
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        log.info("Created new user with username: {}", user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's information.
     * <p>
     * This method synchronizes email changes with all linked Customer entities
     * to maintain data consistency across the application. When a user's email
     * is updated, all customers associated with this user will also have their
     * email updated.
     * </p>
     *
     * @param id          The user's unique identifier
     * @param userDetails The user data containing updated values
     * @return The updated user entity
     * @throws RuntimeException if user is not found or if username/email already
     *                          taken
     */
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        String oldEmail = user.getEmail();

        // Update username if changed and not already taken
        if (userDetails.getUsername() != null && !userDetails.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDetails.getUsername());
            log.info("Updated username for user ID: {}", id);
        }

        // Update email if changed and not already taken
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDetails.getEmail());

            // Cascade email update to all linked customers for data consistency
            List<Customer> linkedCustomers = customerRepository.findByUser_UserId(id);
            for (Customer customer : linkedCustomers) {
                customer.setEmail(userDetails.getEmail());
                customerRepository.save(customer);
                log.info("Synchronized email update from user {} to customer {}",
                        id, customer.getCustomerId());
            }
        }

        // Update role if provided
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        log.info("Updated user with ID: {}", id);
        return userRepository.save(user);
    }

    /**
     * Deletes a user from the system.
     * <p>
     * Note: Consider the impact on linked Customer entities before deletion.
     * </p>
     *
     * @param id The user's unique identifier
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }

    /**
     * Changes a user's password.
     * <p>
     * Validates the old password before updating to the new password.
     * </p>
     *
     * @param id          The user's unique identifier
     * @param oldPassword The current password
     * @param newPassword The new password to set
     * @throws RuntimeException if old password is incorrect
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserById(id);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", id);
    }
}
