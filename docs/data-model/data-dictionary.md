# Data Dictionary
## Legacy POS System - Field Definitions and Constraints

This document provides a comprehensive data dictionary for all data fields used in the legacy POS system.

---

## Data Dictionary Overview

**Purpose**: Define all data elements, their types, constraints, and usage  
**Scope**: All data stored in files and used in domain models  
**Format**: Tabular format with field definitions

---

## Employee Data

### Employee Entity

**Storage**: `Database/employeeDatabase.txt`  
**Class**: `Employee.java`

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `username` | String | Variable (typically 5-10 chars) | Required, should be unique | Employee login username | `"110001"` |
| `position` | String | Enum: "Admin" or "Cashier" | Required, must be one of two values | Employee role | `"Admin"` |
| `firstName` | String | Variable | Required, no spaces | Employee first name | `"Harry"` |
| `lastName` | String | Variable | Required, no spaces | Employee last name | `"Larry"` |
| `password` | String | Variable | Required, plain text | Login password (security issue!) | `"1"` |

**Notes**:
- ⚠️ Passwords stored in plain text (major security vulnerability)
- No validation on position field in legacy system
- Username uniqueness not enforced at database level

---

## Item/Inventory Data

### Item Entity

**Storage**: `Database/itemDatabase.txt`  
**Class**: `Item.java`

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `itemID` | Integer | 32-bit | Should be unique, positive | Unique item identifier | `1000` |
| `itemName` | String | Variable (typically 1-50 chars) | Required, typically no spaces | Product name | `"Potato"` |
| `price` | Float | 32-bit floating point | Should be >= 0 | Unit price in currency | `1.0` |
| `amount` | Integer | 32-bit | Can be negative (validation issue!) | Quantity in stock | `249` |

**Notes**:
- ⚠️ Price stored as float (precision issues for currency, should use BigDecimal)
- ⚠️ Quantity can go negative (no validation constraint)
- itemID uniqueness not enforced at file level

---

## Customer Data

### Customer Entity (Implicit)

**Storage**: `Database/userDatabase.txt` (embedded in rental records)  
**Class**: Not explicitly defined (phone number used as identifier)

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `phoneNumber` | Long | 64-bit integer | Required, used as unique identifier | Customer phone number | `1234567890` |

**Notes**:
- Phone number serves as both identifier and data
- No separate customer table
- Customer data embedded in rental records
- No validation on phone number format

---

## Rental Data

### Rental Record (Denormalized)

**Storage**: `Database/userDatabase.txt` (denormalized format)  
**Format**: `phoneNumber itemID1,date1,returned1 itemID2,date2,returned2 ...`

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `phoneNumber` | Long | 64-bit integer | Required | Customer identifier | `1234567890` |
| `itemID` | Integer | 32-bit | References itemDatabase | Rented item identifier | `1022` |
| `date` | String | Format "MM/dd/yy" | Required, format "MM/dd/yy" | Return due date | `"6/31/11"` |
| `returned` | Boolean | String: "true" or "false" | Required | Whether item has been returned | `"false"` |

**Notes**:
- ⚠️ Denormalized structure (all rentals for customer in one line)
- Date format is ambiguous (could be MM/dd/yy or dd/MM/yy)
- Multiple rentals stored in same record (no normalization)
- No foreign key validation on itemID

---

## Sale Transaction Data

### Sale Record

**Storage**: `Database/saleInvoiceRecord.txt`  
**Format**: Timestamp + transaction details (mixed format)

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `timestamp` | String | Format "yyyy-MM-dd HH:mm:ss.SSS" | Required | Transaction timestamp | `"2025-11-28 14:30:15.123"` |
| `itemID` | Integer | 32-bit | References itemDatabase | Sold item identifier | `1000` |
| `quantity` | Integer | 32-bit | Required, > 0 | Quantity sold | `2` |
| `total` | Float | 32-bit | Calculated | Total amount (with tax) | `2.12` |
| `couponCode` | String | Variable, optional | Optional | Applied coupon code | `"COUPON10"` |

**Notes**:
- Format varies, not standardized
- No structured fields (difficult to parse)
- No transaction ID for tracking
- No employee ID (cannot track who made sale)

