# Class Diagram - Legacy POS System
## Complete Class Relationships

---

## Class Diagram Overview

This document provides the complete class diagram for the legacy POS system, showing all classes, relationships, and key methods.

---

## Simplified Class Diagram

```
┌────────────────────────────────────────────────────────────┐
│                    Legacy POS System Classes                │
└────────────────────────────────────────────────────────────┘

┌─────────────────┐
│   POSSystem     │
│─────────────────│
│ + main()        │
│ + logIn()       │
│ + logOut()      │
└────────┬────────┘
         │
         ├─────────────────┐
         │                 │
┌────────▼────────┐  ┌─────▼──────────┐
│ EmployeeMgmt    │  │  PointOfSale    │
│─────────────────│  │  (abstract)     │
│ + addEmployee() │  │─────────────────│
│ + updateEmp()   │  │ + startNew()    │
│ + deleteEmp()   │  │ + enterItem()   │
└────────┬────────┘  │ + updateTotal() │
         │           │ + coupon()      │
         │           │ # endPOS()      │
┌────────▼────────┐  │ # deleteTempItem()│
│   Employee      │  │ # retrieveTemp()│
│─────────────────│  └────────┬────────┘
│ - username      │           │
│ - position      │           │
│ - firstName     │           │
│ - lastName      │           │
│ - password      │           │
└─────────────────┘           │
                ┌──────────────┼──────────────┐
                │              │              │
        ┌───────▼──────┐ ┌────▼──────┐ ┌────▼──────┐
        │     POS      │ │    POR    │ │    POH    │
        │  (Sale)      │ │ (Rental)  │ │ (Return)  │
        │──────────────│ │───────────│ │───────────│
        │ + endPOS()   │ │ + endPOS()│ │ + endPOS()│
        └──────┬───────┘ └────┬──────┘ └────┬──────┘
               │              │              │
               └──────────────┼──────────────┘
                              │
                    ┌─────────▼─────────┐
                    │    Inventory       │
                    │  (Singleton)       │
                    │────────────────────│
                    │ + getInstance()    │
                    │ + accessInventory()│
                    │ + updateInventory()│
                    └─────────┬──────────┘
                              │
                    ┌─────────▼─────────┐
                    │       Item         │
                    │────────────────────│
                    │ - itemID          │
                    │ - itemName        │
                    │ - price           │
                    │ - amount          │
                    └────────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │    Management      │
                    │────────────────────│
                    │ + checkUser()     │
                    │ + getLatestReturnDate()│
                    └─────────┬──────────┘
                              │
                    ┌─────────▼─────────┐
                    │    ReturnItem      │
                    │────────────────────│
                    │ - itemID          │
                    │ - daysSinceReturn │
                    └────────────────────┘
```

---

## Detailed Class Descriptions

### POSSystem

**Package**: `src`  
**Type**: Main Application Class  
**Purpose**: Entry point, authentication, routing

```java
public class POSSystem {
    private static ArrayList<Employee> employees;
    public static String employeeDatabase = "Database/employeeDatabase.txt";
    
    // Methods
    public static void main(String[] args)
    public int logIn(String username, String password)
    public void logOut(String username)
}
```

**Relationships**:
- Uses: `Employee`, `EmployeeManagement`
- Routes to: `POS`, `POR`, `POH` (via UI)

---

### EmployeeManagement

**Package**: `src`  
**Type**: Business Logic Class  
**Purpose**: Employee CRUD operations

```java
public class EmployeeManagement {
    public static String employeeDatabase = "Database/employeeDatabase.txt";
    
    // Methods
    public void addEmployee(Employee emp)
    public void updateEmployee(String username, Employee emp)
    public void deleteEmployee(String username)
    private ArrayList<Employee> readEmployees()
    private void writeEmployees(ArrayList<Employee> employees)
}
```

**Relationships**:
- Uses: `Employee`
- Used by: `POSSystem`, UI classes

---

### Employee

**Package**: `src`  
**Type**: Domain Model  
**Purpose**: Employee entity

```java
public class Employee {
    private String username;
    private String position;
    private String firstName;
    private String lastName;
    private String password;  // Plain text!
    
    // Constructors
    public Employee(String username, String position, ...)
    
    // Getters/Setters
    public String getUsername()
    public String getPosition()
    // ...
}
```

**Relationships**:
- Used by: `POSSystem`, `EmployeeManagement`

---

### PointOfSale (Abstract)

**Package**: `src`  
**Type**: Abstract Base Class  
**Purpose**: Common transaction logic

```java
public abstract class PointOfSale {
    protected ArrayList<Item> items;
    protected double total;
    public double tax = 1.06;
    private static float discount = 0.90f;
    
    // Concrete methods (Template Method pattern)
    public boolean startNew(String file)
    public boolean enterItem(int itemID, int amount)
    public void updateTotal()
    public boolean coupon(String couponNumber)
    
    // Abstract methods
    public abstract void endPOS(String file)
    public abstract void deleteTempItem(int id)
    protected abstract ArrayList<Item> retrieveTemp()
}
```

**Relationships**:
- Extended by: `POS`, `POR`, `POH`
- Uses: `Inventory` (Singleton), `Item`

---

### POS (extends PointOfSale)

