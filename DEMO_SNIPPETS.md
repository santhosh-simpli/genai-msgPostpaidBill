# Demo Snippets - MSG Telecom Postpaid Billing System

Quick reference snippets for demonstrating the application features.

---

## 🔐 Test Credentials

### Admin User
```
Username: john.smith
Password: password123
Role: ADMIN
```

### Customer User
```
Username: michael.williams
Password: password123
Role: CUSTOMER
```

### Operator User
```
Username: sarah.davis
Password: password123
Role: OPERATOR
```

---

## 🌐 Access URLs

```
Main Application:     http://localhost:8080
Login Page:          http://localhost:8080/login.html
H2 Database Console: http://localhost:8080/h2-console
Swagger API Docs:    http://localhost:8080/swagger-ui.html
OpenAPI JSON:        http://localhost:8080/v3/api-docs
```

### H2 Console Connection
```
JDBC URL:     jdbc:h2:mem:postpaid_billing
Username:     sa
Password:     (leave empty)
Driver Class: org.h2.Driver
```

---

## 📊 Database Queries (H2 Console)

### View All Users
```sql
SELECT user_id, username, email, role, first_name, last_name 
FROM users 
ORDER BY user_id;
```

### View All Customers with User Info
```sql
SELECT c.customer_id, c.first_name, c.last_name, c.email, c.phone_number, 
       u.username, u.role
FROM customers c
LEFT JOIN users u ON c.user_id = u.user_id
ORDER BY c.customer_id;
```

### View Recent Invoices
```sql
SELECT i.invoice_id, i.total_amount, i.status, i.billing_start_date, i.billing_end_date,
       c.first_name || ' ' || c.last_name AS customer_name
FROM invoices i
JOIN customers c ON i.customer_id = c.customer_id
ORDER BY i.invoice_id DESC
LIMIT 10;
```

### View Payments with Invoice Details
```sql
SELECT p.payment_id, p.amount, p.payment_date, p.payment_method,
       i.invoice_id, i.total_amount AS invoice_amount, i.status AS invoice_status,
       c.first_name || ' ' || c.last_name AS customer_name
FROM payments p
JOIN invoices i ON p.invoice_id = i.invoice_id
JOIN customers c ON i.customer_id = c.customer_id
ORDER BY p.payment_date DESC
LIMIT 20;
```

### View Usage Records by Service Type
```sql
SELECT u.usage_id, u.usage_date, u.usage_amount, u.unit,
       s.service_type, s.plan_name,
       c.first_name || ' ' || c.last_name AS customer_name
FROM usage_records u
JOIN services s ON u.service_id = s.service_id
JOIN customers c ON s.customer_id = c.customer_id
WHERE s.service_type = 'Mobile'
ORDER BY u.usage_date DESC
LIMIT 50;
```

### Check Pending Invoices
```sql
SELECT i.invoice_id, c.first_name || ' ' || c.last_name AS customer_name,
       i.total_amount, i.billing_start_date, i.billing_end_date
FROM invoices i
JOIN customers c ON i.customer_id = c.customer_id
WHERE i.status = 'PENDING'
ORDER BY i.billing_end_date DESC;
```

### Statistics Query
```sql
SELECT 
    COUNT(CASE WHEN status = 'PENDING' THEN 1 END) AS pending_invoices,
    COUNT(CASE WHEN status = 'PAID' THEN 1 END) AS paid_invoices,
    COUNT(CASE WHEN status = 'OVERDUE' THEN 1 END) AS overdue_invoices,
    SUM(CASE WHEN status = 'PENDING' THEN total_amount ELSE 0 END) AS outstanding_amount,
    SUM(CASE WHEN status = 'PAID' THEN total_amount ELSE 0 END) AS total_paid
FROM invoices;
```

---

## 🔧 API Testing (cURL Commands)

### 1. Login (Get JWT Token)
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"john.smith\",\"password\":\"password123\"}"
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john.smith",
  "role": "ADMIN",
  "userId": 1
}
```

### 2. Register New User
```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d "{
    \"username\":\"demo.user\",
    \"password\":\"demo123\",
    \"email\":\"demo.user@msgtel.com\",
    \"role\":\"CUSTOMER\",
    \"firstName\":\"Demo\",
    \"lastName\":\"User\"
  }"
