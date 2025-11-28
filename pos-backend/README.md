# POS Backend - Spring Boot Application

## Overview

Spring Boot REST API backend for the reengineered POS system.

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

## Setup

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE pos_db;

-- Run schema
\i database/schema.sql
```

### 2. Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pos_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/health` - Health check

### Items/Inventory
- `GET /api/items` - Get all items
- `GET /api/items/{id}` - Get item by UUID
- `GET /api/items/item-id/{itemId}` - Get item by item ID
- `GET /api/items/search?name={name}` - Search items
- `GET /api/items/low-stock?threshold={n}` - Get low stock items

### Sales
- `POST /api/sales` - Process a sale (requires X-Employee-Id header)
- `GET /api/sales/{id}` - Get sale by ID
- `GET /api/sales/employee/{employeeId}` - Get sales by employee

### Rentals
- `POST /api/rentals` - Process a rental (requires X-Employee-Id header)
- `GET /api/rentals/{id}` - Get rental by ID
- `GET /api/rentals/customer/{phone}` - Get rentals by customer phone
- `GET /api/rentals/outstanding/{phone}` - Get outstanding rentals

### Returns
- `POST /api/returns` - Process a return (requires X-Employee-Id header)
- `GET /api/returns/{id}` - Get return by ID

### Employees (Admin only)
- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee

## Data Migration

To migrate data from legacy .txt files:

```bash
POST /api/migration/migrate?databasePath=Database
```

## Testing

```bash
mvn test
```

