# Data Restructuring - Legacy POS System

## Overview

This document outlines the data restructuring phase, migrating from file-based storage to a proper relational database (PostgreSQL) with normalized schema, referential integrity, and improved data quality.

## Current Data Storage Issues

### File-Based Storage Problems

1. **No Normalization**: Denormalized data in userDatabase.txt (phone + all rentals in one line)
2. **No Referential Integrity**: Orphaned records possible, no foreign key constraints
3. **No Transaction Support**: Partial writes can corrupt data
4. **No Concurrency Control**: Multiple users can overwrite each other
5. **Inconsistent Formats**: Mix of space and comma delimiters
6. **No Data Validation**: No constraints on quantities, dates, or formats
7. **No Audit Trail Integrity**: Log files append-only with no checksums

## Target Database Schema

### Technology Choice: PostgreSQL

**Rationale**:
- **Mature and Reliable**: Industry-standard relational database
- **ACID Compliance**: Full transaction support
- **Rich Feature Set**: JSON support, full-text search, advanced indexing
- **Spring Boot Integration**: Excellent JPA/Hibernate support
- **Open Source**: No licensing costs
- **Performance**: Handles concurrent access efficiently

### Entity Relationship Diagram

```
┌─────────────────┐
│   employees     │
├─────────────────┤
│ id (PK)         │
│ username (UK)   │
│ first_name      │
│ last_name       │
│ position        │
│ password_hash   │
│ created_at      │
│ updated_at      │
└─────────────────┘
         │
         │ (1:N)
         │
┌────────▼─────────┐
│  audit_logs      │
├──────────────────┤
│ id (PK)          │
│ employee_id (FK) │
│ action           │
│ timestamp        │
│ details          │
└──────────────────┘

┌─────────────────┐
│     items       │
├─────────────────┤
│ id (PK)         │
│ item_id (UK)    │
│ name            │
│ price           │
│ quantity        │
│ created_at      │
│ updated_at      │
└─────────────────┘
         │
         │ (1:N)
         │
┌────────▼─────────┐
│  sale_items      │
├──────────────────┤
│ id (PK)          │
│ sale_id (FK)     │
│ item_id (FK)     │
│ quantity         │
│ unit_price       │
│ subtotal         │
└──────────────────┘

┌─────────────────┐
│     sales       │
├─────────────────┤
│ id (PK)         │
│ employee_id(FK) │
│ total_amount    │
│ tax_amount      │
│ discount_amount │
│ final_total     │
│ coupon_code     │
│ transaction_date│
│ created_at      │
└─────────────────┘

┌─────────────────┐
│   customers     │
├─────────────────┤
│ id (PK)         │
│ phone (UK)      │
│ first_name      │
│ last_name       │
│ email           │
│ created_at      │
│ updated_at      │
└─────────────────┘
         │
         │ (1:N)
         │
┌────────▼─────────┐
│    rentals       │
├──────────────────┤
│ id (PK)          │
│ customer_id (FK) │
│ employee_id (FK) │
│ rental_date      │
│ due_date         │
│ total_amount     │
│ tax_amount       │
│ created_at       │
└──────────────────┘
         │
         │ (1:N)
         │
┌────────▼─────────┐
│  rental_items    │
├──────────────────┤
│ id (PK)          │
│ rental_id (FK)   │
│ item_id (FK)     │
│ quantity         │
│ unit_price       │
│ returned         │
│ return_date      │
│ days_overdue     │
└──────────────────┘

┌─────────────────┐
│    returns      │
├─────────────────┤
│ id (PK)         │
│ rental_id (FK)  │
│ employee_id(FK)│
│ return_date     │
│ total_refund    │
│ created_at      │
└─────────────────┘
         │
         │ (1:N)
         │
┌────────▼─────────┐
│  return_items    │
├──────────────────┤
│ id (PK)          │
│ return_id (FK)   │
│ rental_item_id(FK)│
│ quantity         │
│ refund_amount    │
└──────────────────┘

┌─────────────────┐
│    coupons      │
├─────────────────┤
│ id (PK)         │
│ code (UK)       │
│ discount_percent│
│ active          │
│ valid_from       │
│ valid_to         │
└─────────────────┘
```

## Database Schema DDL

### PostgreSQL Schema

