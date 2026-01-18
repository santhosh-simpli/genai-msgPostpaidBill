# Backend Demo Guide - MSG Telecom Postpaid Billing System

Complete guide to understanding and demonstrating the Spring Boot backend architecture with code snippets.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Backend                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Controllers  │  │   Security   │  │     DTOs     │      │
│  │ (REST APIs)  │  │  (JWT Auth)  │  │ (Data Transfer)│    │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘      │
│         │                  │                                 │
│         ▼                  ▼                                 │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │   Services   │  │    Config    │                        │
│  │(Business Logic)│  │  (Security)  │                       │
│  └──────┬───────┘  └──────────────┘                        │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────┐  ┌──────────────┐                        │
│  │ Repositories │  │    Models    │                        │
│  │ (JPA/Data)   │  │  (Entities)  │                        │
│  └──────┬───────┘  └──────┬───────┘                        │
│         │                  │                                 │
│         ▼                  ▼                                 │
│  ┌─────────────────────────────────────┐                   │
│  │      H2 In-Memory Database          │                   │
│  └─────────────────────────────────────┘                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Technology Stack

### Core Framework
- **Spring Boot 3.2.1** - Main application framework
- **Java 21** - Programming language
- **Maven** - Dependency management and build tool

### Database & ORM
- **H2 Database** - In-memory database for development
- **Spring Data JPA** - Object-relational mapping
- **Hibernate** - JPA implementation

### Security
- **Spring Security 6** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password encryption

### API Documentation
- **SpringDoc OpenAPI 3** - Swagger UI integration
- **Jakarta Validation** - Request validation

### Other Libraries
- **Lombok** - Reduce boilerplate code
- **SLF4J/Logback** - Logging framework
- **JavaMail** - Email notifications

---

## 🎯 1. APPLICATION ENTRY POINT

### PostpaidBillingSystemApplication.java

```java
package com.msg.telecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PostpaidBillingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostpaidBillingSystemApplication.class, args);
    }
}
```

**What Happens on Startup:**
1. ✅ Spring container initializes
2. ✅ Database schema auto-created (H2)
3. ✅ DataInitializer runs → Creates 20 test users
4. ✅ Security filters configured
5. ✅ REST endpoints registered
6. ✅ Server starts on port 8080

**Demo Talking Points:**
- Single annotation `@SpringBootApplication` enables auto-configuration
- Embedded Tomcat server - no external deployment needed
- Production-ready application in minimal code

---

## 🔐 2. SECURITY LAYER

### JWT Token Provider

**File:** `JwtTokenProvider.java`

```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // 24 hours

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

**Key Features:**
- 🔑 **HS512 Algorithm** - Secure token signing
- ⏰ **24-hour expiration** - Configurable timeout
- ✅ **Token validation** - Prevents tampering
- 📝 **Claims extraction** - Get username from token

**Demo Command:**
```bash
# Show JWT secret in application.properties
cat src/main/resources/application.properties | grep jwt
```

---

### Security Configuration

**File:** `SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/login", "/api/register").permitAll()
                .requestMatchers("/h2-console/**", "/swagger-ui/**").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // Multi-role endpoints
                .requestMatchers("/api/customers/**")
                    .hasAnyRole("ADMIN", "OPERATOR", "CUSTOMER")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Security Features:**
- 🚫 **CSRF Disabled** - Not needed for JWT
- 🔄 **Stateless Sessions** - No server-side sessions
- 🎭 **Role-Based Access** - ADMIN, OPERATOR, CUSTOMER
- 🛡️ **JWT Filter** - Validates every request
- 🔒 **BCrypt Encryption** - Password hashing

**Demo Scenario:**
```bash
# Show that unauthenticated requests fail
curl http://localhost:8080/api/users
# Response: 401 Unauthorized

# Show that public endpoints work
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john.smith","password":"password123"}'
# Response: {token: "eyJ...", username: "john.smith", role: "ADMIN"}
```