```

### 3. Get All Customers (Authenticated)
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 4. Create New Invoice
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{
    \"customerId\": 1,
    \"billingStartDate\": \"2026-01-01\",
    \"billingEndDate\": \"2026-01-31\",
    \"totalAmount\": 150.00,
    \"status\": \"PENDING\"
  }"
```

### 5. Make Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{
    \"invoiceId\": 1,
    \"amount\": 150.00,
    \"paymentMethod\": \"CREDIT_CARD\"
  }"
```

### 6. Get Service Usage Records
```bash
curl -X GET http://localhost:8080/api/service-usage \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 7. Send Invoice Notification
```bash
curl -X POST http://localhost:8080/api/notifications/invoice \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{
    \"invoiceId\": 1,
    \"customerId\": 1
  }"
```

---

## 🎯 Browser Console Snippets

### Get Current JWT Token
```javascript
localStorage.getItem('token')
```

### Get Current User Info
```javascript
JSON.parse(localStorage.getItem('currentUser'))
```

### Clear Authentication (Logout Manually)
```javascript
localStorage.removeItem('token');
localStorage.removeItem('currentUser');
window.location.reload();
```

### Test API Call from Console
```javascript
// Get all customers
fetch('/api/customers', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})
.then(res => res.json())
.then(data => console.log(data));
```

### Create Invoice from Console
```javascript
fetch('/api/invoices', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token'),
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    customerId: 1,
    billingStartDate: '2026-01-01',
    billingEndDate: '2026-01-31',
    totalAmount: 199.99,
    status: 'PENDING'
  })
})
.then(res => res.json())
.then(data => console.log('Invoice Created:', data));
```

### Monitor API Calls
```javascript
// Intercept all fetch calls
const originalFetch = window.fetch;
window.fetch = function(...args) {
  console.log('API Call:', args[0]);
  return originalFetch.apply(this, args)
    .then(response => {
      console.log('Response:', response.status, args[0]);
      return response;
    });
};
```

---

## 📝 Sample Data for Demo

### New User Registration
```json
{
  "username": "jane.doe",
  "password": "secure123",
  "email": "jane.doe@msgtel.com",
  "role": "CUSTOMER",
  "firstName": "Jane",
  "lastName": "Doe"
}
```

### New Customer Profile
```json
{
  "firstName": "Robert",
  "lastName": "Johnson",
  "email": "robert.johnson@msgtel.com",
  "phoneNumber": "+1-555-0199",
  "address": "789 Oak Avenue, New York, NY 10001",
  "userId": 5
}
```

### Generate Invoice
```json
{
  "customerId": 1,
  "billingStartDate": "2026-01-01",
  "billingEndDate": "2026-01-31",
  "totalAmount": 250.00,
  "status": "PENDING"
}
```

### Make Payment
```json
{
  "invoiceId": 1,
  "amount": 250.00,
  "paymentMethod": "UPI"
}
```

### Add Usage Record
```json
{
  "serviceId": 1,
  "usageType": "Data",
  "amount": 15.5,
  "usageDate": "2026-01-18"
}
```

---

## 🎬 Demo Scenarios

### Scenario 1: Complete Billing Cycle
```
1. Login as Admin (john.smith / password123)
2. Go to Dashboard - Show metrics
3. Go to Usage History - Filter by "Mobile"
4. Go to Invoices - Generate new invoice for customer
5. Go to Payments - Process payment for the invoice
6. Return to Dashboard - Show updated metrics
```

### Scenario 2: Customer Registration Flow
```
1. Go to Login Page
2. Click "Register" tab
3. Fill form:
   - Username: test.customer
   - Email: test.customer@msgtel.com
   - Password: test123
   - First Name: Test
   - Last Name: Customer
4. Register and auto-login
5. Show customer dashboard view
```

### Scenario 3: Admin User Management
```
1. Login as Admin
2. Go to Admin Panel
3. Add New User:
   - Username: operator.new
   - Email: operator.new@msgtel.com
   - Role: OPERATOR
   - First/Last Name
4. Add Customer Profile for the user
5. Show updated dropdowns in Invoice/Payment forms
```