```sql
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Employees table
CREATE TABLE employees (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    position VARCHAR(20) NOT NULL CHECK (position IN ('Admin', 'Cashier')),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items table
CREATE TABLE items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id INTEGER UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers table
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Coupons table
CREATE TABLE coupons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent DECIMAL(5, 2) NOT NULL CHECK (discount_percent >= 0 AND discount_percent <= 100),
    active BOOLEAN DEFAULT TRUE,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales table
CREATE TABLE sales (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID NOT NULL REFERENCES employees(id),
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    tax_amount DECIMAL(10, 2) NOT NULL CHECK (tax_amount >= 0),
    discount_amount DECIMAL(10, 2) DEFAULT 0 CHECK (discount_amount >= 0),
    final_total DECIMAL(10, 2) NOT NULL CHECK (final_total >= 0),
    coupon_code VARCHAR(50) REFERENCES coupons(code),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sale items table
CREATE TABLE sale_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sale_id UUID NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES items(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rentals table
CREATE TABLE rentals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    employee_id UUID NOT NULL REFERENCES employees(id),
    rental_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    tax_amount DECIMAL(10, 2) NOT NULL CHECK (tax_amount >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rental items table
CREATE TABLE rental_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rental_id UUID NOT NULL REFERENCES rentals(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES items(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    returned BOOLEAN DEFAULT FALSE,
    return_date TIMESTAMP,
    days_overdue INTEGER DEFAULT 0 CHECK (days_overdue >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Returns table
CREATE TABLE returns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rental_id UUID NOT NULL REFERENCES rentals(id),
    employee_id UUID NOT NULL REFERENCES employees(id),
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_refund DECIMAL(10, 2) NOT NULL CHECK (total_refund >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Return items table
CREATE TABLE return_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    return_id UUID NOT NULL REFERENCES returns(id) ON DELETE CASCADE,
    rental_item_id UUID NOT NULL REFERENCES rental_items(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    refund_amount DECIMAL(10, 2) NOT NULL CHECK (refund_amount >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit logs table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID REFERENCES employees(id),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    details JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_employees_username ON employees(username);
CREATE INDEX idx_items_item_id ON items(item_id);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_sales_employee ON sales(employee_id);
CREATE INDEX idx_sales_date ON sales(transaction_date);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_rentals_customer ON rentals(customer_id);
CREATE INDEX idx_rental_items_rental ON rental_items(rental_id);
CREATE INDEX idx_rental_items_returned ON rental_items(returned);
CREATE INDEX idx_audit_logs_employee ON audit_logs(employee_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
```

## Data Migration Strategy

### Migration Steps

1. **Parse Legacy Files**: Read all .txt database files
2. **Data Cleaning**: 
   - Deduplicate employees
   - Validate phone numbers
   - Normalize item names
   - Parse dates consistently
3. **Transform Data**: Convert to normalized structure
4. **Load to Database**: Insert with foreign key relationships
5. **Validation**: Verify data integrity and completeness

### Migration Script Structure

```java
// Pseudo-code for migration
1. Read employeeDatabase.txt → employees table
2. Read itemDatabase.txt → items table
3. Read userDatabase.txt → customers + rentals + rental_items
4. Read saleInvoiceRecord.txt → sales + sale_items
5. Read returnSale.txt → returns + return_items
6. Read couponNumber.txt → coupons table
7. Read employeeLogfile.txt → audit_logs table
```

## Schema Improvements

### Normalization Benefits

1. **First Normal Form (1NF)**: Atomic values, no repeating groups
2. **Second Normal Form (2NF)**: No partial dependencies
3. **Third Normal Form (3NF)**: No transitive dependencies

### Data Quality Improvements

1. **Constraints**: CHECK constraints for valid data ranges
2. **Foreign Keys**: Referential integrity enforced
3. **Unique Constraints**: Prevent duplicate usernames, phone numbers
4. **NOT NULL**: Required fields enforced
5. **Default Values**: Sensible defaults for timestamps, booleans

### Security Improvements

1. **Password Hashing**: Store bcrypt hashes instead of plain text
2. **Audit Trail**: Complete transaction history in audit_logs
3. **Data Validation**: Database-level validation prevents bad data

## Migration Plan

### Phase 1: Schema Creation
- Create all tables with constraints
- Set up indexes
- Create sequences/UUID generation

### Phase 2: Data Migration
- Write migration scripts
- Parse legacy files
- Transform and load data
- Validate migrated data

### Phase 3: Application Integration
- Update application to use database
- Implement repository pattern
- Add transaction management
- Update tests

### Phase 4: Verification
- Compare legacy vs. migrated data
- Run integration tests
- Performance testing
- Rollback plan if needed

## Repository Pattern Implementation

### Interface Design

```java
// EmployeeRepository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsername(String username);
    List<Employee> findByPosition(String position);
}

// ItemRepository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByItemId(Integer itemId);
    List<Item> findByQuantityGreaterThan(int minQuantity);
}

// CustomerRepository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByPhone(String phone);
}

// SaleRepository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByEmployeeIdAndTransactionDateBetween(
        UUID employeeId, LocalDateTime start, LocalDateTime end);
}

// RentalRepository
public interface RentalRepository extends JpaRepository<Rental, UUID> {
    List<Rental> findByCustomerId(UUID customerId);
    List<Rental> findByDueDateBeforeAndReturnedFalse(LocalDate date);
}
```

## Benefits of New Schema

1. **Scalability**: Handles concurrent transactions efficiently
2. **Data Integrity**: Foreign keys and constraints prevent corruption
3. **Query Performance**: Indexes optimize common queries
4. **Maintainability**: Normalized structure easier to understand
5. **Extensibility**: Easy to add new features (loyalty programs, discounts)
6. **Security**: Password hashing, audit trails
7. **Reporting**: Easy to generate reports and analytics

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Ready for Implementation

