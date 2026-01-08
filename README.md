# MSG Telecom: Postpaid Billing System

A comprehensive postpaid billing system for telecom operators to manage customer accounts, track service usage, generate invoices, and process payments with role-based access control.

## 🚀 Features

- **Customer Management**: Complete CRUD operations for customer accounts with role-based access (Admin, Operator, Customer)
- **Service Usage Tracking**: Track various telecom services (Voice Calls, Data, SMS, Roaming, Value Added Services)
- **Invoice Generation**: Automated invoice creation with billing periods, taxes, and due dates
- **Payment Processing**: Support multiple payment methods (Credit Card, Debit Card, Net Banking, UPI, Cash)
- **Global Exception Handling**: Centralized error handling with meaningful error responses
- **CORS Enabled**: Supports all HTTP methods for seamless frontend integration
- **H2 Database Console**: Built-in database console for development and testing

## 🛠️ Technical Stack

### Backend
- **Java**: 21
- **Spring Boot**: 3.2.1
- **Maven**: Build tool
- **H2 Database**: In-memory database
- **Spring Data JPA**: Data persistence
- **Lombok**: Reduce boilerplate code
- **Port**: 8090

### Frontend
- **AlpineJS**: Reactive JavaScript framework
- **Tailwind CSS**: Utility-first CSS framework
- **Vanilla JavaScript**: For API interactions
- **Port**: Served via Spring Boot on 8090

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Any modern web browser

## 🔧 Installation & Setup

### 1. Clone the repository
```bash
git clone <repository-url>
cd genai-msgPostpaidBill
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on:
- **Backend API**: http://localhost:8090
- **Frontend**: http://localhost:8090/index.html
- **H2 Console**: http://localhost:8090/h2-console

## 🗄️ Database Configuration

### H2 Database Console Access
- **URL**: http://localhost:8090/h2-console
- **JDBC URL**: jdbc:h2:mem:postpaid_billing
- **Username**: sa
- **Password**: (leave empty)

## 📚 API Endpoints

### Customer Management
- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/email/{email}` - Get customer by email
- `GET /api/customers/phone/{phoneNumber}` - Get customer by phone
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Service Usage Management
- `GET /api/service-usage` - Get all service usages
- `GET /api/service-usage/{id}` - Get usage by ID
- `GET /api/service-usage/customer/{customerId}` - Get usages by customer
- `POST /api/service-usage` - Create new usage record
- `PUT /api/service-usage/{id}` - Update usage record
- `DELETE /api/service-usage/{id}` - Delete usage record

### Invoice Management
- `GET /api/invoices` - Get all invoices
- `GET /api/invoices/{id}` - Get invoice by ID
- `GET /api/invoices/customer/{customerId}` - Get invoices by customer
- `GET /api/invoices/number/{invoiceNumber}` - Get invoice by number
- `POST /api/invoices` - Create new invoice
- `PUT /api/invoices/{id}` - Update invoice
- `PATCH /api/invoices/{id}/status?status={STATUS}` - Update invoice status
- `DELETE /api/invoices/{id}` - Delete invoice