### Scenario 4: Invoice Notification
```
1. Go to Invoices
2. Generate new invoice
3. Click "Send Notification" button
4. Check logs to show email attempt
5. Explain email configuration (Gmail SMTP)
```

---

## 🔍 Troubleshooting Commands

### Restart Application
```bash
# Stop running application
Stop-Process -Name java -Force

# Start application
mvn spring-boot:run
```

### Check Application Logs
```bash
# In the terminal where Maven is running, scroll to see logs
# Look for:
# - "Started PostpaidBillingSystemApplication"
# - "Initialized database with 20 test users"
# - API request logs
```

### Verify Database Connection
```sql
-- Run in H2 Console
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS customer_count FROM customers;
SELECT COUNT(*) AS invoice_count FROM invoices;
SELECT COUNT(*) AS payment_count FROM payments;
SELECT COUNT(*) AS usage_count FROM usage_records;
```

### Test Email Configuration
```java
// Check application.properties
spring.mail.host=smtp.gmail.com
spring.mail.username=santhoshgupta4@gmail.com

// Note: For production, use environment variables
// ${MAIL_USERNAME:default@gmail.com}
```

---

## 📋 Quick Reference Tables

### Service Types
| Type | Icon | Color | Usage Unit |
|------|------|-------|------------|
| Mobile | 📱 | Blue | Minutes/SMS/GB |
| Broadband | 🌐 | Green | GB |
| Cable TV | 📺 | Purple | Hours |
| VoIP | ☎️ | Orange | Minutes |

### Payment Methods
| Method | Display Name |
|--------|--------------|
| CREDIT_CARD | Credit Card 💳 |
| DEBIT_CARD | Debit Card 🏦 |
| CASH | Cash 💵 |
| UPI | UPI 🔗 |
| BANK_TRANSFER | Bank Transfer 🏛️ |

### Invoice Status
| Status | Badge Color | Meaning |
|--------|-------------|---------|
| PENDING | Yellow | Unpaid, not overdue |
| PAID | Green | Payment completed |
| OVERDUE | Red | Past due date |

### User Roles
| Role | Access Level |
|------|--------------|
| ADMIN | Full access to all features |
| OPERATOR | View and edit data, no user management |
| CUSTOMER | View own data only |

---

## 💡 Pro Demo Tips

### Before Demo
- ✅ Clear browser cache and cookies
- ✅ Open application in incognito window
- ✅ Have H2 console open in another tab
- ✅ Keep browser DevTools open (Network tab)
- ✅ Prepare 2-3 browser windows for multi-user demo

### During Demo
- 🎯 Show API calls in Network tab
- 🎯 Demonstrate real-time updates
- 🎯 Use mobile responsive view (F12 → Device Toolbar)
- 🎯 Show database updates in H2 console
- 🎯 Highlight zero page refreshes

### Common Questions
**Q: Where is the data stored?**
A: H2 in-memory database (show H2 console)

**Q: How secure is the authentication?**
A: JWT tokens with BCrypt password hashing (show token in localStorage)

**Q: Can it handle multiple users?**
A: Yes, open two browsers with different users (demo simultaneous access)

**Q: Is it mobile-friendly?**
A: Fully responsive (resize browser or use mobile device)

**Q: What happens after payment?**
A: Invoice status auto-updates from PENDING → PAID (demonstrate this)

---

## 🎓 Advanced Demo Features

### Show Source Code in VS Code
```
1. Point out clean project structure
2. Show Spring Boot controllers
3. Highlight Alpine.js reactive data
4. Show Tailwind CSS utility classes
5. Demonstrate API endpoint mapping
```

### Explain Architecture
```
Frontend (Alpine.js + Tailwind)
    ↓ REST API calls
Backend (Spring Boot)
    ↓ JPA/Hibernate
Database (H2 → PostgreSQL for production)
```

### Performance Highlights
- ⚡ SPA - No page reloads
- ⚡ JWT - Stateless authentication
- ⚡ In-memory DB - Fast queries (H2)
- ⚡ Lazy loading - Load data on demand
- ⚡ Optimistic UI - Immediate feedback

---

**Last Updated:** January 18, 2026  
**Application Version:** 1.0  
**Demo Environment:** http://localhost:8080
