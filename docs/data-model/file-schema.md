# File Schema Documentation
## Legacy POS System Data Storage

This document provides complete documentation of all text file formats used for data storage in the legacy POS system.

---

## File Storage Overview

**Location**: `Database/` directory  
**Format**: Plain text files  
**Encoding**: System default (typically UTF-8 or ASCII)  
**Structure**: Space or comma-delimited records  
**No Schema Validation**: Files can contain inconsistent data

---

## File Inventory

| File Name | Purpose | Format | Status |
|-----------|---------|--------|--------|
| `employeeDatabase.txt` | Employee records | Space-delimited | Active |
| `itemDatabase.txt` | Inventory items | Space-delimited | Active |
| `userDatabase.txt` | Customer rentals | Space + comma-delimited | Active |
| `saleInvoiceRecord.txt` | Sales transactions | Mixed format | Active |
| `returnSale.txt` | Return transactions | Mixed format | Active |
| `couponNumber.txt` | Valid coupons | One per line | Active |
| `employeeLogfile.txt` | Login audit trail | Timestamp + action | Active |
| `rentalDatabase.txt` | Rental inventory | Space-delimited | Duplicate? |
| `temp.txt` | Transaction state | Mixed format | Temporary |

---

## Detailed File Schemas

### 1. employeeDatabase.txt

**Purpose**: Store employee account information  
**Format**: Space-delimited, one record per line  
**Location**: `Database/employeeDatabase.txt`

**Schema**:
```
username position firstName lastName password
```

**Fields**:
| Field | Type | Constraints | Example |
|-------|------|-------------|---------|
| `username` | String | Unique identifier | `"110001"` |
| `position` | String | Must be "Admin" or "Cashier" | `"Admin"` |
| `firstName` | String | No spaces | `"Harry"` |
| `lastName` | String | No spaces | `"Larry"` |
| `password` | String | Plain text (security issue!) | `"1"` |

**Example Records**:
```
110001 Admin Harry Larry 1
110002 Cashier John Doe lehigh2016
110003 Cashier Jane Smith password123
```

**File Access**:
- **Read**: On application startup (load employees)
- **Write**: When adding/updating/deleting employees
- **Concurrency**: No locking (single user assumed)

**Issues**:
- ⚠️ Plain text passwords (major security vulnerability)
- ⚠️ No validation of position field
- ⚠️ No unique constraint enforcement (can have duplicates)

---

### 2. itemDatabase.txt

**Purpose**: Store inventory items  
**Format**: Space-delimited, one record per line  
**Location**: `Database/itemDatabase.txt`

**Schema**:
```
itemID itemName price amount
```

**Fields**:
| Field | Type | Constraints | Example |
|-------|------|-------------|---------|
| `itemID` | Integer | Unique identifier | `1000` |
| `itemName` | String | Typically no spaces | `"Potato"` |
| `price` | Float | Should be >= 0 | `1.0` |
| `amount` | Integer | Quantity in stock, can go negative! | `249` |

**Example Records**:
```
1000 Potato 1.0 249
1001 Tomato 2.5 150
1002 Onion 1.5 200
1022 Carrot 1.2 100
1023 Broccoli 3.0 75
```

**File Access**:
- **Read**: On inventory access operations
- **Write**: After sales, rentals, returns (quantity updates)
- **Concurrency**: No locking (data corruption risk)

**Issues**:
- ⚠️ Quantity can go negative (no validation)
- ⚠️ Price stored as float (precision issues for currency)
- ⚠️ No unique constraint on itemID
- ⚠️ Entire file read/written on each update

---

### 3. userDatabase.txt

**Purpose**: Store customer information and rental history  
**Format**: Space-delimited with comma-delimited sub-fields  
**Location**: `Database/userDatabase.txt`

**Schema**:
```
phoneNumber itemID1,date1,returned1 itemID2,date2,returned2 ...
```

**Fields**:
| Field | Type | Constraints | Example |
|-------|------|-------------|---------|
| `phoneNumber` | Long | Unique customer identifier | `1234567890` |
| `itemID` | Integer | References itemDatabase | `1022` |
| `date` | String | Format "MM/dd/yy" | `"6/31/11"` |
| `returned` | Boolean | "true" or "false" | `"false"` |

**Example Records**:
```
1234567890 1022,6/31/11,false 1023,7/15/11,true
9876543210 1000,7/1/11,false
5555555555 1001,6/20/11,true 1002,6/25/11,false
```

**File Access**:
- **Read**: Customer lookup, rental history queries
- **Write**: Create customer, add rental, mark as returned
- **Concurrency**: No locking

**Issues**:
- ⚠️ **Denormalized**: All rentals for a customer in one line
- ⚠️ **No normalization**: Difficult to query individual rentals
- ⚠️ **Inconsistent date format**: "MM/dd/yy" ambiguous
- ⚠️ **No foreign key validation**: itemID might not exist
- ⚠️ **Line length issues**: Long lines for customers with many rentals

**Parsing Logic**:
- Split by space to get phone number and rental items
- Split rental items by comma to get itemID, date, returned
- First element is phone number
- Remaining elements are rental items

---

### 4. saleInvoiceRecord.txt

**Purpose**: Log sales transactions  
**Format**: Mixed format (timestamp + transaction details)  
**Location**: `Database/saleInvoiceRecord.txt`

**Schema** (approximate):
```
timestamp | sale_details
```