**Package**: `src`  
**Type**: Concrete Class  
**Purpose**: Sales processing

```java
public class POS extends PointOfSale {
    // Overrides
    @Override
    public void endPOS(String itemDatabaseFile)
    @Override
    public void deleteTempItem(int id)
    @Override
    protected ArrayList<Item> retrieveTemp()
}
```

**Relationships**:
- Extends: `PointOfSale`
- Uses: `Inventory`, `Item`

---

### POR (extends PointOfSale)

**Package**: `src`  
**Type**: Concrete Class  
**Purpose**: Rental processing

```java
public class POR extends PointOfSale {
    private long phoneNumber;
    
    // Constructor
    public POR(long phoneNumber)
    
    // Overrides
    @Override
    public void endPOS(String rentalDatabaseFile)
    @Override
    public void deleteTempItem(int id)
    @Override
    protected ArrayList<Item> retrieveTemp()
}
```

**Relationships**:
- Extends: `PointOfSale`
- Uses: `Inventory`, `Item`, `Management`

---

### POH (extends PointOfSale)

**Package**: `src`  
**Type**: Concrete Class  
**Purpose**: Return processing

```java
public class POH extends PointOfSale {
    private long phoneNumber;
    
    // Constructor
    public POH(long phoneNumber)
    
    // Overrides
    @Override
    public void endPOS(String rentalDatabaseFile)
    @Override
    public void deleteTempItem(int id)
    @Override
    protected ArrayList<Item> retrieveTemp()
}
```

**Relationships**:
- Extends: `PointOfSale`
- Uses: `Inventory`, `Item`, `Management`

---

### Inventory (Singleton)

**Package**: `src`  
**Type**: Business Logic Class  
**Purpose**: Inventory management

```java
public class Inventory {
    private static Inventory instance = null;
    private ArrayList<Item> items;
    
    // Singleton pattern
    public static Inventory getInstance()
    
    // Methods
    public boolean accessInventory(String file)
    public void updateInventory(String file, int itemID, int quantity)
    public Item findItem(int itemID)
}
```

**Relationships**:
- Uses: `Item`
- Used by: `POS`, `POR`, `POH`

---

### Item

**Package**: `src`  
**Type**: Domain Model  
**Purpose**: Item entity

```java
public class Item {
    private int itemID;
    private String itemName;
    private float price;
    private int amount;  // quantity in stock
    
    // Constructors
    public Item(int itemID, String itemName, float price, int amount)
    
    // Getters/Setters
    public int getItemID()
    public String getItemName()
    public float getPrice()
    public int getAmount()
    // ...
}
```

**Relationships**:
- Used by: `Inventory`, `POS`, `POR`, `POH`, `PointOfSale`

---

### Management

**Package**: `src`  
**Type**: Business Logic Class  
**Purpose**: Customer and rental management

```java
public class Management {
    private static String userDatabase = "Database/userDatabase.txt";
    
    // Methods
    public void checkUser(long phoneNumber)
    public String getLatestReturnDate(long phone)
    // ... (many helper methods)
}
```

**Relationships**:
- Uses: `ReturnItem`
- Used by: `POR`, `POH`

---

### ReturnItem

**Package**: `src`  
**Type**: Domain Model  
**Purpose**: Return item entity

```java
public class ReturnItem {
    private int itemID;
    private int daysSinceReturn;
    
    // Constructors, getters, setters
}
```

**Relationships**:
- Used by: `Management`

---

## UI Classes (Swing Components)

**Note**: UI classes have minimal documented relationships but interact with business logic classes.

- `Login_Interface.java` - Uses `POSSystem.logIn()`
- `Cashier_Interface.java` - Uses `POS`, `POR`, `POH`
- `Admin_Interface.java` - Uses `EmployeeManagement`
- `Transaction_Interface.java` - Uses transaction classes
- `Payment_Interface.java` - Payment UI
- `EnterItem_Interface.java` - Item entry UI
- `AddEmployee_Interface.java` - Uses `EmployeeManagement`
- `UpdateEmployee_Interface.java` - Uses `EmployeeManagement`

---

## Relationship Types

**Inheritance** (`extends`):
- `POS extends PointOfSale`
- `POR extends PointOfSale`
- `POH extends PointOfSale`

**Composition** (`has-a`):
- `POSSystem` contains `ArrayList<Employee>`
- `PointOfSale` contains `ArrayList<Item>`
- `Inventory` contains `ArrayList<Item>`

**Dependency** (`uses`):
- `POSSystem` uses `EmployeeManagement`
- `POS/POR/POH` use `Inventory`
- `POR/POH` use `Management`
- `Inventory` uses `Item`

**Singleton Access**:
- `POS/POR/POH` access `Inventory.getInstance()`

---

## Class Statistics

| Category | Count | Classes |
|----------|-------|---------|
| **Domain Models** | 3 | Employee, Item, ReturnItem |
| **Business Logic** | 5 | POSSystem, PointOfSale, POS, POR, POH, Inventory, Management, EmployeeManagement |
| **UI Components** | 8 | Various *_Interface classes |
| **Total** | ~20 | |

---

**Document Version**: 1.0  
**Date**: 2025-11-28  
**Status**: Complete Class Diagram Documentation

