package com.msg.telecom.dto;

import lombok.Data;

/**
 * Data Transfer Object for UsageRecord entity.
 * <p>
 * Used for tracking service consumption for billing purposes.
 * Usage records contribute to invoice calculations.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class UsageRecordDto {

    /** Unique identifier for the usage record. */
    private Long usageId;

    /** ID of the service this usage is associated with. */
    private Long serviceId;

    /** Type of usage (e.g., VOICE, DATA, SMS). */
    private String usageType;

    /** Amount of usage recorded. */
    private Double amount;

    /** Date when the usage occurred. */
    private String usageDate;
}
