package com.msg.telecom.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user registration requests.
 * <p>
 * Contains validated user information for account creation.
 * Defaults to CUSTOMER role if not specified.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    /** Username for the new account. Must be 3-50 characters. */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /** Email address for the new account. Must be valid format. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /** Password for the new account. Must be at least 6 characters. */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    /** Role for the new user. Defaults to CUSTOMER. */
    @NotBlank(message = "Role is required")
    private String role = "CUSTOMER";
}
