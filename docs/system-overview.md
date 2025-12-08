# System Overview - Legacy POS System
## SG Technologies Point-of-Sale System

---

## System Purpose and Scope

The SG Technologies Point-of-Sale (POS) System is a desktop Java application designed to manage retail operations for a small to medium-sized business. The system handles sales transactions, item rentals, returns, inventory management, and employee administration.

### Purpose

The system enables retail employees (cashiers and administrators) to:
- Process direct sales transactions with tax calculation
- Manage item rentals to customers
- Process returns for rented items
- Track inventory levels in real-time
- Manage employee accounts and access control

### Scope

**In Scope:**
- Sales processing with tax calculation (6% tax rate)
- Rental management with customer phone number lookup
- Return processing with overdue calculation
- Inventory tracking and updates
- Employee authentication and role-based access
- Coupon code validation and discount application
- Transaction logging and audit trails

**Out of Scope:**
- Payment processing (external system)
- Accounting integration
- Advanced reporting and analytics
- Multi-store support
- Customer loyalty programs
- Barcode scanning hardware integration

---

## High-Level Architecture

### Architecture Style

**Legacy System**: Monolithic Desktop Application

```
┌─────────────────────────────────────────┐
│      SG Technologies POS System          │
│         (Desktop Application)            │
└─────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌───────▼──────┐        ┌───────▼───────┐
│   Swing UI   │        │  POSSystem    │
│  Components  │◄──────►│  (Monolithic) │
│              │        │               │
│ - Login      │        │ - Auth        │
│ - Cashier    │        │ - Routing     │
│ - Admin      │        │ - Logging     │
│ - Transaction│        │               │
└───────┬──────┘        └───────┬───────┘
        │                       │
        └───────────┬───────────┘
                    │
        ┌───────────▼───────────┐
        │  Business Logic       │
        │                       │
        │ - POS (Sales)         │
        │ - POR (Rentals)       │
        │ - POH (Returns)       │
        │ - Inventory           │
        │ - Employee Management │
        └───────────┬───────────┘
                    │
        ┌───────────▼───────────┐
        │   Data Persistence    │
        │                       │
        │ File-based Storage    │
        │ - employeeDatabase.txt│
        │ - itemDatabase.txt    │
        │ - userDatabase.txt    │
        │ - saleInvoiceRecord.txt│
        │ - returnSale.txt      │
        │ - couponNumber.txt    │
        │ - employeeLogfile.txt │
        └───────────────────────┘
```

### Key Components

1. **User Interface Layer** (Swing Components)
   - Login interface
   - Cashier dashboard
   - Admin dashboard
   - Transaction interfaces (Sale, Rental, Return)
   - Payment interface
   - Employee management interfaces

2. **Business Logic Layer** (Mixed with UI)
   - `POSSystem`: Entry point, authentication, routing
   - `POS`: Sales processing
   - `POR`: Rental processing
   - `POH`: Return processing
   - `Inventory`: Inventory management (Singleton)
   - `EmployeeManagement`: Employee CRUD operations
   - `Management`: Customer and rental operations

3. **Data Persistence Layer** (File-based)
   - Text file storage in `Database/` directory
   - No database abstraction layer
   - Direct file I/O operations

---

## Key Features

### 1. Sales Processing

**Functionality:**
- Add items to sale cart by item ID
- Calculate running total
- Apply coupon codes (10% discount)
- Apply 6% sales tax
- Update inventory after sale
- Generate invoice record

**Key Classes:**
- `POS` (extends `PointOfSale`)
- `Inventory` (Singleton)

**Data Files:**
- `itemDatabase.txt` - Item lookup
- `couponNumber.txt` - Coupon validation
- `saleInvoiceRecord.txt` - Transaction logging

### 2. Rental Management

**Functionality:**
- Customer lookup by phone number
- Create new customer if not exists
- Add items to rental cart
- Calculate rental total with tax
- Track due dates for returns
- Update inventory for rented items

**Key Classes:**
- `POR` (extends `PointOfSale`)
- `Management` - Customer operations

**Data Files:**
- `userDatabase.txt` - Customer and rental data (denormalized)
- `rentalDatabase.txt` - Rental inventory
- `itemDatabase.txt` - Item lookup

### 3. Return Processing

**Functionality:**
- Lookup customer by phone number
- Display outstanding rentals
- Select items to return
- Calculate days overdue
- Restore inventory
- Mark items as returned

**Key Classes:**
- `POH` (extends `PointOfSale`)
- `Management` - Rental lookup

**Data Files:**
- `userDatabase.txt` - Rental lookup
- `returnSale.txt` - Return transaction logging

### 4. Inventory Management

**Functionality:**
- View all items in inventory
- Real-time quantity updates
- Item lookup by ID
- Inventory reduction on sales/rentals
- Inventory restoration on returns

**Key Classes:**
- `Inventory` (Singleton pattern)
- `Item` (domain model)

