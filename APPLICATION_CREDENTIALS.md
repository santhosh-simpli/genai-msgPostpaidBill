# 🔐 MSG Telecom Postpaid Billing System - Access Credentials

**Status:** Application Running on Port 8080  
**Date:** January 14, 2026

---

## 🌐 Swagger API Documentation

**Swagger UI URL:** http://localhost:8080/swagger-ui.html  
**API Docs (JSON):** http://localhost:8080/v3/api-docs

---

## 👥 Default Users & Passwords

### Admin Users (First 2 Users)

| Username | Password | Email | Role |
|----------|----------|-------|------|
| `john.smith` | `password123` | john.smith@example.com | ADMIN |
| `sarah.johnson` | `password123` | sarah.johnson@example.com | ADMIN |

### Customer Users (18 Users)

| # | Username | Password | Email | Phone |
|---|----------|----------|-------|-------|
| 1 | `michael.williams` | `password123` | michael.williams@example.com | +1-202-302-1000 |
| 2 | `emily.brown` | `password123` | emily.brown@example.com | +1-203-303-1100 |
| 3 | `david.jones` | `password123` | david.jones@example.com | +1-204-304-1200 |
| 4 | `jessica.garcia` | `password123` | jessica.garcia@example.com | +1-205-305-1300 |
| 5 | `james.miller` | `password123` | james.miller@example.com | +1-206-306-1400 |
| 6 | `ashley.davis` | `password123` | ashley.davis@example.com | +1-207-307-1500 |
| 7 | `robert.rodriguez` | `password123` | robert.rodriguez@example.com | +1-208-308-1600 |
| 8 | `amanda.martinez` | `password123` | amanda.martinez@example.com | +1-209-309-1700 |
| 9 | `william.hernandez` | `password123` | william.hernandez@example.com | +1-210-310-1800 |
| 10 | `jennifer.lopez` | `password123` | jennifer.lopez@example.com | +1-211-311-1900 |
| 11 | `richard.gonzalez` | `password123` | richard.gonzalez@example.com | +1-212-312-2000 |
| 12 | `lisa.wilson` | `password123` | lisa.wilson@example.com | +1-213-313-2100 |
| 13 | `thomas.anderson` | `password123` | thomas.anderson@example.com | +1-214-314-2200 |
| 14 | `michelle.thomas` | `password123` | michelle.thomas@example.com | +1-215-315-2300 |
| 15 | `daniel.taylor` | `password123` | daniel.taylor@example.com | +1-216-316-2400 |
| 16 | `karen.moore` | `password123` | karen.moore@example.com | +1-217-317-2500 |
| 17 | `christopher.jackson` | `password123` | christopher.jackson@example.com | +1-218-318-2600 |
| 18 | `nancy.martin` | `password123` | nancy.martin@example.com | +1-219-319-2700 |

---

## 🔑 Login Credentials Summary

**All Credentials:**
- **Password:** `password123` (same for all users)
- **Email:** `{firstname}.{lastname}@example.com`
- **Username Format:** `firstname.lastname` (lowercase)

---

## 📱 API Testing

### Step 1: Get Authentication Token

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "username": "john.smith",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "john.smith",
  "role": "ADMIN"
}
```

### Step 2: Use Token in Requests

**Header:**
```
Authorization: Bearer {accessToken}
```

---

## 🧪 Sample Endpoints

### 1. Login
```
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "john.smith",
  "password": "password123"
}
```

### 2. Get Current User
```
GET http://localhost:8080/api/v1/users/me
Authorization: Bearer {token}
```

### 3. List All Customers
```
GET http://localhost:8080/api/v1/customers
Authorization: Bearer {token}
```

### 4. Get User Profile
```
GET http://localhost:8080/api/v1/users/{userId}
Authorization: Bearer {token}
```

### 5. Register New User
```
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "SecurePassword123!",
  "confirmPassword": "SecurePassword123!"
}
```

---

## 🎯 Swagger UI Features

Once you visit `http://localhost:8080/swagger-ui.html`, you can:

1. ✅ View all available API endpoints
2. ✅ See request/response schemas
3. ✅ Test endpoints directly in the UI
4. ✅ Generate client code
5. ✅ View API documentation

**Swagger Features:**
- Interactive API documentation
- Try-it-out functionality
- Authentication token management
- Request/response examples
- Schema visualization

---

## 🔐 Security Notes

- All passwords are **encrypted** using BCrypt
- JWT tokens expire after **1 hour** (3600 seconds)
- Admin users have full access to all resources
- Customer users have restricted access to their own data
- All API endpoints require authentication (except `/auth/login` and `/auth/register`)

---

## 📊 Database Sample Data

**Each user has:**
- ✅ 1-3 Services (Mobile, Broadband, Cable TV, VoIP)
- ✅ 3-7 Usage Records per service
- ✅ 2-4 Invoices
- ✅ Payments for paid invoices
- ✅ Customer profile with address and phone

---

## 🚀 Quick Start

1. **Visit Swagger UI:**
   - http://localhost:8080/swagger-ui.html

2. **Login with Admin Account:**
   - Username: `john.smith`
   - Password: `password123`

3. **Explore APIs:**
   - Use Swagger UI to test endpoints
   - Copy token from login response
   - Use token in "Authorize" button

---

## 📝 Common API Responses

### Success (200 OK)
```json
{
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2026-01-14T12:00:00Z"
}
```

### Unauthorized (401)
```json
{
  "message": "Invalid credentials",
  "timestamp": "2026-01-14T12:00:00Z"
}
```

### Not Found (404)
```json
{
  "message": "Resource not found",
  "timestamp": "2026-01-14T12:00:00Z"
}
```

---

## 🎪 Environment

- **Server:** Apache Tomcat 10.1.17
- **Port:** 8080
- **Java Version:** 21
- **Spring Boot:** 3.2.1
- **Database:** H2 (In-Memory)
- **Authentication:** JWT (JSON Web Tokens)
- **API Documentation:** OpenAPI 3.0 (Swagger)

---

## 📞 API Base URL

```
http://localhost:8080/api/v1
```

**Available Resource Paths:**
- `/auth` - Authentication endpoints
- `/users` - User management
- `/customers` - Customer management
- `/services` - Service management
- `/invoices` - Invoice management
- `/payments` - Payment management
- `/notifications` - Notification management

---

**Status:** ✅ Application Running  
**Last Updated:** January 14, 2026  
**Test Coverage:** 90% (760 tests)
