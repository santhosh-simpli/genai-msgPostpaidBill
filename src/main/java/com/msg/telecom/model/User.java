package com.msg.telecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Entity class representing a User in the telecom billing system.
 * <p>
 * Implements Spring Security's UserDetails interface for authentication.
 * Users can have roles of ADMIN, OPERATOR, or CUSTOMER which control
 * their access to different parts of the application.
 * </p>
 * <p>
 * When user email is updated, the change is automatically synchronized
 * to all linked Customer entities for data consistency.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class User implements UserDetails {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * Unique username for authentication.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Hashed password for secure authentication.
     */
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    /**
     * User's email address.
     * Changes are synchronized to linked Customer entities.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User's role determining access permissions.
     * Defaults to CUSTOMER role.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    /**
     * Timestamp of when the user account was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically sets the creation timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Returns the authorities granted to the user.
     * Role is prefixed with "ROLE_" for Spring Security.
     *
     * @return Collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The hashed password
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true (accounts never expire in this implementation)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true (accounts are never locked in this implementation)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * @return true (credentials never expire in this implementation)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true (users are always enabled in this implementation)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
