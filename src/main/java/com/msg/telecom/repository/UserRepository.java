package com.msg.telecom.repository;

import com.msg.telecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing user data.
 * Extends JpaRepository to inherit standard persistence operations.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username The username to check
     * @return true if a user with this username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email The email to check
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all users ordered by user ID descending (most recent first).
     *
     * @return List of all users with newest entries first
     */
    List<User> findAllByOrderByUserIdDesc();
}