**Fields** (inferred):
| Field | Type | Format | Example |
|-------|------|--------|---------|
| `timestamp` | String | "yyyy-MM-dd HH:mm:ss.SSS" | `"2025-11-28 14:30:15.123"` |
| `sale_details` | String | Mixed format | Varies |

**Example Records**:
```
2025-11-28 14:30:15.123 | Sale: ItemID=1000, Quantity=2, Total=2.12
2025-11-28 15:45:30.456 | Sale: ItemID=1001, Quantity=1, Coupon=COUPON10, Total=2.38
```

**File Access**:
- **Read**: Rarely (for reporting)
- **Write**: Append after each sale (append-only)
- **Concurrency**: Append-only reduces corruption risk

**Issues**:
- ⚠️ **Inconsistent format**: Format may vary
- ⚠️ **No structured data**: Difficult to parse programmatically
- ⚠️ **No transaction ID**: Cannot track individual sales
- ⚠️ **No foreign keys**: Cannot link to employees or items

---

### 5. returnSale.txt

**Purpose**: Log return transactions  
**Format**: Mixed format (timestamp + return details)  
**Location**: `Database/returnSale.txt`

**Schema** (approximate):
```
timestamp | return_details
```

**Similar Issues as saleInvoiceRecord.txt**

**Example Records**:
```
2025-11-28 16:00:00.789 | Return: Phone=1234567890, ItemID=1022, DaysOverdue=5
```

---

### 6. couponNumber.txt

**Purpose**: Store valid coupon codes  
**Format**: One coupon code per line  
**Location**: `Database/couponNumber.txt`

**Schema**:
```
couponCode
```

**Fields**:
| Field | Type | Constraints | Example |
|-------|------|-------------|---------|
| `couponCode` | String | One per line | `"COUPON10"` |

**Example Records**:
```
COUPON10
SAVE20
DISCOUNT15
```

**File Access**:
- **Read**: On coupon validation
- **Write**: When adding/removing coupons
- **Concurrency**: No locking

**Issues**:
- ⚠️ **No expiry dates**: Coupons never expire
- ⚠️ **No discount amount**: Hard-coded 10% discount
- ⚠️ **No usage tracking**: Cannot track coupon usage

---

### 7. employeeLogfile.txt

**Purpose**: Audit trail for employee login/logout  
**Format**: Timestamp + action + username  
**Location**: `Database/employeeLogfile.txt`

**Schema** (inferred):
```
timestamp | action | username
```

**Example Records**:
```
2025-11-28 09:00:00.000 | LOGIN | 110002
2025-11-28 17:30:00.000 | LOGOUT | 110002
```

**File Access**:
- **Read**: Rarely (for audit review)
- **Write**: Append on login/logout (append-only)
- **Concurrency**: Append-only

**Issues**:
- ⚠️ **No structured format**: Parsing may be difficult
- ⚠️ **No integrity checks**: Can be tampered with

---

### 8. rentalDatabase.txt

**Purpose**: Rental inventory (appears to duplicate itemDatabase.txt)  
**Format**: Space-delimited  
**Location**: `Database/rentalDatabase.txt`

**Note**: This file appears to be a duplicate or alternative view of `itemDatabase.txt`. Format likely identical.

**Issues**:
- ⚠️ **Data duplication**: Same data in two files
- ⚠️ **Consistency risk**: Files may get out of sync

---

### 9. temp.txt

**Purpose**: Temporary transaction state  
**Format**: Mixed format  
**Location**: `Database/temp.txt`

**Schema**: Varies by transaction type

**Usage**:
- Stores current transaction cart items
- Created at transaction start
- Deleted at transaction completion
- Used for transaction recovery (if needed)

**File Access**:
- **Read**: Retrieve current transaction state
- **Write**: Update cart items
- **Delete**: After transaction completion or cancellation

**Issues**:
- ⚠️ **Not thread-safe**: Multiple transactions could conflict
- ⚠️ **Security risk**: Contains sensitive transaction data
- ⚠️ **No cleanup guarantee**: File may remain if application crashes

---

## Data Integrity Issues

### 1. No Referential Integrity

- `userDatabase.txt` contains `itemID` but no validation that item exists in `itemDatabase.txt`
- `saleInvoiceRecord.txt` references items but no foreign key constraints

### 2. No Data Validation

- Quantities can go negative
- Prices can be negative
- Date formats inconsistent
- No email validation
- No phone number format validation

### 3. Data Duplication

- `rentalDatabase.txt` duplicates `itemDatabase.txt`
- Customer phone numbers may appear in multiple contexts

### 4. No Transaction Support

- Partial writes possible (file corruption risk)
- No rollback mechanism
- No atomicity guarantees

### 5. No Concurrency Control

- File locking not implemented
- Multiple users could corrupt data
- Race conditions possible

---

## Migration Considerations

When migrating to database:

1. **Normalize userDatabase.txt** → Separate into:
   - `customers` table (phone, name, etc.)
   - `rentals` table (customer_id, rental_date, due_date)
   - `rental_items` table (rental_id, item_id, quantity, returned)

2. **Add Constraints**:
   - Foreign keys (itemID references items)
   - CHECK constraints (quantity >= 0, price >= 0)
   - UNIQUE constraints (username, itemID)

3. **Password Migration**:
   - Hash plain text passwords (BCrypt)
   - Store password_hash instead of password

4. **Date Standardization**:
   - Parse all dates to ISO format
   - Use DATE/TIMESTAMP types

5. **Transaction Logging**:
   - Create structured `sales` and `sale_items` tables
   - Add transaction IDs
   - Link to employees and items

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Complete File Schema Documentation

