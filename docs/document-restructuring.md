# Document Restructuring - Legacy POS System

## Overview

This document captures the document restructuring phase of the reengineering process, where we reorganize and recreate technical documentation to accurately represent the true system structure.

## Legacy Documentation Inventory

### Existing Documentation (from Documentation/ folder)

**Inception Phase:**
- Business Rules.docx
- Glossary.docx
- Vision.docx
- Use Cases Draft.docx
- Supplementary Specification.docx
- WBS and Responsibility Matrices

**Elaboration Phase:**
- SAD.docx, SAD(2).docx, SAD(3).docx (Software Architecture Documents)
- Process diagrams (Activity, Class, Domain, Sequence) for:
  - Sales
  - Rentals
  - Returns
  - System Startup/Shutdown
- Package Diagram.png
- Responsibility Matrix.xlsx

**Construction Phase:**
- Beta Release documentation
- Developer Manual.docx
- User Manual.docx
- White box test documentation
- Bug reports

**Beta Release:**
- Changes from alpha to beta
- Updated Developer/User manuals
- Responsibility matrices

### Documentation Gaps Identified

1. **No Current Architecture Diagrams**: Existing diagrams may not match actual codebase
2. **Outdated Class Diagrams**: Code has evolved since original design
3. **Missing Data Model Documentation**: No ERD for file-based storage
4. **No API Documentation**: N/A for desktop app, but needed for future web version
5. **Incomplete Test Documentation**: Minimal test coverage documented
6. **No Deployment Guide**: Assumes NetBeans IDE usage
7. **Missing Configuration Documentation**: File paths, OS detection logic not documented

## Restructured Documentation Structure

### 1. System Overview Document

**Location**: `docs/system-overview.md` (to be created)

**Contents**:
- System purpose and scope
- High-level architecture
- Key features (Sales, Rentals, Returns, Employee Management)
- Technology stack (Java 8, Swing UI, File-based storage)
- System boundaries

### 2. Architecture Documentation

**Location**: `docs/architecture/` (to be created)

**Contents**:
- **legacy-architecture.md**: Current system architecture (reverse-engineered)
- **class-diagram.md**: Updated class diagram with all relationships
- **sequence-diagrams.md**: Key workflows (login, sale, rental, return)
- **component-diagram.md**: High-level component interactions
- **deployment-diagram.md**: Current deployment model (desktop app)

### 3. Data Model Documentation

**Location**: `docs/data-model/` (to be created)

**Contents**:
- **file-schema.md**: Documentation of all .txt file formats
- **data-dictionary.md**: Field definitions and constraints
- **data-flow.md**: How data moves through the system
- **migration-plan.md**: Strategy for migrating to database

### 4. API/Interface Documentation

**Location**: `docs/api/` (to be created)

**Contents**:
- **public-methods.md**: Documented public APIs of core classes
- **ui-interfaces.md**: UI component interfaces (for future web migration)

### 5. Testing Documentation

**Location**: `docs/testing/` (to be created)

**Contents**:
- **test-strategy.md**: Testing approach for reengineering
- **test-coverage.md**: Current vs. target coverage
- **test-cases.md**: Key test scenarios

### 6. Development Documentation

**Location**: `docs/development/` (to be created)

**Contents**:
- **build-instructions.md**: How to compile and run
- **development-setup.md**: IDE setup, dependencies
- **coding-standards.md**: Code style guidelines (to be established)
- **refactoring-log.md**: Record of refactorings performed

## Reverse-Engineered Diagrams

### Class Diagram (Simplified)

```
┌─────────────────┐
│   POSSystem     │
│  - employees    │
│  - logIn()      │
│  - logOut()     │
└────────┬────────┘
         │
         ├─────────────────┐
         │                 │
┌────────▼────────┐  ┌─────▼──────────┐
│ EmployeeMgmt    │  │  PointOfSale    │
│  - addEmployee()│  │  (abstract)     │
│  - updateEmp()  │  │  - enterItem()  │
└─────────────────┘  │  - updateTotal()│
                     │  - coupon()     │
                     └────────┬────────┘
                               │
                ┌──────────────┼──────────────┐
                │              │              │
        ┌───────▼──────┐ ┌────▼──────┐ ┌────▼──────┐
        │     POS      │ │    POR    │ │    POH    │
        │  (Sale)      │ │ (Rental)  │ │ (Return)  │
        │  - endPOS()  │ │ - endPOS()│ │ - endPOS()│
        └──────┬───────┘ └────┬──────┘ └────┬──────┘
               │              │              │
               └──────────────┼──────────────┘
                              │
                    ┌─────────▼─────────┐
                    │    Inventory       │
                    │  (Singleton)       │
                    │  - accessInventory()│
                    │  - updateInventory()│
                    └────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │       Item         │
                    │  - itemID          │
                    │  - itemName        │
                    │  - price           │
                    │  - amount          │
                    └────────────────────┘
```

