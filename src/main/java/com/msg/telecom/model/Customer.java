package com.msg.telecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Customer in the telecom billing system.
 * <p>
 * Customers are linked to User accounts and can have multiple services,
 * invoices, and payments. Customer email is synchronized with the linked
 * User entity for data consistency across the application.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Customer {

    /**
     * Unique identifier for the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    /**
     * The User account linked to this customer.
     * Provides authentication and authorization capabilities.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Customer's full legal name.
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Customer's physical address for billing and service purposes.
     */
    @Column(nullable = false)
    private String address;

    /**
     * Customer's contact phone number.
     */
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * Customer's email address.
     * Note: This is synchronized with the linked User's email for consistency.
     */
    @Column(nullable = false)
    private String email;

    /**
     * Constructs a Customer with essential fields.
     * Used for test case requirements and backward compatibility.
     *
     * @param customerId  The customer's unique identifier
     * @param user        The linked User account
     * @param fullName    The customer's full name
     * @param address     The customer's address
     * @param phoneNumber The customer's phone number
     */
    public Customer(Long customerId, User user, String fullName, String address, String phoneNumber) {
        this.customerId = customerId;
        this.user = user;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