---

## Return Transaction Data

### Return Record

**Storage**: `Database/returnSale.txt`  
**Format**: Timestamp + return details (mixed format)

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `timestamp` | String | Format "yyyy-MM-dd HH:mm:ss.SSS" | Required | Return timestamp | `"2025-11-28 16:00:00.789"` |
| `phoneNumber` | Long | 64-bit | Required | Customer identifier | `1234567890` |
| `itemID` | Integer | 32-bit | Required | Returned item identifier | `1022` |
| `daysOverdue` | Integer | 32-bit | Can be negative | Days past due date | `5` |

**Notes**:
- Similar format issues as sale records
- No structured fields

---

## Coupon Data

### Coupon Entity

**Storage**: `Database/couponNumber.txt`  
**Format**: One coupon code per line

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `couponCode` | String | Variable (typically 5-20 chars) | Required, one per line | Valid coupon code | `"COUPON10"` |

**Notes**:
- No expiry dates stored
- Discount percentage hard-coded in code (10%)
- No usage tracking
- No validation on coupon format

---

## Audit Log Data

### Audit Log Record

**Storage**: `Database/employeeLogfile.txt`  
**Format**: Timestamp + action + username

| Field Name | Data Type | Size/Length | Constraints | Description | Example |
|------------|-----------|-------------|-------------|-------------|---------|
| `timestamp` | String | Format "yyyy-MM-dd HH:mm:ss.SSS" | Required | Action timestamp | `"2025-11-28 09:00:00.000"` |
| `action` | String | Enum: "LOGIN" or "LOGOUT" | Required | Action type | `"LOGIN"` |
| `username` | String | Variable | Required | Employee username | `"110002"` |

**Notes**:
- Format may vary
- Append-only (no deletions)
- No structured format

---

## Temporary Transaction Data

### Temp Transaction State

**Storage**: `Database/temp.txt`  
**Format**: Varies by transaction type

**For Sales (POS)**:
- List of items in current transaction cart

**For Rentals/Returns (POR/POH)**:
- Phone number
- List of items in current transaction cart

**Notes**:
- Temporary file (should be deleted after transaction)
- Not thread-safe
- Security risk (contains transaction data)

---

## Data Type Summary

| Data Type | Usage | Issues |
|-----------|-------|--------|
| **String** | Names, codes, dates | Date format inconsistency |
| **Integer** | IDs, quantities | No validation constraints |
| **Float** | Prices | Precision issues for currency |
| **Long** | Phone numbers | No format validation |
| **Boolean** | Returned status | Stored as string "true"/"false" |

---

## Validation Rules (Missing in Legacy System)

### Employee Data
- ❌ Username uniqueness not enforced
- ❌ Position must be "Admin" or "Cashier" (not validated)
- ❌ Password complexity not enforced
- ❌ First/last name format not validated

### Item Data
- ❌ itemID uniqueness not enforced
- ❌ Price >= 0 (not enforced)
- ❌ Quantity >= 0 (not enforced, can go negative)
- ❌ Item name not validated

### Rental Data
- ❌ Phone number format not validated
- ❌ itemID foreign key not validated
- ❌ Date format not validated
- ❌ Returned status consistency not checked

### Transaction Data
- ❌ Transaction ID not generated
- ❌ Employee ID not tracked
- ❌ Total calculation not validated
- ❌ Timestamp format not standardized

---

## Recommended Improvements for Reengineered System

1. **Use Proper Data Types**:
   - Replace `float` with `BigDecimal` for currency
   - Use `DATE`/`TIMESTAMP` types for dates
   - Use `BOOLEAN` type for boolean values

2. **Add Constraints**:
   - UNIQUE constraints on usernames, itemIDs
   - CHECK constraints (quantity >= 0, price >= 0)
   - FOREIGN KEY constraints

3. **Normalize Data**:
   - Separate customer data from rental data
   - Normalize rental records
   - Create proper transaction tables

4. **Add Validation**:
   - Phone number format validation
   - Email validation (if added)
   - Date format standardization
   - Password complexity rules

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Complete Data Dictionary