### Sequence Diagram: Sale Transaction

```
Cashier          POSSystem        POS          Inventory      File System
  │                 │              │               │               │
  │──login()───────>│              │               │               │
  │<───1 (cashier)──│              │               │               │
  │                 │              │               │               │
  │──startSale()───>│              │               │               │
  │                 │──new POS()──>│               │               │
  │                 │              │──startNew()──>│               │
  │                 │              │               │──readFile()─>│
  │                 │              │<──true────────│<───data──────│
  │                 │<──POS───────│               │               │
  │<──POS──────────│              │               │               │
  │                 │              │               │               │
  │──enterItem()───>│              │               │               │
  │                 │              │──enterItem()──>│               │
  │                 │              │<──true────────│               │
  │<──true──────────│              │               │               │
  │                 │              │               │               │
  │──endPOS()──────>│              │               │               │
  │                 │              │──endPOS()────>│               │
  │                 │              │               │──update()───>│
  │                 │              │               │<──updated────│
  │                 │              │──writeInvoice()───────────────>│
  │<──total──────────│              │               │               │
```

## Data Dictionary

### Employee Database Format
**File**: `Database/employeeDatabase.txt`

**Format**: `username position firstName lastName password`

**Fields**:
- `username`: String, unique identifier (e.g., "110001")
- `position`: String, either "Admin" or "Cashier"
- `firstName`: String, employee first name
- `lastName`: String, employee last name  
- `password`: String, plain text password (security issue)

**Example**: `110001 Admin Harry Larry 1`

### Item Database Format
**File**: `Database/itemDatabase.txt`

**Format**: `itemID itemName price amount`

**Fields**:
- `itemID`: Integer, unique identifier (e.g., 1000)
- `itemName`: String, product name (no spaces)
- `price`: Float, unit price
- `amount`: Integer, quantity in stock

**Example**: `1000 Potato 1.0 249`

### User/Rental Database Format
**File**: `Database/userDatabase.txt`

**Format**: `phoneNumber itemID1,date1,returned1 itemID2,date2,returned2 ...`

**Fields**:
- `phoneNumber`: Long, customer phone number (unique identifier)
- `itemID`: Integer, rented item identifier
- `date`: String, format "MM/dd/yy", return due date
- `returned`: Boolean, "true" or "false"

**Example**: `1234567890 1022,6/31/11,false 1023,7/15/11,true`

## Operational Scenarios

### Scenario 1: Cashier Login and Process Sale

1. **Precondition**: Employee database exists with valid cashier credentials
2. **Steps**:
   - Cashier launches application
   - Enters username: "110002", password: "lehigh2016"
   - System validates credentials → returns status 1 (cashier)
   - Cashier selects "Sale"
   - System creates POS instance, loads item database
   - Cashier enters itemID and quantity
   - System adds to cart, updates running total
   - Cashier completes transaction
   - System applies 6% tax, updates inventory, logs invoice
3. **Postcondition**: Inventory reduced, invoice logged, transaction complete

### Scenario 2: Process Rental

1. **Precondition**: Item database and user database exist
2. **Steps**:
   - Cashier logs in, selects "Rental"
   - Enters customer phone number
   - System checks userDatabase.txt
   - If not found, creates new entry
   - Cashier adds items to rental cart
   - System calculates total with tax
   - On completion, updates inventory and saves rental to userDatabase
3. **Postcondition**: Items marked as rented, inventory reduced, rental record created

### Scenario 3: Process Return

1. **Precondition**: Customer has outstanding rentals in userDatabase
2. **Steps**:
   - Cashier logs in, selects "Return"
   - Enters customer phone number
   - System queries userDatabase for outstanding items
   - Displays items with days overdue
   - Cashier selects items to return
   - System restores inventory, marks items as returned
3. **Postcondition**: Inventory restored, rental marked complete

## Known Issues Documented

1. **Security**: Plain text passwords in employeeDatabase.txt
2. **Data Integrity**: No transaction support, partial writes possible
3. **Concurrency**: No locking mechanism for file access
4. **Error Handling**: Exceptions often swallowed, inconsistent error messages
5. **Performance**: Entire files loaded into memory on every operation
6. **Maintainability**: Hardcoded file paths, OS detection scattered
7. **Testability**: Tight coupling makes unit testing difficult

## Documentation Standards Established

- **Markdown format** for all new documentation (portable, version-controllable)
- **Version control** for all documentation changes
- **Diagrams** in text format (Mermaid/PlantUML) or PNG with source files
- **Code examples** with syntax highlighting
- **Cross-references** between related documents

## Next Steps

1. Generate actual class diagrams from code (using tools like PlantUML)
2. Create sequence diagrams for all major workflows
3. Document all file formats in detail
4. Create deployment guide for current system
5. Establish coding standards document
6. Begin refactoring log

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: In Progress