---

## 🎮 3. CONTROLLERS (REST APIs)

### Authentication Controller

**File:** `AuthController.java`

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
```

**Endpoints:**
- `POST /api/login` → Authenticate user, return JWT token
- `POST /api/register` → Create new user account

**Request/Response Examples:**

**Login Request:**
```json
{
  "username": "john.smith",
  "password": "password123"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huLnNtaXRoIiwiaWF0IjoxNzA...",
  "userId": 1,
  "username": "john.smith",
  "email": "john.smith@msgtel.com",
  "role": "ADMIN"
}
```

---

### Payment Controller (Example)

**File:** `PaymentController.java`

```java
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'CUSTOMER')")
    public ResponseEntity<List<PaymentDto>> getAllPayments(Authentication auth) {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentDto> dtos = payments.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<PaymentDto> createPayment(
            @RequestBody PaymentDto dto, 
            Authentication authentication) {
        
        Payment payment = toEntity(dto);
        Payment created = paymentService.createPayment(payment);
        return ResponseEntity.ok(toDto(created));
    }
}
```

**Security Annotations:**
- `@PreAuthorize("hasRole('ADMIN')")` - Admin only
- `@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")` - Multiple roles

**Demo Point:**
- Show how method-level security works
- Different roles have different access
- Automatic 403 Forbidden if unauthorized

---

## 💼 4. SERVICE LAYER (Business Logic)

### Payment Service with Auto-Update Logic

**File:** `PaymentService.java`

```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Creates payment and automatically updates invoice status to PAID
     */
    public Payment createPayment(Payment payment) {
        // Save the payment first
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created new payment with ID: {} for invoice ID: {}",
                savedPayment.getPaymentId(),
                payment.getInvoice() != null ? payment.getInvoice().getInvoiceId() : "N/A");

        // Automatically update invoice status to PAID
        if (payment.getInvoice() != null) {
            Invoice invoice = payment.getInvoice();
            invoice.setStatus("PAID");
            invoiceRepository.save(invoice);
            log.info("Updated invoice {} status to PAID after payment", 
                    invoice.getInvoiceId());
        }

        return savedPayment;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }
}
```

**Business Logic Highlights:**
- ✅ **Transactional** - All-or-nothing operations
- ✅ **Logging** - Track important operations
- ✅ **Automatic Status Update** - Invoice becomes PAID after payment
- ✅ **Data Consistency** - Ensures referential integrity

**Demo Scenario:**
1. Show invoice with status "PENDING"
2. Create payment via API
3. Show invoice status automatically changed to "PAID"
4. Check application logs for confirmation

---

### Authentication Service

**File:** `AuthService.java`

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Successful login for user: {} with role: {}", 
                user.getUsername(), user.getRole());
        
        return new AuthResponse(
            token, 
            user.getUserId(), 
            user.getUsername(), 
            user.getEmail(), 
            user.getRole().name()
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user with encrypted password
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.valueOf(request.getRole()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user: {} with role: {}", 
                savedUser.getUsername(), savedUser.getRole());

        // Auto-login after registration
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        String token = jwtTokenProvider.generateToken(auth);

        return new AuthResponse(
            token, 
            savedUser.getUserId(), 
            savedUser.getUsername(), 
            savedUser.getEmail(), 
            savedUser.getRole().name()
        );
    }
}
```

**Key Operations:**
1. **Login:** Validate credentials → Generate JWT → Return token
2. **Register:** Check duplicates → Encrypt password → Save user → Auto-login

---

## 🗃️ 5. DATA ACCESS LAYER

### Repository Interfaces

**File:** `UserRepository.java`

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

**File:** `PaymentRepository.java`

```java
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderByPaymentDateDesc();
    List<Payment> findByInvoice_InvoiceId(Long invoiceId);
}
```

**JPA Benefits:**
- ✅ No SQL needed for basic operations
- ✅ Type-safe queries
- ✅ Automatic CRUD implementation
- ✅ Custom query methods by naming convention

**Demo Query Examples:**
```java
// Automatically generated by Spring Data JPA:
findByUsername(String username)           // SELECT * FROM users WHERE username = ?
findAllByOrderByPaymentDateDesc()        // SELECT * FROM payments ORDER BY payment_date DESC
findByInvoice_InvoiceId(Long invoiceId)  // JOIN query with invoice
existsByEmail(String email)              // SELECT COUNT(*) > 0 FROM users WHERE email = ?
```

---

## 📊 6. ENTITY MODELS

### User Entity

**File:** `User.java`

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ADMIN, OPERATOR, CUSTOMER

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Spring Security UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
```

