package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for Customer entity.
 * <p>
 * Used for transferring customer data between layers and in API responses.
 * Includes optional nested UserDto for displaying linked user information.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class CustomerDto {
    
    /** Unique identifier for the customer. */
    private Long customerId;
    
    /** Short name or alias for the customer. */
    private String name;
    
    /** Full name of the customer. */
    private String fullName;
    
    /** Customer's email address. Synchronized with linked User entity. */
    private String email;
    
    /** Customer's phone number. */
    private String phoneNumber;
    
    /** Customer's physical address. */
    private String address;
    
    /** ID of the linked User entity. */
    private Long userId;
    
    /** Full user details for displaying linked data. */
    private UserDto user;
}
