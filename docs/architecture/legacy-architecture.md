# Legacy Architecture Documentation
## Reverse-Engineered System Architecture

---

## Overview

This document describes the reverse-engineered architecture of the legacy SG Technologies POS System, extracted through code analysis and system understanding.

---

## Architecture Style

**Type**: Monolithic Desktop Application  
**Pattern**: Procedural with object-oriented classes  
**Coupling**: Tight coupling between layers  
**Separation**: Minimal separation of concerns

---

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────┐
│              Legacy POS System Architecture          │
└─────────────────────────────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
┌───────▼────────┐            ┌─────────▼────────┐
│  Presentation  │            │   Application    │
│     Layer      │            │     Layer        │
│                │            │                  │
│  Swing UI      │◄──────────►│  POSSystem       │
│  Components    │            │  (Entry Point)   │
│                │            │                  │
│ - Login UI     │            │ - Authentication │
│ - Cashier UI   │            │ - Routing        │
│ - Admin UI     │            │ - Logging        │
│ - Transaction  │            │                  │
│   UI           │            │                  │
└───────┬────────┘            └─────────┬────────┘
        │                               │
        └───────────────┬───────────────┘
                        │
        ┌───────────────▼───────────────┐
        │      Business Logic Layer     │
        │                               │
        │  ┌─────────────────────┐     │
        │  │   PointOfSale       │     │
        │  │   (Abstract Base)   │     │
        │  └──────────┬──────────┘     │
        │             │                │
        │  ┌──────────┼──────────┐     │
        │  │          │          │     │
        │ ┌▼──┐    ┌─▼──┐    ┌─▼──┐   │
        │ │POS│    │POR │    │POH │   │
        │ │Sale│    │Rent│    │Ret │   │
        │ └───┘    └────┘    └────┘   │
        │                               │
        │  ┌─────────────────────┐     │
        │  │   Inventory         │     │
        │  │   (Singleton)       │     │
        │  └──────────┬──────────┘     │
        │             │                │
        │  ┌──────────▼──────────┐     │
        │  │   Management        │     │
        │  │   (Customer/Rental) │     │
        │  └─────────────────────┘     │
        │                               │
        │  ┌─────────────────────┐     │
        │  │ EmployeeManagement  │     │
        │  └─────────────────────┘     │
        └───────────────┬───────────────┘
                        │
        ┌───────────────▼───────────────┐
        │     Data Persistence Layer    │
        │                               │
        │   File System (Database/)     │
        │                               │
        │ - employeeDatabase.txt        │
        │ - itemDatabase.txt            │
        │ - userDatabase.txt            │
        │ - saleInvoiceRecord.txt       │
        │ - returnSale.txt              │
        │ - couponNumber.txt            │
        │ - employeeLogfile.txt         │
        │ - temp.txt                    │
        └───────────────────────────────┘
