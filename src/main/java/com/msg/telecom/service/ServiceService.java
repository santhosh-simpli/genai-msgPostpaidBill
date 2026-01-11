package com.msg.telecom.service;

import com.msg.telecom.model.Service;
import com.msg.telecom.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing telecom Service entities.
 * <p>
 * This service handles all operations related to telecom services such as
 * Mobile, Broadband, Cable TV, and VoIP. Services are linked to customers
 * and track usage records for billing purposes.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;

    /**
     * Retrieves all services from the database.
     *
     * @return List of all telecom services
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Retrieves a service by its unique identifier.
     *
     * @param id The service's unique identifier
     * @return The service entity
     * @throws RuntimeException if service is not found
     */
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    /**
     * Retrieves all services for a specific customer.
     *
     * @param customerId The customer's unique identifier
     * @return List of services for the specified customer
     */
    public List<Service> getServicesByCustomerId(Long customerId) {
        return serviceRepository.findByCustomer_CustomerId(customerId);
    }

    /**
     * Creates a new telecom service for a customer.
     *
     * @param service The service entity to create
     * @return The created service with generated ID
     */
    public Service createService(Service service) {
        Service savedService = serviceRepository.save(service);
        log.info("Created new service with ID: {} - Type: {}", 
                savedService.getServiceId(), service.getServiceType());
        return savedService;
    }

    /**
     * Updates an existing service's information.
     *
     * @param id             The service's unique identifier
     * @param serviceDetails The service data containing updated values
     * @return The updated service entity
     * @throws RuntimeException if service is not found
     */
    public Service updateService(Long id, Service serviceDetails) {
        Service service = getServiceById(id);
        service.setServiceType(serviceDetails.getServiceType());
        service.setStatus(serviceDetails.getStatus());
        log.info("Updated service with ID: {} - Status: {}", id, serviceDetails.getStatus());
        return serviceRepository.save(service);
    }

    /**
     * Deletes a service from the system.
     * <p>
     * Note: This will cascade delete all associated usage records.
     * </p>
     *
     * @param id The service's unique identifier
     */
    public void deleteService(Long id) {
        log.info("Deleting service with ID: {}", id);
        serviceRepository.deleteById(id);
    }
}