### Payment Management
- `GET /api/payments` - Get all payments
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/invoice/{invoiceId}` - Get payments by invoice
- `GET /api/payments/transaction/{transactionId}` - Get payment by transaction ID
- `POST /api/payments` - Create new payment
- `PUT /api/payments/{id}` - Update payment
- `DELETE /api/payments/{id}` - Delete payment

## 📊 Data Models

### Customer
- **Fields**: ID, Name, Email, Phone Number, Address, Role (ADMIN/CUSTOMER/OPERATOR), Password, Active Status
- **Relationships**: One-to-Many with ServiceUsage and Invoice

### Service Usage
- **Fields**: ID, Service Type, Quantity, Rate Per Unit, Total Amount, Usage Date
- **Service Types**: VOICE_CALL, DATA, SMS, ROAMING, VALUE_ADDED_SERVICE
- **Relationships**: Many-to-One with Customer

### Invoice
- **Fields**: ID, Invoice Number, Billing Period (Start/End), Subtotal, Tax, Total Amount, Status, Due Date
- **Status Types**: PENDING, PAID, OVERDUE, CANCELLED
- **Relationships**: Many-to-One with Customer, One-to-Many with Payment

### Payment
- **Fields**: ID, Transaction ID, Amount, Payment Method, Status, Payment Date
- **Payment Methods**: CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, CASH
- **Status Types**: SUCCESS, FAILED, PENDING, REFUNDED
- **Relationships**: Many-to-One with Invoice

## 🎨 Frontend Features

### User Interface
- Clean and modern design with Tailwind CSS
- Responsive layout for all screen sizes
- Tab-based navigation for different modules
- Modal forms for CRUD operations
- Real-time data updates

### Data Validation
- Client-side form validation
- Required field checks
- Email and phone number format validation
- Date range validation for billing periods
- Amount validation for payments

### Interactive Components
- Dynamic tables with edit/delete actions
- Status badges with color coding
- Sortable and filterable data views
- Confirmation dialogs for destructive actions

## 🔒 Security Features

- Role-based access control (RBAC)
- Password encryption (implement in production)
- CORS configuration for cross-origin requests
- Input validation on both client and server side

## 🧪 Testing

To run tests:
```bash
mvn test
```

## 📦 Project Structure

```
genai-msgPostpaidBill/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── msg/
│   │   │           └── telecom/
│   │   │               ├── PostpaidBillingSystemApplication.java
│   │   │               ├── config/
│   │   │               │   └── CorsConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── CustomerController.java
│   │   │               │   ├── ServiceUsageController.java
│   │   │               │   ├── InvoiceController.java
│   │   │               │   └── PaymentController.java
│   │   │               ├── model/
│   │   │               │   ├── Customer.java
│   │   │               │   ├── ServiceUsage.java
│   │   │               │   ├── Invoice.java
│   │   │               │   └── Payment.java
│   │   │               ├── repository/
│   │   │               │   ├── CustomerRepository.java
│   │   │               │   ├── ServiceUsageRepository.java
│   │   │               │   ├── InvoiceRepository.java
│   │   │               │   └── PaymentRepository.java
│   │   │               ├── service/
│   │   │               │   ├── CustomerService.java
│   │   │               │   ├── ServiceUsageService.java
│   │   │               │   ├── InvoiceService.java
│   │   │               │   └── PaymentService.java
│   │   │               └── exception/
│   │   │                   ├── GlobalExceptionHandler.java
│   │   │                   ├── ResourceNotFoundException.java
│   │   │                   ├── DuplicateResourceException.java
│   │   │                   └── ErrorResponse.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── index.html
│   └── test/
│       └── java/
└── pom.xml
```

## 🚦 Usage Examples

### Create a Customer
```bash
curl -X POST http://localhost:8090/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+1234567890",
    "address": "123 Main St",
    "role": "CUSTOMER",
    "password": "password123",
    "active": true
  }'
```

### Create Service Usage
```bash
curl -X POST http://localhost:8090/api/service-usage \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {"id": 1},
    "serviceType": "DATA",
    "quantity": 10.5,
    "ratePerUnit": 0.50,
    "usageDate": "2026-01-03T10:30:00"
  }'
```

### Create Invoice
```bash
curl -X POST http://localhost:8090/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {"id": 1},
    "billingPeriodStart": "2026-01-01",
    "billingPeriodEnd": "2026-01-31",
    "subtotal": 100.00,
    "tax": 18.00
  }'
```

### Process Payment
```bash
curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "invoice": {"id": 1},
    "amount": 118.00,
    "paymentMethod": "CREDIT_CARD",
    "status": "SUCCESS"
  }'
```

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**: Change the port in `application.properties`
   ```properties
   server.port=8091
   ```

2. **Database connection issues**: Check H2 console settings match configuration

3. **CORS errors**: Verify CORS configuration allows your frontend origin

## 🔄 Future Enhancements

- [ ] Authentication with JWT
- [ ] Password encryption with BCrypt
- [ ] PDF invoice generation
- [ ] Email notifications
- [ ] Payment gateway integration
- [ ] Analytics dashboard
- [ ] Export data to CSV/Excel
- [ ] Multi-language support
- [ ] SMS notifications
- [ ] Auto-invoice generation scheduler

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

## 📞 Contact

For any queries or support, please contact:
- **Project**: MSG Telecom: Postpaid Billing System
- **Email**: support@msgtelecom.com

## 🙏 Acknowledgments

- Spring Boot Team
- AlpineJS Team
- Tailwind CSS Team
- H2 Database Team

---

**Note**: This is a development version. For production deployment, ensure to:
- Use a persistent database (PostgreSQL, MySQL, etc.)
- Implement proper authentication and authorization
- Add SSL/TLS encryption
- Enable proper logging and monitoring
- Implement rate limiting
- Add comprehensive test coverage
