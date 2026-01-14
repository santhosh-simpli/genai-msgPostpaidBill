package com.msg.telecom.config;

import com.msg.telecom.model.*;
import com.msg.telecom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        Random random = new Random();
        List<User> users = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        // Create 20 test users with customer data
        String[] firstNames = { "John", "Sarah", "Michael", "Emily", "David", "Jessica", "James", "Ashley",
                "Robert", "Amanda", "William", "Jennifer", "Richard", "Lisa", "Thomas",
                "Michelle", "Daniel", "Karen", "Christopher", "Nancy" };
        String[] lastNames = { "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
                "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
                "Thomas", "Taylor", "Moore", "Jackson", "Martin" };
        String[] cities = { "New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia",
                "San Antonio", "San Diego", "Dallas", "San Jose", "Austin", "Jacksonville",
                "Fort Worth", "Columbus", "Charlotte", "San Francisco", "Indianapolis",
                "Seattle", "Denver", "Boston" };
        String[] streets = { "Main St", "Oak Ave", "Maple Dr", "Cedar Ln", "Pine Rd", "Elm St",
                "Washington Blvd", "Park Ave", "Lake Dr", "Hill Rd", "River St", "Forest Ln",
                "Sunset Blvd", "Broadway", "Market St", "Church St", "School Rd", "Mill St",
                "Bridge St", "Valley Rd" };

        // Create users and customers
        for (int i = 0; i < 20; i++) {
            String firstName = firstNames[i];
            String lastName = lastNames[i];
            String username = (firstName + "." + lastName).toLowerCase();

            // First 2 users are admins, rest are customers
            UserRole role = (i < 2) ? UserRole.ADMIN : UserRole.CUSTOMER;

            String email = username + "@msgtel.com";
            User user = User.builder()
                    .username(username)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .email(email) // Ensure email is always set
                    .role(role)
                    .build();
            users.add(user);
            user = userRepository.save(user);

            // Create customer profile for all users
            Customer customer = Customer.builder()
                    .user(user)
                    .firstName(firstName)
                    .lastName(lastName)
                    .fullName(firstName + " " + lastName)
                    .address((100 + i * 10) + " " + streets[i] + ", " + cities[i])
                    .phoneNumber(String.format("+1-%03d-%03d-%04d",
                            200 + i, 300 + i, 1000 + i * 100))
                    .email(email) // Ensure email is always set for customer
                    .build();
            customers.add(customer);
            customer = customerRepository.save(customer);

            // Skip service creation for admins (first 2 users) if needed, but let's create
            // for all
            createServicesAndData(customer, random, i);
        }

        System.out.println("✅ Initialized database with 20 test users and sample data");
    }

    private void createServicesAndData(Customer customer, Random random, int customerIndex) {
        String[] serviceTypes = { "Mobile", "Broadband", "Cable TV", "VoIP" };

        // Each customer gets 1-3 services
        int numberOfServices = 1 + random.nextInt(3);
        List<Service> customerServices = new ArrayList<>();

        for (int i = 0; i < numberOfServices; i++) {
            String serviceType = serviceTypes[random.nextInt(serviceTypes.length)];
            LocalDate startDate = LocalDate.now().minusMonths(random.nextInt(12) + 1);

            Service service = Service.builder()
                    .customer(customer)
                    .serviceType(serviceType)
                    .startDate(startDate)
                    .status(random.nextBoolean() ? "Active" : "Suspended")
                    .build();
            service = serviceRepository.save(service);
            customerServices.add(service);

            // Create usage records for this service
            createUsageRecords(service, random, serviceType);
        }

        // Create invoices for this customer
        createInvoices(customer, random, customerIndex);
    }

    private void createUsageRecords(Service service, Random random, String serviceType) {
        int numberOfRecords = 5 + random.nextInt(10); // 5-14 usage records per service for more test data

        for (int i = 0; i < numberOfRecords; i++) {
            LocalDate usageDate = LocalDate.now().minusDays(random.nextInt(90));
            String unit;
            double amount;

            // Generate varied and realistic usage data based on service type
            switch (serviceType) {
                case "Mobile":
                    // Randomly choose between voice, SMS, or data
                    int mobileType = random.nextInt(3);
                    if (mobileType == 0) {
                        unit = "Minutes";
                        amount = 50 + random.nextInt(500); // 50-550 minutes
                    } else if (mobileType == 1) {
                        unit = "SMS";
                        amount = 10 + random.nextInt(200); // 10-210 SMS
                    } else {
                        unit = "GB";
                        amount = 0.5 + (random.nextDouble() * 25); // 0.5-25.5 GB
                    }
                    break;
                case "Broadband":
                    unit = "GB";
                    amount = 10 + random.nextInt(500); // 10-510 GB with variation
                    break;
                case "Cable TV":
                    unit = "Hours";
                    amount = 5 + random.nextInt(250); // 5-255 hours
                    break;
                case "VoIP":
                    unit = "Minutes";
                    amount = 30 + random.nextInt(600); // 30-630 minutes
                    break;
                default:
                    unit = "Units";
                    amount = 1 + random.nextInt(150);
            }

            UsageRecord usageRecord = UsageRecord.builder()
                    .service(service)
                    .usageDate(usageDate)
                    .usageAmount(amount)
                    .unit(unit)
                    .build();
            usageRecordRepository.save(usageRecord);
        }
    }

    private void createInvoices(Customer customer, Random random, int customerIndex) {
        // Create 2-4 invoices per customer
        int numberOfInvoices = 2 + random.nextInt(3);

        for (int i = 0; i < numberOfInvoices; i++) {
            LocalDate periodEnd = LocalDate.now().minusMonths(i);
            LocalDate periodStart = periodEnd.minusMonths(1);

            double totalAmount = 50.0 + random.nextInt(450); // $50 - $500
            String[] statuses = { "Paid", "Pending", "Overdue" };
            String status = statuses[random.nextInt(statuses.length)];

            Invoice invoice = Invoice.builder()
                    .customer(customer)
                    .billingPeriodStart(periodStart)
                    .billingPeriodEnd(periodEnd)
                    .totalAmount(totalAmount)
                    .status(status)
                    .build();
            invoice = invoiceRepository.save(invoice);

            // Create payments for paid invoices
            if (status.equals("Paid")) {
                createPayments(invoice, random, totalAmount);
            } else if (status.equals("Pending") && random.nextBoolean()) {
                // Some pending invoices have partial payments
                createPayments(invoice, random, totalAmount * 0.5);
            }
        }
    }

    private void createPayments(Invoice invoice, Random random, double totalAmount) {
        // Decide between single payment or split payments
        if (random.nextBoolean() || totalAmount < 100) {
            // Single payment
            String[] methods = { "Credit Card", "Debit Card", "Bank Transfer", "PayPal", "Cash" };
            Payment payment = Payment.builder()
                    .invoice(invoice)
                    .paymentDate(invoice.getBillingPeriodEnd().plusDays(random.nextInt(15)))
                    .amount(totalAmount)
                    .paymentMethod(methods[random.nextInt(methods.length)])
                    .build();
            paymentRepository.save(payment);
        } else {
            // Split into 2 payments
            String[] methods = { "Credit Card", "Debit Card", "Bank Transfer" };
            double firstPayment = totalAmount * (0.4 + random.nextDouble() * 0.3); // 40-70%

            Payment payment1 = Payment.builder()
                    .invoice(invoice)
                    .paymentDate(invoice.getBillingPeriodEnd().plusDays(random.nextInt(7)))
                    .amount(firstPayment)
                    .paymentMethod(methods[random.nextInt(methods.length)])
                    .build();
            paymentRepository.save(payment1);

            Payment payment2 = Payment.builder()
                    .invoice(invoice)
                    .paymentDate(invoice.getBillingPeriodEnd().plusDays(7 + random.nextInt(8)))
                    .amount(totalAmount - firstPayment)
                    .paymentMethod(methods[random.nextInt(methods.length)])
                    .build();
            paymentRepository.save(payment2);
        }
    }
}