```

---

## Component Descriptions

### 1. Presentation Layer

**Components**: Swing UI Classes
- `Login_Interface.java` - Login screen
- `Cashier_Interface.java` - Cashier dashboard
- `Admin_Interface.java` - Admin dashboard
- `Transaction_Interface.java` - Transaction screens
- `Payment_Interface.java` - Payment processing UI
- `EnterItem_Interface.java` - Item entry UI
- `AddEmployee_Interface.java` - Add employee UI
- `UpdateEmployee_Interface.java` - Update employee UI

**Characteristics**:
- Direct coupling to business logic
- No MVC separation
- Business logic embedded in UI event handlers

### 2. Application Layer

**Component**: `POSSystem.java`

**Responsibilities**:
- Application entry point (`main()` method)
- User authentication
- Role-based routing (Cashier vs Admin)
- Employee log file management
- System initialization

**Key Methods**:
- `logIn(String username, String password)` - Authentication
- `logOut()` - Logout and logging
- Routing logic to appropriate interfaces

### 3. Business Logic Layer

#### 3.1 Transaction Processing

**Abstract Base**: `PointOfSale.java`
- Template method pattern
- Common transaction logic
- Abstract methods for specific transaction types

**Concrete Implementations**:
- `POS.java` - Direct sales processing
- `POR.java` - Rental processing
- `POH.java` - Return processing

**Key Operations**:
- `startNew(String file)` - Initialize transaction
- `enterItem(int itemID, int quantity)` - Add item to cart
- `updateTotal()` - Calculate running total
- `coupon(String couponNo)` - Apply coupon discount
- `endPOS(String file)` - Complete transaction
- `deleteTempItem(int id)` - Remove item from cart

#### 3.2 Inventory Management

**Component**: `Inventory.java` (Singleton Pattern)

**Responsibilities**:
- Inventory access and updates
- Item lookup
- Quantity management

**Key Methods**:
- `getInstance()` - Get singleton instance
- `accessInventory(String file)` - Load inventory from file
- `updateInventory(String file, int itemID, int quantity)` - Update item quantity

#### 3.3 Customer and Rental Management

**Component**: `Management.java`

**Responsibilities**:
- Customer lookup and creation
- Rental history management
- Return date calculations
- Outstanding rental queries

**Key Methods**:
- `checkUser(long phoneNumber)` - Find or create customer
- `getLatestReturnDate(long phone)` - Get outstanding rentals

#### 3.4 Employee Management

**Component**: `EmployeeManagement.java`

**Responsibilities**:
- Employee CRUD operations
- Employee data validation
- File-based employee storage

**Key Methods**:
- `addEmployee(Employee emp)` - Create new employee
- `updateEmployee(String username, Employee emp)` - Update employee
- `deleteEmployee(String username)` - Delete employee

### 4. Domain Models

**Components**:
- `Employee.java` - Employee entity
- `Item.java` - Item entity
- `ReturnItem.java` - Return item entity
- `Sale.java` - Sale transaction (unused?)
- `Rental.java` - Rental transaction (unused?)
- `Register.java` - Register entity (unused?)

**Characteristics**:
- Simple data classes
- No business logic
- Direct file I/O in some cases

### 5. Data Persistence Layer

**Storage Type**: File-based (text files)

**Files**:
1. `employeeDatabase.txt` - Employee records
2. `itemDatabase.txt` - Inventory items
3. `userDatabase.txt` - Customer and rental data (denormalized)
4. `saleInvoiceRecord.txt` - Sales transaction logs
5. `returnSale.txt` - Return transaction logs
6. `couponNumber.txt` - Valid coupon codes
7. `employeeLogfile.txt` - Login/logout audit trail
8. `temp.txt` - Temporary transaction state
9. `rentalDatabase.txt` - Rental inventory (duplicate?)

**Characteristics**:
- No database abstraction
- Direct file I/O operations
- No transaction support
- No concurrency control
- No schema validation

---

## Design Patterns Identified

### 1. Singleton Pattern

**Implementation**: `Inventory.getInstance()`

**Purpose**: Ensure single inventory instance across application

**Code Example**:
```java
public class Inventory {
    private static Inventory instance = null;
    
    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }
}
```

### 2. Abstract Factory Pattern

**Implementation**: `PointOfSale` abstract class with concrete implementations

**Purpose**: Create transaction objects based on type (Sale, Rental, Return)

**Structure**:
```
PointOfSale (abstract)
├── POS (concrete - Sales)
├── POR (concrete - Rentals)
└── POH (concrete - Returns)
```

### 3. Template Method Pattern

**Implementation**: `PointOfSale` class

**Purpose**: Define skeleton algorithm with variable steps

**Template Methods**:
- `enterItem()` - Common logic, calls abstract `retrieveTemp()`
- `updateTotal()` - Common calculation logic
- `endPOS()` - Abstract, implemented by subclasses

---

## Data Flow

### Sale Transaction Flow

```
1. Cashier UI → POSSystem.logIn()
2. POSSystem → Creates POS instance
3. POS.startNew() → Inventory.getInstance().accessInventory()
4. Inventory → Reads itemDatabase.txt
5. Cashier UI → POS.enterItem()
6. POS → Updates temp.txt, calculates total
7. Cashier UI → POS.coupon() (optional)
8. Cashier UI → POS.endPOS()
9. POS → Updates itemDatabase.txt (inventory)
10. POS → Writes to saleInvoiceRecord.txt
11. POS → Deletes temp.txt
```

### Rental Transaction Flow

```
1. Cashier UI → POSSystem.logIn()
2. POSSystem → Creates POR instance with phone number
3. POR.startNew() → Inventory.accessInventory()
4. Cashier UI → Management.checkUser(phone)
5. Management → Reads/creates entry in userDatabase.txt
6. Cashier UI → POR.enterItem()
7. POR → Updates temp.txt
8. Cashier UI → POR.endPOS()
9. POR → Updates itemDatabase.txt
10. POR → Writes to userDatabase.txt (denormalized)
11. POR → Deletes temp.txt
```

---

## Architectural Issues

### 1. Tight Coupling

- UI classes directly instantiate business logic classes
- No dependency injection
- Hard-coded dependencies

### 2. No Separation of Concerns

- Business logic mixed with UI
- File I/O operations scattered throughout
- No clear layer boundaries

### 3. No Abstraction

- Direct file I/O (no repository pattern)
- Hard-coded file paths
- No data access abstraction

### 4. No Transaction Management

- File operations not atomic
- No rollback mechanism
- Data corruption risk

### 5. Singleton Anti-Pattern

- Inventory singleton creates hidden dependencies
- Difficult to test
- Not thread-safe (though single-user system)

---

## Dependencies

### Internal Dependencies

```
POSSystem
  ├── Employee
  ├── EmployeeManagement
  └── routes to → POS/POR/POH

POS/POR/POH
  ├── extends PointOfSale
  ├── Inventory (Singleton)
  ├── Item
  └── Management (for rentals/returns)

Inventory
  └── Item

Management
  └── ReturnItem
```

### External Dependencies

- Java Standard Library only
- Swing/AWT for UI
- File system access
- No external frameworks

---

## Deployment Architecture

```
┌─────────────────────────────┐
│    Desktop Machine          │
│                             │
│  ┌─────────────────────┐   │
│  │  Java Runtime       │   │
│  │  (JRE 8+)           │   │
│  └──────────┬──────────┘   │
│             │               │
│  ┌──────────▼──────────┐   │
│  │  POS Application    │   │
│  │  (SGTechnologies.jar)│   │
│  └──────────┬──────────┘   │
│             │               │
│  ┌──────────▼──────────┐   │
│  │  File System        │   │
│  │  (Database/ folder) │   │
│  └─────────────────────┘   │
└─────────────────────────────┘
```

**Characteristics**:
- Standalone desktop application
- No network requirements
- Local file system storage
- Single-user deployment

---

## Performance Characteristics

**Strengths**:
- Fast startup (no database connection)
- Simple file operations
- Low memory footprint (for small datasets)

**Weaknesses**:
- File I/O on every operation (no caching)
- O(n) searches through files
- Performance degrades with large files
- No indexing for fast lookups

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Reverse-Engineered Architecture Complete

