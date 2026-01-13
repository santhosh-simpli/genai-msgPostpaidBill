package com.msg.telecom.repository;

import com.msg.telecom.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Service entity data access operations.
 * <p>
 * Provides CRUD operations and custom query methods for managing telecom
 * service data.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    /**
     * Finds all services for a specific customer.
     *
     * @param customerId The customer's unique identifier
     * @return List of services subscribed by the customer
     */
    List<Service> findByCustomer_CustomerId(Long customerId);

    /**
     * Finds all services with a specific status.
     *
     * @param status The service status (Active, Inactive, Suspended)
     * @return List of services with the specified status
     */
    List<Service> findByStatus(String status);
}
