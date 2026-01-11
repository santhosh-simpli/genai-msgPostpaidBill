package com.msg.telecom.model;

/**
 * Enumeration of user roles in the telecom billing system.
 * <p>
 * Roles determine access levels and permissions within the application:
 * <ul>
 *   <li>CUSTOMER - Basic access to view own data and make payments</li>
 *   <li>OPERATOR - Can manage customers, services, and view reports</li>
 *   <li>ADMIN - Full administrative access to all features</li>
 * </ul>
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum UserRole {
    /**
     * Basic customer role with limited access.
     */
    CUSTOMER,
    
    /**
     * Operator role with customer management permissions.
     */
    OPERATOR,
    
    /**
     * Administrator role with full system access.
     */
    ADMIN
}