package com.msg.telecom.service;

import com.msg.telecom.model.*;
import com.msg.telecom.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final Random random = new Random();
    
    public CustomerService(CustomerRepository customerRepository,
                          ServiceRepository serviceRepository,
                          UsageRecordRepository usageRecordRepository,
                          InvoiceRepository invoiceRepository) {
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public List<Customer> getCustomersByUserId(Long userId) {
        return customerRepository.findByUser_UserId(userId);
    }

    public Customer createCustomer(Customer customer) {
        if (customer.getPhoneNumber() != null && 
            customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        customer = customerRepository.save(customer);
        
        // Automatically generate sample data for new customer
        generateSampleDataForCustomer(customer);
        
        return customer;
    }
    
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
    }
    
    private String getUnitForServiceType(String serviceType) {
        return switch (serviceType) {
            case "Mobile" -> "Minutes";
            case "Broadband" -> "GB";
            case "Cable TV" -> "Hours";
            case "VoIP" -> "Minutes";
            default -> "Units";
        };
    }
    
    private double getRandomUsageAmount(String serviceType) {
        return switch (serviceType) {
            case "Mobile" -> 50 + random.nextDouble() * 450; // 50-500 minutes
            case "Broadband" -> 10 + random.nextDouble() * 90; // 10-100 GB
            case "Cable TV" -> 20 + random.nextDouble() * 80; // 20-100 hours
            case "VoIP" -> 30 + random.nextDouble() * 270; // 30-300 minutes
            default -> random.nextDouble() * 100;
        };
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);
        customer.setFullName(customerDetails.getFullName());
        customer.setAddress(customerDetails.getAddress());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
