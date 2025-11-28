# Forward Engineering - Web-Based POS System

## Overview

This document outlines the forward engineering phase, implementing the reengineered POS system as a modern web-based application using Spring Boot (backend) and React (frontend).

## Technology Stack Selection

### Backend: Spring Boot

**Rationale**:
- **Java Ecosystem**: Reuses existing Java knowledge from legacy system
- **Mature Framework**: Industry-standard, well-documented
- **Dependency Injection**: Built-in IoC container for clean architecture
- **JPA/Hibernate**: Excellent ORM support for database integration
- **Spring Security**: Robust authentication and authorization
- **RESTful APIs**: Easy to build and consume
- **Testing Support**: Comprehensive testing framework integration

### Frontend: React + TypeScript

**Rationale**:
- **Modern UI Framework**: Component-based, reusable architecture
- **TypeScript**: Type safety reduces runtime errors
- **Large Ecosystem**: Rich library ecosystem (React Router, Material-UI)
- **Performance**: Virtual DOM for efficient rendering
- **Developer Experience**: Hot reload, excellent tooling
- **Industry Standard**: Widely adopted, good job market

### Database: PostgreSQL

**Rationale**:
- **ACID Compliance**: Full transaction support
- **Relational Model**: Well-suited for POS data structure
- **Performance**: Handles concurrent access efficiently
- **Spring Integration**: Excellent JPA support
- **Open Source**: No licensing costs

## Architecture Design

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  React SPA (TypeScript + Material-UI)  │
│  - Login/Logout                         │
│  - Cashier Dashboard                    │
│  - Admin Dashboard                      │
│  - Sales/Rentals/Returns UI             │
└──────────────┬──────────────────────────┘
               │ HTTP/REST
┌──────────────▼──────────────────────────┐
│         API Layer                       │
│  Spring Boot REST Controllers           │
│  - AuthenticationController             │
│  - SaleController                       │
│  - RentalController                     │
│  - ReturnController                     │
│  - InventoryController                  │
│  - EmployeeController                   │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Business Logic Layer               │
│  Spring Services                        │
│  - AuthService                          │
│  - SaleService                          │
│  - RentalService                        │
│  - InventoryService                     │
│  - EmployeeService                      │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│      Data Access Layer                  │
│  Spring Data JPA Repositories           │
│  - EmployeeRepository                   │
│  - ItemRepository                       │
│  - SaleRepository                       │
│  - RentalRepository                    │
│  - CustomerRepository                  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│         Database Layer                  │
│  PostgreSQL Database                    │
│  - Normalized schema                    │
│  - Foreign key constraints              │
│  - Indexes for performance             │
└─────────────────────────────────────────┘
```

## Project Structure

### Backend Structure (Spring Boot)

```
pos-backend/
├── src/main/java/com/sgtech/pos/
│   ├── PosApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── SaleController.java
│   │   ├── RentalController.java
│   │   ├── ReturnController.java
│   │   ├── InventoryController.java
│   │   └── EmployeeController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── SaleService.java
│   │   ├── RentalService.java
│   │   ├── ReturnService.java
│   │   ├── InventoryService.java
│   │   └── EmployeeService.java
│   ├── repository/
│   │   ├── EmployeeRepository.java
│   │   ├── ItemRepository.java
│   │   ├── SaleRepository.java
│   │   ├── RentalRepository.java
│   │   ├── CustomerRepository.java
│   │   └── ReturnRepository.java
│   ├── model/
│   │   ├── Employee.java
│   │   ├── Item.java
│   │   ├── Sale.java
│   │   ├── Rental.java
│   │   ├── Customer.java
│   │   └── Return.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── SaleRequest.java
│   │   ├── RentalRequest.java
│   │   └── ReturnRequest.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
├── src/main/resources/
│   ├── application.properties
│   └── application-dev.properties
└── pom.xml
```

### Frontend Structure (React)

```
pos-frontend/
├── src/
│   ├── App.tsx
│   ├── index.tsx
│   ├── components/
│   │   ├── Login.tsx
│   │   ├── Dashboard.tsx
│   │   ├── SaleForm.tsx
│   │   ├── RentalForm.tsx
│   │   ├── ReturnForm.tsx
│   │   └── InventoryList.tsx
│   ├── pages/
│   │   ├── CashierDashboard.tsx
│   │   ├── AdminDashboard.tsx
│   │   ├── SalesPage.tsx
│   │   ├── RentalsPage.tsx
│   │   └── ReturnsPage.tsx
│   ├── services/
│   │   ├── api.ts
│   │   ├── authService.ts
│   │   ├── saleService.ts
│   │   ├── rentalService.ts
│   │   └── inventoryService.ts
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   └── useInventory.ts
│   ├── types/
│   │   ├── Employee.ts
│   │   ├── Item.ts
│   │   ├── Sale.ts
│   │   └── Rental.ts
│   └── utils/
│       ├── constants.ts
│       └── formatters.ts
├── package.json
└── tsconfig.json
```

## Key Features Implementation

### 1. Authentication & Authorization

**Backend (Spring Security)**:
- JWT token-based authentication
- Password hashing with BCrypt
- Role-based access control (Admin/Cashier)
- Session management

**Frontend**:
- Login page with form validation
- Token storage in localStorage
- Protected routes based on role
- Auto-logout on token expiration

### 2. Sales Processing

**Backend**:
- RESTful API endpoints
- Transaction management (@Transactional)
- Inventory updates
- Tax calculation
- Coupon validation

**Frontend**:
- Item search and selection
- Shopping cart management
- Real-time total calculation
- Receipt generation

### 3. Rental Management

**Backend**:
- Customer lookup/creation
- Rental transaction processing
- Due date calculation
- Overdue tracking

**Frontend**:
- Customer phone lookup
- Item selection for rental
- Due date display
- Rental history

### 4. Return Processing

**Backend**:
- Outstanding rental lookup
- Return validation
- Inventory restoration
- Refund calculation

**Frontend**:
- Customer rental history
- Item selection for return
- Overdue indicator
- Return confirmation

### 5. Inventory Management

**Backend**:
- Real-time inventory queries
- Stock level updates
- Low stock alerts

**Frontend**:
- Inventory list with search
- Stock level indicators
- Item details view

### 6. Employee Management (Admin Only)

**Backend**:
- CRUD operations for employees
- Password reset functionality

**Frontend**:
- Employee list
- Add/Edit employee forms
- Role assignment

## API Design

### RESTful Endpoints

```
Authentication:
POST   /api/auth/login
POST   /api/auth/logout
GET    /api/auth/me

