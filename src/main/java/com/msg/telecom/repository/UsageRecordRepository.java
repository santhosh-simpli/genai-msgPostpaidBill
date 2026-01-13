package com.msg.telecom.repository;

import com.msg.telecom.model.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for UsageRecord entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing usage record
 * data.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    /**
     * Finds all usage records for a specific service.
     *
     * @param serviceId The service's unique identifier
     * @return List of usage records for the service
     */
    List<UsageRecord> findByService_ServiceId(Long serviceId);
}
