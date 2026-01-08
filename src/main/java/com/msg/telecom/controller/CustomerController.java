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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceService serviceService;
    private final InvoiceService invoiceService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<List<CustomerDto>> getAllCustomers(Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        List<Customer> customers;
        if (currentUser.getRole().name().equals("ADMIN") || currentUser.getRole().name().equals("OPERATOR")) {
            customers = customerService.getAllCustomers();
        } else {
            customers = customerService.getCustomersByUserId(currentUser.getUserId());
        }
        List<CustomerDto> dtos = customers.stream().map(this::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = toEntity(customerDto);
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.ok(toDto(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'OPERATOR')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        Customer customer = customerService.getCustomerById(id);
        if (currentUser.getRole().name().equals("CUSTOMER") &&
                !customer.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(toDto(customer));
    }

    @GetMapping("/{id}/services")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Service>> getCustomerServices(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServicesByCustomerId(id));
    }

    @PostMapping("/{id}/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Service> addCustomerService(@PathVariable Long id, @RequestBody Service service) {
        Customer customer = customerService.getCustomerById(id);
        service.setCustomer(customer);
        return ResponseEntity.ok(serviceService.createService(service));
    }

    @GetMapping("/{id}/invoices")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<Invoice>> getCustomerInvoices(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(id));
    }

    @PostMapping("/{id}/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> generateCustomerInvoice(@PathVariable Long id, @RequestBody Invoice invoice) {
        Customer customer = customerService.getCustomerById(id);
        invoice.setCustomer(customer);
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

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

    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setFullName(dto.getName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        // Set user and other fields as needed
        return customer;
    }
}