**Entity Features:**
- 🔑 **Primary Key:** Auto-generated ID
- 🔒 **Unique Constraints:** Username and email
- 🎭 **Enum Role:** Type-safe role handling
- 🔐 **UserDetails Interface:** Spring Security integration
- 📝 **Lombok Annotations:** Reduce boilerplate

---

### Payment Entity

**File:** `Payment.java`

```java
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, CASH, UPI, BANK_TRANSFER
}
```

**Relationships:**
- `@ManyToOne` - Many payments can belong to one invoice
- `@JoinColumn` - Foreign key column
- `FetchType.EAGER` - Load invoice data automatically

---

## 🚀 7. DATA INITIALIZATION

**File:** `DataInitializer.java`

```java
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

        // Create 20 test users
        String[] firstNames = {"John", "Sarah", "Michael", "Emily", ...};
        String[] lastNames = {"Smith", "Johnson", "Williams", ...};

        for (int i = 0; i < 20; i++) {
            String firstName = firstNames[i];
            String lastName = lastNames[i];
            String username = (firstName + "." + lastName).toLowerCase();
            String email = username + "@msgtel.com";

            // Assign roles
            UserRole role = (i == 0) ? UserRole.ADMIN :
                          (i < 3) ? UserRole.OPERATOR : UserRole.CUSTOMER;

            // Create user with encrypted password
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(role)
                    .firstName(firstName)
                    .lastName(lastName)
                    .createdAt(LocalDateTime.now())
                    .build();
            users.add(userRepository.save(user));

            // Create customer profile
            Customer customer = Customer.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phoneNumber("+1-555-" + String.format("%04d", random.nextInt(10000)))
                    .address(/* generate address */)
                    .user(user)
                    .build();
            customers.add(customerRepository.save(customer));
        }

        // Create services, usage records, invoices, payments...
        System.out.println("✅ Initialized database with 20 test users and sample data");
    }
}
```

**What Gets Created:**
- 20 Users (1 Admin, 2 Operators, 17 Customers)
- 20 Customer profiles
- 40+ Services (Mobile, Broadband, Cable TV, VoIP)
- 426+ Usage records
- 60+ Invoices (PENDING, PAID, OVERDUE)
- 40+ Payments

**Demo Point:**
- Show logs on startup: "Initialized database with 20 test users"
- Open H2 console and show data

---

## 📝 8. DTOs (Data Transfer Objects)

### Why DTOs?
- ✅ Decouple API from database models
- ✅ Control what data is exposed
- ✅ Validation at API boundary
- ✅ Prevent over-fetching

### Login Request DTO

```java
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
```

### Auth Response DTO

```java
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;
}
```

### Payment DTO

```java
@Data
public class PaymentDto {
    private Long paymentId;
    private Long invoiceId;
    private Double amount;
    private String paymentDate;
    private String status; // Display as "Completed" in frontend
}
```

---

## ⚙️ 9. CONFIGURATION FILES

### application.properties

