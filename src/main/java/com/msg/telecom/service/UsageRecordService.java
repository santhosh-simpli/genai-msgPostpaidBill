package com.msg.telecom.service;

import com.msg.telecom.model.UsageRecord;
import com.msg.telecom.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing UsageRecord entities.
 * <p>
 * This service handles all operations related to tracking customer usage
 * of telecom services. Usage records are linked to services and contain
 * measurements like minutes, GB, or hours depending on service type.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UsageRecordService {

    private final UsageRecordRepository usageRecordRepository;

    /**
     * Retrieves all usage records from the database.
     *
     * @return List of all usage records
     */
    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    /**
     * Retrieves a usage record by its unique identifier.
     *
     * @param id The usage record's unique identifier
     * @return The usage record entity
     * @throws RuntimeException if usage record is not found
     */
    public UsageRecord getUsageRecordById(Long id) {
        return usageRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage record not found with id: " + id));
    }

    /**
     * Retrieves all usage records for a specific service.
     *
     * @param serviceId The service's unique identifier
     * @return List of usage records for the specified service
     */
    public List<UsageRecord> getUsageRecordsByServiceId(Long serviceId) {
        return usageRecordRepository.findByService_ServiceId(serviceId);
    }

    /**
     * Creates a new usage record for a service.
     *
     * @param usageRecord The usage record entity to create
     * @return The created usage record with generated ID
     */
    public UsageRecord createUsageRecord(UsageRecord usageRecord) {
        UsageRecord savedRecord = usageRecordRepository.save(usageRecord);
        log.info("Created new usage record with ID: {} - Amount: {} {}", 
                savedRecord.getUsageId(), usageRecord.getUsageAmount(), usageRecord.getUnit());
        return savedRecord;
    }

    /**
     * Updates an existing usage record's information.
     *
     * @param id                 The usage record's unique identifier
     * @param usageRecordDetails The usage record data containing updated values
     * @return The updated usage record entity
     * @throws RuntimeException if usage record is not found
     */
    public UsageRecord updateUsageRecord(Long id, UsageRecord usageRecordDetails) {
        UsageRecord usageRecord = getUsageRecordById(id);
        usageRecord.setUsageDate(usageRecordDetails.getUsageDate());
        usageRecord.setUsageAmount(usageRecordDetails.getUsageAmount());
        usageRecord.setUnit(usageRecordDetails.getUnit());
        log.info("Updated usage record with ID: {}", id);
        return usageRecordRepository.save(usageRecord);
    }

    /**
     * Deletes a usage record from the system.
     *
     * @param id The usage record's unique identifier
     */
    public void deleteUsageRecord(Long id) {
        log.info("Deleting usage record with ID: {}", id);
        usageRecordRepository.deleteById(id);
    }
}
