# Reverse Engineering Analysis - Legacy POS System

## Executive Summary

This document captures the reverse-engineered architecture, design patterns, data structures, and identified code/data smells from the legacy SG Technologies POS system.

## System Overview

The legacy POS is a desktop Java application that handles:
- **Sales**: Direct item sales with tax calculation
- **Rentals**: Item rentals tracked by customer phone number
- **Returns**: Processing returns for rented items
- **Employee Management**: Admin functions for managing cashiers/admins
- **Inventory Management**: Real-time inventory updates

## Architecture Analysis

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    POSSystem (Entry Point)                │
│  - Authentication (login/logout)                          │
│  - Role-based routing (Cashier vs Admin)                 │
│  - Employee log file management                          │
└──────────────────┬────────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
┌───────▼────────┐   ┌────────▼──────────┐
│  Cashier Flow  │   │   Admin Flow       │
│  - POS (Sale)  │   │   - EmployeeMgmt   │
│  - POR (Rental)│   │   - Add/Update/Del │
│  - POH (Return)│   │     Employees      │
└───────┬────────┘   └────────────────────┘
        │
┌───────▼──────────────────────────────────────┐
│         PointOfSale (Abstract Base)            │
│  - Transaction management                     │
│  - Item cart operations                       │
│  - Tax/coupon calculation                     │
│  - Temp file persistence                      │
└───────┬───────────────────────────────────────┘
        │
┌───────▼──────────────────────────────────────┐
│           Inventory (Singleton)              │
│  - File-based inventory access               │
│  - Inventory updates (add/remove items)     │
└──────────────────────────────────────────────┘
```

### Class Hierarchy

**Core Domain Classes:**
- `Employee`: Username, name, position, password (plain text)
- `Item`: itemID, itemName, price, amount (inventory quantity)
- `ReturnItem`: itemID, daysSinceReturn

**Transaction Classes:**
- `PointOfSale` (abstract): Base for all transaction types
  - `POS`: Direct sales
  - `POR`: Rentals (extends PointOfSale)
  - `POH`: Returns (extends PointOfSale)

**Management Classes:**
- `POSSystem`: Entry point, authentication, routing
- `Inventory`: Singleton for inventory operations
- `Management`: Customer/rental database operations
- `EmployeeManagement`: Employee CRUD operations

**UI Classes (Swing-based):**
- `Login_Interface`
- `Cashier_Interface`
- `Admin_Interface`
- `Transaction_Interface`
- `Payment_Interface`
- `EnterItem_Interface`
- `AddEmployee_Interface`
- `UpdateEmployee_Interface`

## Design Patterns Identified

1. **Singleton Pattern**: `Inventory.getInstance()` - ensures single inventory instance
2. **Abstract Factory Pattern**: `PointOfSale` abstract class with concrete implementations (POS, POR, POH)
3. **Template Method Pattern**: `PointOfSale` defines skeleton (enterItem, updateTotal) with abstract methods (endPOS, deleteTempItem, retrieveTemp)

## Data Storage Analysis

### File-Based Storage

All data persisted in plain text files under `Database/`:

1. **employeeDatabase.txt**: Format `username position firstName lastName password`
2. **itemDatabase.txt**: Format `itemID itemName price amount`
3. **userDatabase.txt**: Format `phoneNumber itemID1,date1,returned1 itemID2,date2,returned2 ...`
4. **rentalDatabase.txt**: Format `itemID itemName price amount` (duplicate of itemDatabase?)
5. **saleInvoiceRecord.txt**: Transaction logs with timestamps
6. **returnSale.txt**: Return transaction logs
7. **employeeLogfile.txt**: Login/logout audit trail
8. **couponNumber.txt**: Valid coupon codes (one per line)
9. **temp.txt**: Temporary transaction state (for session recovery)

### Data Format Issues

- **No schema validation**: Files can be corrupted without detection
- **No transaction support**: Partial writes can corrupt data
- **No concurrency control**: Multiple users can overwrite each other
- **Inconsistent delimiters**: Mix of spaces and commas
- **No referential integrity**: Orphaned records possible

## Code Smells Identified

### 1. **God Class**
- **`POSSystem`**: Handles authentication, file I/O, routing, logging (210+ lines)
- **`PointOfSale`**: Manages transactions, file I/O, calculations, temp files (246 lines)
- **`Management`**: Customer lookup, rental management, date calculations (387+ lines)

### 2. **Long Method**
- `Management.checkUser()`: 40+ lines with nested try-catch
- `Management.getLatestReturnDate()`: 80+ lines with complex parsing
- `PointOfSale.coupon()`: File I/O mixed with business logic
- `POS.endPOS()`: 50+ lines doing inventory update, file deletion, invoice logging

### 3. **Feature Envy**
- `PointOfSale` directly manipulates file paths and performs I/O
- `POS`, `POR`, `POH` duplicate temp file deletion logic
- Multiple classes check OS type and adjust file paths

### 4. **Data Clumps**
- File path strings repeated across classes: `"Database/..."` patterns
- OS detection code duplicated: `System.getProperty("os.name").startsWith("W")`
- Date formatting: `SimpleDateFormat` instantiated multiple times

### 5. **Primitive Obsession**
- Phone numbers stored as `long` (no validation, no type safety)
- Prices as `float` (should use `BigDecimal` for currency)
- Dates as strings in various formats (`MM/dd/yy`, `yyyy-MM-dd HH:mm:ss.SSS`)

### 6. **Duplicate Code**
- `deleteTempItem()` implemented identically in POS, POR, POH (30+ lines each)
- File reading patterns repeated: BufferedReader/FileReader try-catch blocks
- OS detection logic scattered across 10+ classes

### 7. **Magic Numbers/Strings**
- Tax rate: `1.06` hardcoded (should be configurable)
- Discount: `0.90f` hardcoded
- Credit card length: `16` hardcoded in `creditCard()` validation
- File paths hardcoded as string literals

### 8. **Inappropriate Intimacy**
- `PointOfSale` directly accesses `Inventory.getInstance()` (tight coupling)
- `POR`/`POH` directly instantiate `Management` (should use dependency injection)
- UI classes likely tightly coupled to business logic (needs verification)

### 9. **Comments Indicating Problems**
- `Management.checkUser()`: Comment says "needs to be cleaned up.. written with terrible style"
- Multiple commented-out OS detection blocks suggest unstable file path handling

### 10. **Error Handling Issues**
- Swallowed exceptions: `catch(IOException e){}` in `Inventory.updateInventory()`
- Inconsistent error messages
- No logging framework (uses `System.out.println`)

## Data Smells Identified

### 1. **No Normalization**
- `userDatabase.txt` stores denormalized rental history (phone + all items in one line)
- No separate customer table
- No separate rental transaction table

### 2. **No Data Validation**
- No constraints on item quantities (can go negative?)
- No validation on employee passwords (stored plain text)
- No date format validation

### 3. **Inconsistent Data Formats**
- Employee DB: space-separated
- Item DB: space-separated  
- User DB: space-separated with comma-separated sub-fields
- Invoice logs: mixed formats

### 4. **No Audit Trail Integrity**
- Log files append-only with no checksums
- No way to detect tampering
- No transaction IDs for traceability

### 5. **Data Duplication**
- `rentalDatabase.txt` appears to duplicate `itemDatabase.txt`
- Customer phone numbers stored in multiple places

## Workflow Analysis

### Sale Workflow
1. Cashier logs in → `POSSystem.logIn()`
2. Select "Sale" → Create `POS` instance
3. `POS.startNew(itemDatabaseFile)` → Load inventory
4. `enterItem(itemID, amount)` → Add to cart
5. `updateTotal()` → Calculate running total
6. Optional: `coupon(couponNo)` → Apply discount
7. `endPOS(itemDatabaseFile)` → Apply tax, update inventory, log invoice, clear cart

### Rental Workflow
1. Cashier logs in → Select "Rental"
2. Enter phone number → `Management.checkUser()` → Create if not exists
3. Create `POR(phoneNum)` instance
4. `startNew(rentalDatabaseFile)` → Load inventory
5. `enterItem()` → Add items to rental cart
6. `endPOS()` → Update inventory, save rental to `userDatabase.txt`

### Return Workflow
1. Cashier logs in → Select "Return"
2. Enter phone number → `Management.getLatestReturnDate(phone)` → Get outstanding rentals
3. Create `POH(phone)` instance
4. `startNew(rentalDatabaseFile)` → Load inventory
5. `enterItem()` → Add items to return cart
6. `endPOS()` → Restore inventory, mark items as returned in `userDatabase.txt`

## Dependencies

### External Dependencies
- Java Standard Library only (no external frameworks)
- Swing for UI (not analyzed in detail)

### Internal Dependencies
```
POSSystem
  ├── Employee
  ├── EmployeeManagement
  └── (routes to) POS/POR/POH

