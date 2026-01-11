package com.msg.telecom.controller;

import com.msg.telecom.model.Customer;
import com.msg.telecom.model.Invoice;
import com.msg.telecom.model.Service;
import com.msg.telecom.model.User;
import com.msg.telecom.service.CustomerService;
import com.msg.telecom.service.InvoiceService;
import com.msg.telecom.service.ServiceService;
import com.msg.telecom.service.UserService;
import com.msg.telecom.dto.CustomerDto;
import com.msg.telecom.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Customer resources.
 * <p>
 * This controller handles all HTTP requests related to customer operations
 * including CRUD operations, customer services, and invoices. Access is
 * controlled based on user roles (ADMIN, OPERATOR, CUSTOMER).
 * </p>
 * <p>
 * Customer data is synchronized with User entities to maintain consistency
 * across the application.
 * </p>
 *
 * @author MSG Telecom Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceService serviceService;
    private final InvoiceService invoiceService;
    private final UserService userService;

    /**
     * Retrieves all customers based on user role.
     * <p>
     * ADMIN and OPERATOR roles can view all customers.
     * CUSTOMER role can only view their own customer profiles.
     * </p>
     *
     * @param authentication The current authentication context
     * @return ResponseEntity containing list of CustomerDto objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Customer> customers;
        
        // Role-based filtering of customer data
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            customers = customerService.getAllCustomers();
        } else {
            customers = customerService.getCustomersByUserId(currentUser.getUserId());
        }
        
        List<CustomerDto> dtos = customers.stream().map(this::toDto).toList();
        log.debug("Retrieved {} customers for user: {}", dtos.size(), authentication.getName());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Creates a new customer.
     * <p>
     * Only ADMIN users can create new customers. The created customer
     * will have sample data (services, usage records, invoices) automatically
     * generated.
     * </p>
     *
     * @param customerDto The customer data to create
     * @return ResponseEntity containing the created CustomerDto
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = toEntity(customerDto);
        Customer created = customerService.createCustomer(customer);
        log.info("Created new customer with ID: {}", created.getCustomerId());
        return ResponseEntity.ok(toDto(created));
    }

    /**
     * Retrieves a specific customer by ID.
     * <p>
     * ADMIN and OPERATOR can view any customer.
     * CUSTOMER can only view their own profile.
     * </p>
     *
     * @param id             The customer's unique identifier
     * @param authentication The current authentication context
     * @return ResponseEntity containing the CustomerDto or 403 if unauthorized
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        Customer customer = customerService.getCustomerById(id);
        
        // Ensure customers can only view their own profile
        if (currentUser.getRole().name().equals("CUSTOMER") &&
                !customer.getUser().getUserId().equals(currentUser.getUserId())) {
            log.warn("Unauthorized access attempt to customer {} by user {}", id, currentUser.getUsername());
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(toDto(customer));
    }

    /**
     * Updates an existing customer's information.
     * <p>
     * When customer data is updated, the linked User entity's email
     * is also synchronized to maintain data consistency.
     * </p>
     *
     * @param id             The customer's unique identifier
     * @param customerDto    The customer data containing updated values
     * @param authentication The current authentication context
     * @return ResponseEntity containing the updated CustomerDto
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, 
                                                       @RequestBody CustomerDto customerDto,
                                                       Authentication authentication) {
        Customer customerDetails = toEntity(customerDto);
        customerDetails.setEmail(customerDto.getEmail());
        Customer updated = customerService.updateCustomer(id, customerDetails);
        log.info("Updated customer with ID: {}", id);
        return ResponseEntity.ok(toDto(updated));
    }

    /**
     * Retrieves all services for a specific customer.
     *
     * @param id The customer's unique identifier
     * @return ResponseEntity containing list of Service objects
     */
    @GetMapping("/{id}/services")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Service>> getCustomerServices(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServicesByCustomerId(id));
    }

    /**
     * Adds a new service for a customer.
     *
     * @param id      The customer's unique identifier
     * @param service The service to add
     * @return ResponseEntity containing the created Service
     */
    @PostMapping("/{id}/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> addCustomerService(@PathVariable Long id, @RequestBody Service service) {
        Customer customer = customerService.getCustomerById(id);
        service.setCustomer(customer);
        log.info("Adding new service for customer ID: {}", id);
        return ResponseEntity.ok(serviceService.createService(service));
    }

    /**
     * Retrieves all invoices for a specific customer.
     * <p>
     * Invoices are returned in descending order by ID (most recent first).
     * </p>
     *
     * @param id The customer's unique identifier
     * @return ResponseEntity containing list of Invoice objects
     */
    @GetMapping("/{id}/invoices")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Invoice>> getCustomerInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(id));
    }

    /**
     * Generates a new invoice for a customer.
     *
     * @param id      The customer's unique identifier
     * @param invoice The invoice to create
     * @return ResponseEntity containing the created Invoice
     */
    @PostMapping("/{id}/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> generateCustomerInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        Customer customer = customerService.getCustomerById(id);
        invoice.setCustomer(customer);
        log.info("Generating new invoice for customer ID: {}", id);
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    /**
     * Deletes a customer from the system.
     * <p>
     * This will cascade delete all associated services, usage records,
     * invoices, and payments.
     * </p>
     *
     * @param id The customer's unique identifier
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Converts a Customer entity to CustomerDto.
     * <p>
     * Includes linked User data for consistent display across the application.
     * </p>
     *
     * @param customer The customer entity to convert
     * @return CustomerDto with all relevant data
     */
    private CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getFullName());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getUser() != null ? customer.getUser().getEmail() : null);
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setUserId(customer.getUser() != null ? customer.getUser().getUserId() : null);

        // Include full user details for linked data display
        if (customer.getUser() != null) {
            UserDto userDto = new UserDto();
            userDto.setUserId(customer.getUser().getUserId());
            userDto.setUsername(customer.getUser().getUsername());
            userDto.setEmail(customer.getUser().getEmail());
            userDto.setRole(customer.getUser().getRole() != null ? customer.getUser().getRole().name() : null);
            dto.setUser(userDto);
        }
        return dto;
    }

    /**
     * Converts a CustomerDto to Customer entity.
     *
     * @param dto The CustomerDto to convert
     * @return Customer entity
     */
    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setFullName(dto.getName() != null ? dto.getName() : dto.getFullName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddress(dto.getAddress());
        return customer;
    }
}