**Data Files:**
- `itemDatabase.txt` - Inventory storage

### 5. Employee Management (Admin Only)

**Functionality:**
- Add new employees
- Update employee information
- Delete employees
- Assign roles (Admin/Cashier)
- Manage credentials

**Key Classes:**
- `EmployeeManagement`
- `Employee` (domain model)

**Data Files:**
- `employeeDatabase.txt` - Employee storage

### 6. Authentication and Authorization

**Functionality:**
- Username/password authentication
- Role-based access control
- Login/logout tracking
- Session management (implied through UI state)

**Key Classes:**
- `POSSystem` - Authentication logic
- `Employee` - Credential storage

**Data Files:**
- `employeeDatabase.txt` - Credential storage (plain text)
- `employeeLogfile.txt` - Login audit trail

---

## Technology Stack

### Legacy System Stack

**Programming Language:**
- Java 8 (Java Standard Edition)

**UI Framework:**
- Java Swing (AWT-based)
- Java Swing Components:
  - JFrame, JPanel, JButton
  - JTextField, JLabel
  - JTable, JList
  - Dialog boxes

**Build System:**
- Apache Ant (`build.xml`)
- NetBeans IDE project files

**Data Storage:**
- File-based storage (text files)
- No database system
- No ORM or data access framework

**Dependencies:**
- Java Standard Library only
- No external libraries or frameworks

**Deployment:**
- Desktop application
- Executable JAR file (`SGTechnologies.jar`)
- Single-user application
- Local file system access required

---

## System Boundaries

### System Boundaries Diagram

```
┌────────────────────────────────────────────┐
│         POS System Boundary                │
│                                            │
│  ┌──────────────┐  ┌──────────────┐      │
│  │   Cashier    │  │    Admin     │      │
│  │  Interface   │  │  Interface   │      │
│  └──────┬───────┘  └──────┬───────┘      │
│         │                 │               │
│  ┌──────▼─────────────────▼───────┐      │
│  │      Business Logic Layer      │      │
│  │  (POS, POR, POH, Inventory)    │      │
│  └──────┬─────────────────────────┘      │
│         │                                 │
│  ┌──────▼─────────────────────────┐      │
│  │    File System (Database/)     │      │
│  └────────────────────────────────┘      │
└────────────────────────────────────────────┘
         │                    │
         │                    │
    ┌────▼────┐         ┌─────▼─────┐
    │ Cashier │         │   Admin   │
    │  User   │         │   User    │
    └─────────┘         └───────────┘
```

### External Interfaces

**User Interfaces:**
- Cashier: Interactive Swing GUI for transaction processing
- Admin: Interactive Swing GUI for employee and inventory management

**File System Interfaces:**
- Read/write operations on text files in `Database/` directory
- Temporary file operations for transaction state

**No External System Integration:**
- No payment gateway integration
- No accounting system integration
- No reporting system integration
- No email or notification system
- No network communication (single machine)

### System Constraints

1. **Platform**: Java 8+ required, Swing components require desktop OS
2. **Storage**: Local file system access required for `Database/` directory
3. **Concurrency**: Single-user application (no multi-user support)
4. **Data Format**: Fixed-format text files (space/comma delimited)
5. **Security**: Plain text password storage (security vulnerability)

---

## Operational Context

### Deployment Environment

- **Type**: Desktop application
- **Platform**: Windows, macOS, or Linux (with Java 8+)
- **Installation**: JAR file execution
- **Network**: No network requirements (standalone)

### User Roles

1. **Cashier**
   - Process sales
   - Process rentals
   - Process returns
   - View inventory

2. **Admin**
   - All cashier functions
   - Manage employees (add, update, delete)
   - Manage inventory

### Usage Patterns

- **Primary Users**: Retail cashiers and store managers
- **Usage Frequency**: Daily during business hours
- **Transaction Volume**: Low to medium (typical retail store)
- **Concurrent Users**: Single user at a time

---

## Known Limitations

1. **Single User**: Cannot support multiple concurrent users
2. **File-Based Storage**: No transaction support, data corruption risk
3. **No Backup**: No automated backup mechanism
4. **Security**: Plain text passwords (major security issue)
5. **Scalability**: Performance degrades with large data files
6. **Platform Dependency**: Requires Java and Swing support
7. **Data Integrity**: No referential integrity constraints
8. **Error Recovery**: Limited error recovery mechanisms

---

## System Evolution Context

**Original Development**: 
- Created as part of CSE216 - Software Engineering course (December 2015)
- Alpha release with basic functionality
- Beta release with bug fixes

**Current State**:
- Legacy system maintained as-is
- Minimal documentation updates
- No major feature additions since beta

**Reengineering Context**:
- System being reengineered to modern web-based architecture
- Migration from file-based to database storage
- Migration from desktop to web application
- Technology stack modernization

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**System**: Legacy POS System (Pre-Reengineering)  
**Status**: Baseline Documentation Complete