```properties
# Server Configuration
server.port=8080
spring.application.name=MSG Telecom Postpaid Billing System

# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:postpaid_billing
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true

# JWT Configuration
jwt.secret=MSG_TELECOM_SECRET_KEY_FOR_JWT_TOKEN_GENERATION_2026_VERY_SECURE_KEY
jwt.expiration=86400000  # 24 hours in milliseconds

# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:santhoshgupta4@gmail.com}
spring.mail.password=${MAIL_PASSWORD:Santhosh@143}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging Configuration
logging.level.com.msg.telecom=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=INFO

# Swagger UI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

**Key Properties Explained:**
- `create-drop` - Recreate database on each startup (dev mode)
- `show-sql=true` - Log SQL queries
- `jwt.expiration` - Token validity period
- Environment variables for sensitive data: `${MAIL_USERNAME:default}`

---

## 🔍 10. API FLOW DEMONSTRATION

### Complete Request Flow: Make Payment

**1. Frontend Request:**
```javascript
fetch('/api/payments', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9...',
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    invoiceId: 1,
    amount: 150.00,
    paymentMethod: 'CREDIT_CARD'
  })
})
```

**2. Security Filter (JwtAuthenticationFilter):**
```java
// Extract JWT token from Authorization header
String jwt = getJwtFromRequest(request);

