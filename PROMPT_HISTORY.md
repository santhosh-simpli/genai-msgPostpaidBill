# Prompt History - MSG Telecom Postpaid Billing System

This file contains a categorized summary and the literal text of user prompts used to create and enhance the application.

---

## 1. Project Initialization & Setup
- Create a Spring Boot project for a postpaid billing system.
- Scaffold a Maven project with Java 21, Spring Web, Spring Data JPA, H2, Spring Security, Lombok.
- Add JWT authentication and role-based access (ADMIN, OPERATOR, CUSTOMER).
- Configure H2 in-memory database for development.

## 2. Data Model & Database
- Design entities for User, Customer, Service, UsageRecord, Invoice, Payment.
- Add JPA relationships: User 1:1 Customer, Customer 1:N Services, Service 1:N UsageRecords, Customer 1:N Invoices, Invoice 1:N Payments.
- Add firstName and lastName fields to Customer.
- Add sample/test data initialization on startup.
- Provide SQL queries for H2 console to view users, customers, invoices, payments, usage records, and statistics.

## 3. Backend API
- Implement REST controllers for authentication, users, customers, services, usage, invoices, payments, notifications.
- Add endpoints for login, register, CRUD operations, and business actions (generate invoice, make payment, send notification).
- Secure endpoints with JWT and @PreAuthorize annotations.
- Change authentication endpoints from `/api/v1/auth` to `/api`.
- Validate all API endpoints against requirements and generate a validation report.

## 4. Frontend
- Build a responsive SPA using Alpine.js and Tailwind CSS.
- Implement login, registration, dashboard, usage history, invoices, payments, and admin panel views.
- Add customer names to dropdowns and history.
- Show service details in usage history.
- Add record count, filter, and reset in usage history.
- Connect frontend to backend using fetch API and JWT tokens.
- Update API_BASE_URL and AUTH_BASE_URL to `/api` in frontend files.

## 5. UX & Features
- Implement password change modal and backend endpoint.
- Auto-refresh dropdowns after adding users/customers.
- Show empty state messages and record counts in usage history.
- Display payment status as “Completed” in payment history, but keep invoice status as “PAID” in backend.
- Add color-coded status badges and responsive layouts.

## 6. Testing & Validation
- Add test users and sample data on startup.
- Write SQL queries for H2 console demo.
- Add cURL and browser console snippets for API testing.
- Validate frontend, backend, and database connections.
- Generate connection validation report.

## 7. Documentation & Demo
- Generate demo guides for frontend, backend, and database.
- Create markdown files with sample queries, API calls, and demo scripts.
- Document deployment options (Heroku, Azure, etc.).
- Provide demo scenarios and pro tips for presentations.

## 8. Deployment
- Provide GitHub Actions workflow for CI/CD.
- Explain free and easy deployment options (Heroku).
- Give instructions for creating a Heroku account and connecting GitHub.
- Generate a production URL example for the deployed app.

## 9. Version Control
- Commit and push code changes to the remote repository.
- Stage, commit, and push documentation and code updates as needed.

---

# Literal User Prompts (Chronological)

1. still I can see we can improve the user experience by adding the new user and customer in payments and invoice dropdowns, replace @example.com to @msgtel.com, Update the payment and invoice details at the top of history with user/customer name, Under usage history - add respective service details
2. Under usage history, add sample data for all service types (Mobile, Broadband, Cable TV, VoIP) with random values for each type.
3. update form users and customers also along with static user data
4. when a new user or customer is added from admin panel, these new created users/customers are not visible in invoice drop down and invoice generation dropdown. Let the customer can also change or update his password
5. how can we get the id from static users?
6. In usage history, when any service type is selected, render some sample data
7. still the issue persists
8. commit and push the code to git
9. run
10. Validate these api's are implemented as per the requirement or not
11. commit and push the code
12. after the payment is done, let the status is updated to completed inplace of pending
13. After the invoice payment, let invoices be updated as paid but in payment history showcase as completed instead of pending.
14. Validate front end , backend and db connections are interconnected properly or not.. if not please establish a connection with proper url's as per the standards.
15. run
16. Explain frontend for the demo
17. add some snippets here to add in demo
18. Explain backend for the demo along with snippets
19. explain db along with snippets
20. run the code
21. can you create screenshots of UI?
22. commit and push the code
23. commit and push the code
24. generate one production url of this webiste
25. deploy using github
26. which process is free and easy ?
27. create an heroku account and install HEroku CLI
28. how to create heroku account?
29. generate all the prompts used for creating this app.
30. Try Again
31. generate all the prompts used for creating this app.
32. yes, reconstruct all the prompts based on categories
33. create a mork down file and also literal text of each propmt
