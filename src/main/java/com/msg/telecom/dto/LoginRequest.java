package com.msg.telecom.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication login requests.
 * <p>
 * Contains validated credentials for user authentication.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Username for authentication. Required field. */
    @NotBlank(message = "Username is required")
    private String username;

    /** Password for authentication. Required field. */
    @NotBlank(message = "Password is required")
    private String password;
}