Sales:
POST   /api/sales
GET    /api/sales/{id}
GET    /api/sales?employeeId={id}&startDate={date}&endDate={date}

Rentals:
POST   /api/rentals
GET    /api/rentals/{id}
GET    /api/rentals/customer/{phone}
GET    /api/rentals/outstanding/{phone}

Returns:
POST   /api/returns
GET    /api/returns/{id}

Inventory:
GET    /api/items
GET    /api/items/{id}
PUT    /api/items/{id}/quantity
GET    /api/items/low-stock

Customers:
GET    /api/customers/phone/{phone}
POST   /api/customers

Employees (Admin only):
GET    /api/employees
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}
```

## Design Patterns Applied

1. **Repository Pattern**: Data access abstraction
2. **Service Layer Pattern**: Business logic separation
3. **DTO Pattern**: Data transfer objects for API
4. **Dependency Injection**: Spring IoC container
5. **Singleton Pattern**: Service beans (Spring managed)
6. **Factory Pattern**: Object creation (Spring factories)
7. **Strategy Pattern**: Different payment/calculation strategies

## Testing Strategy

### Backend Testing
- **Unit Tests**: JUnit 5 for services and repositories
- **Integration Tests**: @SpringBootTest for API endpoints
- **Repository Tests**: @DataJpaTest for data layer
- **Security Tests**: MockMvc for authentication

### Frontend Testing
- **Unit Tests**: Jest + React Testing Library
- **Component Tests**: Render and interaction testing
- **Integration Tests**: API mocking with MSW
- **E2E Tests**: Cypress (optional)

## Deployment Strategy

### Development
- Backend: `mvn spring-boot:run` (port 8080)
- Frontend: `npm start` (port 3000)
- Database: Local PostgreSQL instance

### Production
- **Backend**: Docker container or cloud deployment (AWS/Heroku)
- **Frontend**: Static hosting (Netlify/Vercel) or CDN
- **Database**: Managed PostgreSQL (AWS RDS/Heroku Postgres)

## Migration from Legacy System

### Data Migration
1. Run migration scripts to load legacy data
2. Validate data integrity
3. Test with migrated data

### Feature Parity
1. Implement all legacy features
2. Maintain same business logic
3. Improve UX where possible

### Rollout Strategy
1. Deploy to staging environment
2. User acceptance testing
3. Gradual rollout to production
4. Legacy system as backup during transition

## Improvements Over Legacy System

1. **Architecture**: Layered, maintainable, testable
2. **Data Storage**: Normalized database vs. flat files
3. **Security**: Password hashing, JWT tokens vs. plain text
4. **Concurrency**: Database transactions vs. file locking issues
5. **Scalability**: Web-based, multiple users vs. desktop single-user
6. **Maintainability**: Modern frameworks, clear separation of concerns
7. **Testing**: Comprehensive test coverage vs. minimal tests
8. **User Experience**: Modern web UI vs. Swing desktop

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Ready for Implementation