POS/POR/POH
  ├── PointOfSale (extends)
  ├── Inventory (Singleton)
  ├── Item
  └── Management (for rentals/returns)

Management
  └── ReturnItem

Inventory
  └── Item
```

## Performance Concerns

1. **File I/O on Every Operation**: No caching, reads entire files into memory
2. **O(n) Searches**: Linear search through employee/item lists
3. **No Connection Pooling**: N/A (file-based)
4. **Synchronous Operations**: All file operations block

## Security Concerns

1. **Plain Text Passwords**: `Employee.password` stored unencrypted
2. **No Input Validation**: SQL injection N/A, but file path injection possible
3. **No Access Control**: File system permissions only protection
4. **No Session Management**: Temp files used for state (vulnerable to tampering)

## Testing Coverage

**Existing Tests:**
- `EmployeeTest`: Single test for `getUsername()`
- Minimal coverage (< 5% estimated)

**Missing Tests:**
- Authentication flows
- Inventory operations
- Transaction calculations (tax, discounts)
- File I/O error handling
- Concurrent access scenarios

## Recommendations for Reengineering

### Immediate Refactoring Targets
1. Extract file I/O into repository pattern
2. Replace magic numbers with constants/config
3. Consolidate duplicate `deleteTempItem()` implementations
4. Extract OS detection into utility class
5. Replace `float` with `BigDecimal` for currency

### Architectural Improvements
1. Separate presentation, business logic, and data access layers
2. Introduce dependency injection
3. Replace file storage with database (PostgreSQL recommended)
4. Implement proper authentication (password hashing, session management)
5. Add comprehensive test coverage

### Data Model Improvements
1. Normalize data model (Customers, Items, Transactions, Rentals tables)
2. Add foreign key constraints
3. Implement audit logging with transaction IDs
4. Add data validation at persistence layer

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Analyst**: Senior Reengineering Team

