package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for User entity.
 * <p>
 * Used for transferring user data in API responses.
 * Excludes sensitive information like password hash.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class UserDto {

    /** Unique identifier for the user. */
    private Long userId;

    /** Username for authentication. */
    private String username;

    /** User's email address. Synchronized with linked Customer entities. */
    private String email;

    /** User's role (CUSTOMER, OPERATOR, ADMIN). */
    private String role;
}
