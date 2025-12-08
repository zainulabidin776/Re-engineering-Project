# Sequence Diagrams - Legacy POS System
## Key Workflow Interactions

This document provides sequence diagrams for all major workflows in the legacy POS system.

---

## Workflow Overview

The legacy POS system has three main transaction workflows:
1. **Sale Transaction** - Direct item sales
2. **Rental Transaction** - Item rentals to customers
3. **Return Transaction** - Processing returns for rented items

Additional workflows:
4. **Login/Authentication** - User authentication
5. **Employee Management** - Admin functions

---

## 1. Sale Transaction Workflow

**Actors**: Cashier, POSSystem, POS, Inventory, File System

```
┌─────────┐   ┌──────────┐   ┌──────┐   ┌──────────┐   ┌─────────────┐
│ Cashier │   │POSSystem │   │ POS  │   │Inventory │   │File System  │
└────┬────┘   └────┬─────┘   └───┬──┘   └────┬─────┘   └──────┬──────┘
     │             │              │           │                │
     │──login()───>│              │           │                │
     │             │──validate()─>│           │                │
     │             │              │           │                │
     │<──1 (cashier)──────────────│           │                │
     │             │              │           │                │
     │──startSale()──────────────>│           │                │
     │             │──new POS()──>│           │                │
     │             │              │──startNew()───────────────>│
     │             │              │           │──readFile()───>│
     │             │              │           │                │
     │             │              │<──items───────<────────────│
     │             │<──POS────────│           │                │
     │<──POS───────│              │           │                │
     │             │              │           │                │
     │──enterItem(id, qty)───────>│           │                │
     │             │              │──enterItem()───────────────>│
     │             │              │──writeTemp()───────────────>│
     │             │              │           │                │
     │             │              │<──true───────<──────────────│
     │<──true──────│              │           │                │
     │             │              │           │                │
     │──updateTotal()────────────>│           │                │
     │             │              │──updateTotal()              │
     │             │              │──calculate()                │
     │<──total─────│              │           │                │
     │             │              │           │                │
     │──coupon(code)─────────────>│           │                │
     │             │              │──coupon()──────────────────>│
     │             │              │──readCouponFile()──────────>│
     │             │              │           │                │
     │             │              │<──valid───────<─────────────│
     │<──discount──│              │           │                │
     │             │              │           │                │
     │──endPOS()─────────────────>│           │                │
     │             │              │──endPOS()                  │
     │             │              │──updateInventory()────────>│
     │             │              │           │──updateFile()─>│
     │             │              │           │                │
     │             │              │──writeInvoice()───────────>│
     │             │              │           │                │
     │             │              │──deleteTemp()─────────────>│
     │             │              │           │                │
     │<──receipt───│              │           │                │
```

**Key Steps**:
1. Cashier logs in
2. Cashier starts sale transaction
3. POS loads inventory from file
4. Cashier adds items to cart (stored in temp file)
5. System calculates running total
6. Optional: Apply coupon discount
7. Complete transaction: update inventory, log invoice, clear temp file

---

## 2. Rental Transaction Workflow

**Actors**: Cashier, POSSystem, POR, Management, Inventory, File System

```
┌─────────┐   ┌──────────┐   ┌──────┐   ┌───────────┐   ┌──────────┐   ┌─────────────┐
│ Cashier │   │POSSystem │   │ POR  │   │Management │   │Inventory │   │File System  │
└────┬────┘   └────┬─────┘   └───┬──┘   └─────┬─────┘   └────┬─────┘   └──────┬──────┘
     │             │              │            │              │                │
     │──login()───>│              │            │              │                │
     │<──1─────────│              │            │              │                │
     │             │              │            │              │                │
     │──startRental(phone)───────>│            │              │                │
     │             │──new POR(phone)──────────>│              │                │
     │             │              │──checkUser(phone)────────>│                │
     │             │              │            │──readUserDB()───────────────>│
     │             │              │            │                │              │
     │             │              │<──found───────<───────────────────────────│
     │             │              │            │              │                │
     │             │              │──startNew()───────────────>│              │
     │             │              │            │──readRentalDB()─────────────>│
     │             │              │<──items───────────────<───────────────────│
     │             │<──POR────────│            │              │                │
     │<──POR───────│              │            │              │                │
     │             │              │            │              │                │
     │──enterItem(id, qty)───────>│            │              │                │
     │             │              │──enterItem()──────────────>│              │
     │             │              │──writeTemp()──────────────>│              │
     │<──true──────│              │            │              │                │
     │             │              │            │              │                │
     │──endPOS()─────────────────>│            │              │                │
     │             │              │──endPOS()                 │                │
     │             │              │──updateInventory()───────>│                │
     │             │              │            │──updateFile()───────────────>│
     │             │              │            │              │                │
     │             │              │──saveRental()────────────>│                │
     │             │              │            │──writeUserDB()──────────────>│
     │             │              │            │              │                │
     │             │              │──deleteTemp()────────────>│                │
     │<──receipt───│              │            │              │                │
```

**Key Steps**:
1. Cashier logs in
2. Cashier enters customer phone number
3. System checks if customer exists (or creates new)
4. POR loads rental inventory
5. Cashier adds items to rental cart
6. Complete rental: update inventory, save to userDatabase.txt (denormalized)
7. Clear temp file

---

## 3. Return Transaction Workflow

**Actors**: Cashier, POSSystem, POH, Management, Inventory, File System

