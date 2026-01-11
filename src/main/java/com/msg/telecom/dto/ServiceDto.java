package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for Service entity.
 * <p>
 * Used for managing telecom service subscriptions.
 * Service types include VOICE, DATA, SMS, and BUNDLE.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class ServiceDto {
    
    /** Unique identifier for the service. */
    private Long serviceId;
    
    /** Type of service (VOICE, DATA, SMS, BUNDLE). */
    private String serviceType;
    
    /** Current status (ACTIVE, SUSPENDED, CANCELLED). */
    private String status;
    
    /** ID of the customer who subscribed to this service. */
    private Long customerId;
}
