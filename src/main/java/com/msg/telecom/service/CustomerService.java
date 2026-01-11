package com.msg.telecom.service;

import com.msg.telecom.model.*;
import com.msg.telecom.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Service class for managing Customer entities and their related data.
 * <p>
 * This service handles all CRUD operations for customers and provides
 * automatic sample data generation for new customers including services,
 * usage records, and invoices.
 * </p>
 * 
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();
    
    /**
     * Constructs a CustomerService with required dependencies.
     *
     * @param customerRepository    Repository for customer data access
     * @param serviceRepository     Repository for service data access
     * @param usageRecordRepository Repository for usage record data access
     * @param invoiceRepository     Repository for invoice data access
     * @param userRepository        Repository for user data access
     */
    public CustomerService(CustomerRepository customerRepository,
                          ServiceRepository serviceRepository,
                          UsageRecordRepository usageRecordRepository,
                          InvoiceRepository invoiceRepository,
                          UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return List of all customers, ordered by customer ID descending (most recent first)
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAllByOrderByCustomerIdDesc();
    }

    /**
     * Retrieves a customer by their unique identifier.
     *
     * @param id The customer's unique identifier
     * @return The customer entity
     * @throws RuntimeException if customer is not found
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    /**
     * Retrieves all customers associated with a specific user.
     *
     * @param userId The user's unique identifier
     * @return List of customers linked to the specified user
     */
    public List<Customer> getCustomersByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    /**
     * Creates a new customer with automatic sample data generation.
     * <p>
     * When a customer is created, the system automatically generates:
     * <ul>
     *   <li>1-2 random services (Mobile, Broadband, Cable TV, or VoIP)</li>
     *   <li>3-5 usage records per service</li>
     *   <li>2-3 sample invoices with varying statuses</li>
     * </ul>
     * </p>
     *
     * @param customer The customer entity to create
     * @return The created customer with generated ID
     * @throws RuntimeException if the phone number already exists
     */
    public Customer createCustomer(Customer customer) {
        if (customer.getPhoneNumber() != null && 
            customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        customer = customerRepository.save(customer);
        log.info("Created new customer with ID: {}", customer.getCustomerId());
        
        // Automatically generate sample data for new customer
        generateSampleDataForCustomer(customer);
        
        return customer;
    }
    
    /**
     * Generates sample data for a newly created customer.
     * <p>
     * Creates random services with usage records and sample invoices
     * to demonstrate the billing system functionality.
     * </p>
     *
     * @param customer The customer to generate data for
     */
    private void generateSampleDataForCustomer(Customer customer) {
        String[] serviceTypes = {"Mobile", "Broadband", "Cable TV", "VoIP"};
        int numServices = random.nextInt(2) + 1; // 1-2 services
        
        for (int i = 0; i < numServices; i++) {
            String serviceType = serviceTypes[random.nextInt(serviceTypes.length)];
            
            // Create service
            com.msg.telecom.model.Service service = com.msg.telecom.model.Service.builder()
                .customer(customer)
                .serviceType(serviceType)
                .startDate(LocalDate.now().minusDays(random.nextInt(180) + 30))
                .status("Active")
                .build();
            service = serviceRepository.save(service);
            
            // Create 3-5 usage records
            int numRecords = random.nextInt(3) + 3;
            for (int j = 0; j < numRecords; j++) {
                String unit = getUnitForServiceType(serviceType);
                double amount = getRandomUsageAmount(serviceType);
                
                UsageRecord record = UsageRecord.builder()
                    .service(service)
                    .usageDate(LocalDate.now().minusDays(random.nextInt(60)))
                    .usageAmount(amount)
                    .unit(unit)
                    .build();
                usageRecordRepository.save(record);
            }
        }
        
        // Create 2-3 sample invoices
        int numInvoices = random.nextInt(2) + 2;
        String[] statuses = {"PAID", "PENDING", "OVERDUE"};
        
        for (int i = 0; i < numInvoices; i++) {
            LocalDate startDate = LocalDate.now().minusMonths(i + 1).withDayOfMonth(1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            double amount = 50 + random.nextDouble() * 450; // $50-$500
            
            Invoice invoice = Invoice.builder()
                .customer(customer)
                .billingPeriodStart(startDate)
                .billingPeriodEnd(endDate)
                .totalAmount(amount)
                .status(statuses[i < statuses.length ? i : random.nextInt(statuses.length)])
                .build();
            invoiceRepository.save(invoice);
        }
        log.debug("Generated sample data for customer ID: {}", customer.getCustomerId());
    }
    
    /**
     * Maps service type to appropriate usage unit.
     *
     * @param serviceType The type of telecom service
     * @return The unit of measurement for usage tracking
     */
    private String getUnitForServiceType(String serviceType) {
        return switch (serviceType) {
            case "Mobile" -> "Minutes";
            case "Broadband" -> "GB";
            case "Cable TV" -> "Hours";
            case "VoIP" -> "Minutes";
            default -> "Units";
        };
    }
    
    /**
     * Generates a random usage amount based on service type.
     *
     * @param serviceType The type of telecom service
     * @return A random usage amount appropriate for the service type
     */
    private double getRandomUsageAmount(String serviceType) {
        return switch (serviceType) {
            case "Mobile" -> 50 + random.nextDouble() * 450; // 50-500 minutes
            case "Broadband" -> 10 + random.nextDouble() * 90; // 10-100 GB
            case "Cable TV" -> 20 + random.nextDouble() * 80; // 20-100 hours
            case "VoIP" -> 30 + random.nextDouble() * 270; // 30-300 minutes
            default -> random.nextDouble() * 100;
        };
    }

    /**
     * Updates an existing customer's profile information.
     * <p>
     * This method also synchronizes the email with the linked User entity
     * to maintain data consistency across the application.
     * </p>
     *
     * @param id              The customer's unique identifier
     * @param customerDetails The customer data containing updated values
     * @return The updated customer entity
     * @throws RuntimeException if customer is not found
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        customer.setFullName(customerDetails.getFullName());
        customer.setAddress(customerDetails.getAddress());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        
        // Synchronize email with linked User entity if email changed
        if (customerDetails.getEmail() != null && customer.getUser() != null) {
            User linkedUser = customer.getUser();
            if (!customerDetails.getEmail().equals(linkedUser.getEmail())) {
                // Check if new email is available
                if (!userRepository.existsByEmail(customerDetails.getEmail())) {
                    linkedUser.setEmail(customerDetails.getEmail());
                    userRepository.save(linkedUser);
                    log.info("Synchronized email update from customer {} to user {}", 
                            customer.getCustomerId(), linkedUser.getUserId());
                }
            }
            customer.setEmail(customerDetails.getEmail());
        }
        
        log.info("Updated customer with ID: {}", customer.getCustomerId());
        return customerRepository.save(customer);
    }

    /**
     * Deletes a customer from the system.
     * <p>
     * Note: This will cascade delete all related services, usage records,
     * invoices, and payments associated with this customer.
     * </p>
     *
     * @param id The customer's unique identifier
     */
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        customerRepository.deleteById(id);
    }
}