// Validate token
if (jwtTokenProvider.validateToken(jwt)) {
    String username = jwtTokenProvider.getUsernameFromToken(jwt);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
    // Set authentication in context
    UsernamePasswordAuthenticationToken auth = 
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

**3. Controller (PaymentController):**
```java
@PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto dto) {
    // Check authorization (Spring Security handles this)
    // Convert DTO to Entity
    Payment payment = toEntity(dto);
    
    // Call service layer
    Payment created = paymentService.createPayment(payment);
    
    // Convert Entity to DTO
    return ResponseEntity.ok(toDto(created));
}
```

**4. Service Layer (PaymentService):**
```java
@Transactional
public Payment createPayment(Payment payment) {
    // Save payment
    Payment savedPayment = paymentRepository.save(payment);
    log.info("Created payment ID: {}", savedPayment.getPaymentId());
    
    // Auto-update invoice status
    if (payment.getInvoice() != null) {
        Invoice invoice = payment.getInvoice();
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);
        log.info("Updated invoice {} to PAID", invoice.getInvoiceId());
    }
    
    return savedPayment;
}
```

**5. Repository Layer (PaymentRepository):**
```java
// Spring Data JPA generates SQL automatically
paymentRepository.save(payment);
// Executes: INSERT INTO payments (amount, invoice_id, payment_date, payment_method) 
//           VALUES (150.00, 1, '2026-01-18', 'CREDIT_CARD')
```

**6. Database (H2):**
```sql
-- Payment record inserted
-- Invoice status updated from PENDING to PAID
```

**7. Response:**
```json
{
  "paymentId": 42,
  "invoiceId": 1,
  "amount": 150.00,
  "paymentDate": "2026-01-18",
  "status": "Completed"
}
```

---

## 🧪 11. TESTING THE BACKEND

### Using cURL

**1. Login and Get Token:**
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john.smith","password":"password123"}' \
  | jq .token -r
```

**2. Store Token:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john.smith","password":"password123"}' \
  | jq .token -r)

echo $TOKEN
```

**3. Make Authenticated Request:**
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer $TOKEN" \
  | jq .
```

**4. Create Payment:**
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "invoiceId": 1,
    "amount": 200.00,
    "paymentMethod": "UPI"
  }' | jq .
```

---

### Using Swagger UI

**Access:** http://localhost:8080/swagger-ui.html

**Steps:**
1. Click "Authorize" button
2. Enter: `Bearer YOUR_TOKEN_HERE`
3. Try endpoints with built-in UI
4. See request/response examples
5. Download OpenAPI spec

---

### Using H2 Console

**Access:** http://localhost:8080/h2-console

**Connection:**
```
JDBC URL:  jdbc:h2:mem:postpaid_billing
Username:  sa
Password:  (empty)
```

**Sample Queries:**
```sql
-- Check payment count
SELECT COUNT(*) FROM payments;

-- View recent payments with customer names
SELECT p.payment_id, p.amount, p.payment_date, p.payment_method,
       i.invoice_id, i.status AS invoice_status,
       c.first_name || ' ' || c.last_name AS customer_name
FROM payments p
JOIN invoices i ON p.invoice_id = i.invoice_id
JOIN customers c ON i.customer_id = c.customer_id
ORDER BY p.payment_date DESC
LIMIT 10;

-- Check invoice status change
SELECT invoice_id, total_amount, status, billing_start_date
FROM invoices
WHERE invoice_id = 1;
```

---

## 📊 12. LOGGING AND MONITORING

### Application Logs

**View Logs:**
```bash
# In Maven terminal
# Look for these key messages:

✅ Started PostpaidBillingSystemApplication in X.XXX seconds
✅ Initialized database with 20 test users and sample data
✅ Tomcat started on port(s): 8080

# API Request Logs:
INFO  c.m.t.controller.PaymentController : Creating payment for invoice 1
INFO  c.m.t.service.PaymentService : Created new payment with ID: 42
INFO  c.m.t.service.PaymentService : Updated invoice 1 status to PAID after payment

# Authentication Logs:
INFO  c.m.t.service.AuthService : Login attempt for username: john.smith
INFO  c.m.t.service.AuthService : Successful login for user: john.smith with role: ADMIN
```

### Enable Debug Logging

**Add to application.properties:**
```properties
logging.level.com.msg.telecom=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## 🎯 13. DEMO SCRIPT FOR BACKEND

### 5-Minute Backend Demo Flow:

**1. Show Project Structure (30 sec)**
```
src/main/java/com/msg/telecom/
├── controller/    ← REST API endpoints
├── service/       ← Business logic
├── repository/    ← Data access
├── model/         ← Database entities
├── security/      ← JWT authentication
├── config/        ← Spring configuration
└── dto/           ← Request/Response objects
```

**2. Explain Security (1 min)**
- Show JwtTokenProvider.java
- Explain token generation
- Show SecurityConfig.java with role-based access
- Demo: Login → Get token → Use token in request

**3. Show API Flow (1.5 min)**
- Open PaymentController.java
- Trace request: Controller → Service → Repository
- Show @Transactional annotation
- Explain automatic invoice status update

**4. Database Layer (1 min)**
- Open H2 console
- Show generated tables
- Run JOIN query showing relationships
- Show 426 usage records, 60 invoices, 40 payments

**5. Live API Testing (1.5 min)**
- Use Swagger UI or cURL
- Login and get JWT token
- Create invoice
- Make payment
- Show invoice status changed in H2 console
- Check application logs

---

## 🔧 14. BACKEND BEST PRACTICES IMPLEMENTED

### Design Patterns
- ✅ **Repository Pattern** - Data access abstraction
- ✅ **Service Layer Pattern** - Business logic separation
- ✅ **DTO Pattern** - Decouple API from entities
- ✅ **Dependency Injection** - @RequiredArgsConstructor (Lombok)
- ✅ **Builder Pattern** - Clean object construction

### Security Best Practices
- ✅ **Password Encryption** - BCrypt hashing
- ✅ **JWT Authentication** - Stateless tokens
- ✅ **Role-Based Access Control** - Method-level security
- ✅ **CORS Configuration** - Controlled cross-origin access
- ✅ **Input Validation** - @Valid annotations

### Code Quality
- ✅ **Lombok** - Reduce boilerplate code
- ✅ **Logging** - SLF4J with meaningful messages
- ✅ **Transactions** - @Transactional for data consistency
- ✅ **Exception Handling** - Custom exceptions
- ✅ **Documentation** - JavaDoc comments

### Database
- ✅ **JPA Entities** - Object-relational mapping
- ✅ **Automatic Schema Generation** - DDL auto-creation
- ✅ **Named Queries** - Custom repository methods
- ✅ **Eager/Lazy Loading** - Optimized data fetching
- ✅ **Cascading** - Relationship management

---

## 💡 15. PRODUCTION CONSIDERATIONS

### Database Migration
```properties
# Development (Current)
spring.jpa.hibernate.ddl-auto=create-drop

# Production (Change to)
spring.jpa.hibernate.ddl-auto=validate
# Use Flyway or Liquibase for migrations
```

### External Database
```properties
# PostgreSQL Example
spring.datasource.url=jdbc:postgresql://localhost:5432/msgtel_billing
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Environment Variables
```bash
# Set in production
export JWT_SECRET=your-very-secure-production-secret-key
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export MAIL_USERNAME=notifications@msgtel.com
export MAIL_PASSWORD=app-specific-password
```

### Performance Tuning
```properties
# Connection pooling
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5

# Query optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
```

---

## 📚 16. KEY ENDPOINTS SUMMARY

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| `/api/login` | POST | ❌ | Public | User authentication |
| `/api/register` | POST | ❌ | Public | User registration |
| `/api/users` | GET | ✅ | ADMIN | List all users |
| `/api/users/{id}` | PUT | ✅ | ADMIN | Update user |
| `/api/users/{id}/password` | PUT | ✅ | All | Change password |
| `/api/customers` | GET | ✅ | All | List customers |
| `/api/customers/{id}/services` | GET | ✅ | All | Customer services |
| `/api/service-usage` | GET | ✅ | All | All usage records |
| `/api/invoices` | GET | ✅ | All | List invoices |
| `/api/invoices` | POST | ✅ | ADMIN/OP | Create invoice |
| `/api/payments` | GET | ✅ | All | List payments |
| `/api/payments` | POST | ✅ | ADMIN/CUST | Make payment |
| `/api/notifications/invoice` | POST | ✅ | All | Send email |

---

## 🎓 17. INTERVIEW/DEMO QUESTIONS & ANSWERS

**Q: How does JWT authentication work?**
A: Login generates a signed token with user info, valid for 24 hours. Every request includes this token in the Authorization header. JwtAuthenticationFilter validates it and sets Spring Security context.

**Q: How do you handle authorization?**
A: Role-based access control using @PreAuthorize annotations. Three roles: ADMIN (full access), OPERATOR (read/write), CUSTOMER (own data only).

**Q: What happens when a payment is made?**
A: PaymentService creates the payment record and automatically updates the invoice status from PENDING to PAID in a single transaction.

**Q: How is password security handled?**
A: BCrypt hashing with salt. Passwords are never stored in plain text. ValidationPasswordEncoder compares hashed values during authentication.

**Q: Can you explain the database relationships?**
A: User 1→1 Customer 1→N Services 1→N UsageRecords. Customer 1→N Invoices 1→N Payments. All use JPA @OneToMany and @ManyToOne.

**Q: How do you prevent SQL injection?**
A: JPA PreparedStatements with parameter binding. No raw SQL concatenation. All queries are parameterized.

**Q: What's the purpose of DTOs?**
A: Decouple API contracts from database schema. Control exposed data. Add validation. Prevent over-fetching and security issues.

**Q: How is transaction management handled?**
A: @Transactional annotation ensures atomic operations. If any step fails, entire operation rolls back (e.g., payment + invoice update).

---

## 🚀 Quick Start Commands

```bash
# Clone repository
git clone https://github.com/santhosh-simpli/genai-msgPostpaidBill.git
cd genai-msgPostpaidBill

# Run application
mvn spring-boot:run

# Run tests
mvn clean test

# Generate coverage report
mvn clean test jacoco:report

# Build JAR
mvn clean package

# Run JAR
java -jar target/postpaid-billing-system-0.0.1-SNAPSHOT.jar
```

---

**Last Updated:** January 18, 2026  
**Spring Boot Version:** 3.2.1  
**Java Version:** 21
