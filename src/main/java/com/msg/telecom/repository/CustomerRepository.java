package com.msg.telecom.repository;

import com.msg.telecom.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing customer data.
 * Extends JpaRepository to inherit standard persistence operations.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Finds all customers associated with a specific user.
     *
     * @param userId The user's unique identifier
     * @return List of customers linked to the user
     */
    List<Customer> findByUser_UserId(Long userId);
    
    /**
     * Finds a customer by their phone number.
     *
     * @param phoneNumber The customer's phone number
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    /**
     * Checks if a customer exists with the given phone number.
     *
     * @param phoneNumber The phone number to check
     * @return true if a customer with this phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Retrieves all customers ordered by customer ID descending (most recent first).
     *
     * @return List of all customers with newest entries first
     */
    List<Customer> findAllByOrderByCustomerIdDesc();
}
