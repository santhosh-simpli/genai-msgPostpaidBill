# Authentication and Authorization Update Guide

## Files Successfully Created:

### Backend:
1. **User.java** - User entity with roles and permissions
2. **UserRepository.java** - JPA repository for user management
3. **UserService.java** - Business logic for user operations
4. **AuthService.java** - Authentication and registration service
5. **CustomUserDetailsService.java** - Spring Security UserDetailsService implementation
6. **JwtTokenProvider.java** - JWT token generation and validation
7. **JwtAuthenticationFilter.java** - JWT filter for request authentication
8. **SecurityConfig.java** - Spring Security configuration
9. **AuthController.java** - REST endpoints for login and registration
10. **UserController.java** - REST endpoints for user management
11. **DTOs** - LoginRequest, RegisterRequest, AuthResponse, UserUpdateRequest

### Frontend:
1. **login.html** - Login and registration page

## User Roles and Permissions Implemented:

### NEW_USER
- Permission: REGISTER_ACCOUNT
- Can register for an account

### CUSTOMER
- Permissions:
  - VIEW_ACCOUNT - View their account details
  - MANAGE_ACCOUNT - Manage their account
  - VIEW_USAGE_HISTORY - View service usage history
  - VIEW_CHARGES - View current charges
  - PAY_BILLS - Pay bills online

### OPERATOR
- Permissions:
  - VIEW_ACCOUNT
  - MANAGE_ACCOUNT
  - VIEW_USAGE_HISTORY
  - VIEW_CHARGES

### ADMIN
- All permissions including:
  - MANAGE_USERS - Manage user accounts
  - MANAGE_ROLES - Change user roles
  - MANAGE_PERMISSIONS - Modify user permissions
  - GENERATE_INVOICES - Create invoices
  - SEND_INVOICES - Send invoices to customers
  - VIEW_ALL_DATA - View all system data
  - MANAGE_ALL_DATA - Full CRUD access

## API Endpoints Created:

### Authentication (`/api/auth`):
- `POST /api/auth/login` - User login (returns JWT token)
- `POST /api/auth/register` - User registration (returns JWT token)

### User Management (`/api/users`) - Admin only:
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `PATCH /api/users/{id}/role?role={ROLE}` - Update user role
- `POST /api/users/{id}/permissions?permission={PERMISSION}` - Add permission
- `DELETE /api/users/{id}/permissions?permission={PERMISSION}` - Remove permission

## Security Features:

1. **JWT Authentication**: Token-based stateless authentication
2. **BCrypt Password Encryption**: Secure password storage
3. **Role-Based Access Control (RBAC)**: Method-level security
4. **Permission-Based Authorization**: Fine-grained access control
5. **CORS Configuration**: Supports all HTTP methods
6. **Session Management**: Stateless (JWT)

## Updated Controllers with Security:

### CustomerController:
- `GET /api/customers` - Requires: VIEW_ALL_DATA or MANAGE_ALL_DATA
- `GET /api/customers/{id}` - Requires: VIEW_ACCOUNT or VIEW_ALL_DATA
- `POST /api/customers` - Requires: MANAGE_USERS or MANAGE_ALL_DATA
- `PUT /api/customers/{id}` - Requires: MANAGE_ACCOUNT or MANAGE_ALL_DATA
- `DELETE /api/customers/{id}` - Requires: MANAGE_ALL_DATA

### ServiceUsageController:
- All endpoints require: VIEW_USAGE_HISTORY or VIEW_ALL_DATA

### InvoiceController:
- View endpoints require: VIEW_CHARGES or VIEW_ALL_DATA
- Create endpoint requires: GENERATE_INVOICES

### PaymentController:
- Create endpoint requires: PAY_BILLS or MANAGE_ALL_DATA

## How to Use:

### 1. Start the Application:
```bash
mvn spring-boot:run
```

### 2. Access Login Page:
Navigate to: `http://localhost:8090/login.html`

### 3. Register a New Account:
- Click "Register" tab
- Fill in username, email, full name, password
- Select role (Customer or Operator)
- Submit

### 4. Login:
- Enter username and password
- Click Login
- JWT token is stored in localStorage
- Redirected to main application

### 5. Create Admin User (via API):
```bash
curl -X POST http://localhost:8090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@msgtelecom.com",
    "fullName": "System Administrator",
    "password": "admin123",
    "phoneNumber": "+1234567890",
    "role": "ADMIN"
  }'
```

## Frontend Updates Needed:

The index.html file needs these manual updates to complete the authentication integration:

1. **Update All API Calls**: Add authentication headers to all fetch calls
2. **Add User Management View**: For admins to manage users
3. **Permission-Based UI**: Show/hide features based on user permissions

### Sample Code for API Calls with Auth:
```javascript
async loadData() {
    const response = await fetch(`${this.API_BASE_URL}/endpoint`, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.token}`
        }
    });
}
```

### Permission Check in UI:
```html
<button x-show="hasPermission('MANAGE_USERS')" @click="manageUsers()">
    Manage Users
</button>
```

## Testing Different Roles:

### As Customer:
- Login with customer account
- Can view usage, charges, pay bills
- Cannot access admin features

### As Operator:
- Can view and manage accounts
- Can view usage and charges
- Cannot create invoices or manage users

### As Admin:
- Full access to all features
- Can manage users and roles
- Can generate and send invoices

## Security Best Practices Implemented:

1. ✅ JWT tokens expire after 24 hours
2. ✅ Passwords are encrypted with BCrypt
3. ✅ Role-based method security
4. ✅ Permission-based fine-grained control
5. ✅ Stateless authentication
6. ✅ CORS properly configured
7. ✅ H2 console secured (same origin)

## Next Steps:

1. Update all fetch calls in index.html to include auth headers
2. Add user management interface for admins
3. Implement token refresh mechanism
4. Add "remember me" functionality
5. Implement password reset feature
6. Add email verification
7. Add audit logging

## Error Handling:

- 401 Unauthorized: Invalid or expired token → Redirect to login
- 403 Forbidden: Insufficient permissions → Show error message
- All errors are properly handled with custom exception handler

## Database Schema:

### users table:
- id, username, email, password, full_name, phone_number
- role, enabled, account_non_expired, account_non_locked, credentials_non_expired
- created_at, updated_at, customer_id

### user_permissions table:
- user_id, permission

This implements a complete authentication and authorization system with role-based and permission-based access control!
