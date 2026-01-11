package com.msg.telecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * <p>
 * Contains JWT token and user information after successful authentication.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    /** JWT authentication token. */
    private String token;
    
    /** Token type. Always "Bearer" for JWT. */
    private String type = "Bearer";
    
    /** User's unique identifier. */
    private Long id;
    
    /** User's username. */
    private String username;
    
    /** User's email address. */
    private String email;
    
    /** User's role (CUSTOMER, OPERATOR, ADMIN). */
    private String role;

    /**
     * Creates an AuthResponse with the specified parameters.
     *
     * @param token    JWT authentication token
     * @param id       User's unique identifier
     * @param username User's username
     * @param email    User's email address
     * @param role     User's role
     */
    public AuthResponse(String token, Long id, String username, String email, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