```
┌─────────┐   ┌──────────┐   ┌──────┐   ┌───────────┐   ┌──────────┐   ┌─────────────┐
│ Cashier │   │POSSystem │   │ POH  │   │Management │   │Inventory │   │File System  │
└────┬────┘   └────┬─────┘   └───┬──┘   └─────┬─────┘   └────┬─────┘   └──────┬──────┘
     │             │              │            │              │                │
     │──login()───>│              │            │              │                │
     │<──1─────────│              │            │              │                │
     │             │              │            │              │                │
     │──startReturn(phone)───────>│            │              │                │
     │             │──new POH(phone)──────────>│              │                │
     │             │              │──getLatestReturnDate(phone)───────────────>│
     │             │              │            │──readUserDB()───────────────>│
     │             │              │            │──parseRentals()              │
     │             │              │<──rentals───                              │
     │             │              │            │              │                │
     │<──outstanding items────────│            │              │                │
     │             │              │            │              │                │
     │──selectItems(items)───────>│            │              │                │
     │             │              │──startNew()───────────────>│              │
     │             │              │            │──readRentalDB()─────────────>│
     │             │              │<──items───────────────<───────────────────│
     │             │              │            │              │                │
     │──enterItem(id, qty)───────>│            │              │                │
     │             │              │──enterItem()──────────────>│              │
     │             │              │──writeTemp()──────────────>│              │
     │<──true──────│              │            │              │                │
     │             │              │            │              │                │
     │──endPOS()─────────────────>│            │              │                │
     │             │              │──endPOS()                 │                │
     │             │              │──restoreInventory()──────>│                │
     │             │              │            │──updateFile()───────────────>│
     │             │              │            │              │                │
     │             │              │──markReturned()──────────>│                │
     │             │              │            │──updateUserDB()─────────────>│
     │             │              │            │              │                │
     │             │              │──writeReturnLog()────────>│                │
     │             │              │            │              │                │
     │             │              │──deleteTemp()────────────>│                │
     │<──receipt───│              │            │              │                │
```

**Key Steps**:
1. Cashier logs in
2. Cashier enters customer phone number
3. System queries for outstanding rentals
4. System displays items with days overdue
5. Cashier selects items to return
6. POH loads rental inventory
7. Complete return: restore inventory, mark items as returned in userDatabase.txt
8. Log return transaction
9. Clear temp file

---

## 4. Login/Authentication Workflow

**Actors**: User (Cashier/Admin), POSSystem, File System

```
┌───────┐   ┌──────────┐   ┌─────────────┐
│ User  │   │POSSystem │   │File System  │
└───┬───┘   └────┬─────┘   └──────┬──────┘
    │            │                 │
    │──login(username, pwd)──────>│
    │            │                 │
    │            │──readEmployeeDB()─────────>│
    │            │                 │          │
    │            │<──employees─────<──────────│
    │            │                 │          │
    │            │──validate(username, pwd)   │
    │            │──getPosition()             │
    │            │                 │          │
    │            │──writeLog("LOGIN")────────>│
    │            │                 │          │
    │<──1 (cashier) or 2 (admin)──│          │
    │            │                 │          │
    │──route to dashboard────────>│          │
```

**Key Steps**:
1. User enters username and password
2. System reads employee database file
3. System validates credentials (plain text comparison!)
4. System gets employee position (Cashier/Admin)
5. System logs login action
6. System routes to appropriate dashboard (Cashier or Admin)

**Security Issues**:
- ⚠️ Passwords stored and compared in plain text
- ⚠️ No password hashing
- ⚠️ No session management
- ⚠️ No timeout mechanism

---

## 5. Employee Management Workflow (Admin Only)

**Actors**: Admin, AdminInterface, EmployeeManagement, File System

```
┌───────┐   ┌──────────────┐   ┌──────────────────┐   ┌─────────────┐
│ Admin │   │AdminInterface│   │EmployeeManagement│   │File System  │
└───┬───┘   └──────┬───────┘   └────────┬─────────┘   └──────┬──────┘
    │              │                     │                    │
    │──addEmployee(emp)────────────────>│                    │
    │              │                     │──readEmployees()──>│
    │              │                     │                    │
    │              │                     │<──employees───────<│
    │              │                     │                    │
    │              │                     │──validate(emp)     │
    │              │                     │──addToCollection() │
    │              │                     │                    │
    │              │                     │──writeEmployees()─>│
    │              │                     │                    │
    │<──success────│                     │                    │
```

**Similar workflows for**:
- Update employee
- Delete employee

**Key Steps**:
1. Admin initiates employee operation (add/update/delete)
2. EmployeeManagement reads employee database
3. Operation is performed (validate, modify collection)
4. Employee database is rewritten
5. Success/failure returned to UI

---

## Sequence Diagram Conventions

### Actor Notation
- **Cashier/Admin**: Human users
- **POSSystem**: Main application class
- **POS/POR/POH**: Transaction classes
- **Inventory**: Singleton inventory manager
- **Management**: Customer/rental manager
- **EmployeeManagement**: Employee CRUD operations
- **File System**: File I/O operations

### Message Types
- **Synchronous calls**: Solid arrows (→)
- **Returns**: Dashed arrows (←)
- **File operations**: Dotted arrows

### Notes
- All file operations are synchronous (blocking)
- No transaction management (file operations not atomic)
- Temp file operations are not thread-safe

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Complete Sequence Diagrams

