# Refactoring Log

This document tracks all refactorings performed during the code restructuring phase. Each refactoring includes before/after code, rationale, and quality impact.

## Refactoring #1: Extract Constants Class

**Date**: 2025-11-28  
**Type**: Extract Constant  
**Files Changed**: `src/Constants.java` (new), `src/PointOfSale.java`, `src/POSSystem.java`, `src/POS.java`

### Before
```java
// Magic numbers and strings scattered throughout codebase
public double tax=1.06;
private static float discount = 0.90f;
public static String couponNumber = "Database/couponNumber.txt";
if (length != 16) // credit card validation
```

### After
```java
// Constants.java
public static final double DEFAULT_TAX_RATE = 1.06;
public static final float COUPON_DISCOUNT = 0.90f;
public static final String COUPON_FILE = DATABASE_DIR + "couponNumber.txt";
public static final int CREDIT_CARD_LENGTH = 16;

// Usage
public double tax=Constants.DEFAULT_TAX_RATE;
if (length != Constants.CREDIT_CARD_LENGTH)
```

### Rationale
- Eliminates magic numbers/strings (code smell: Magic Number)
- Centralizes configuration values
- Makes future changes easier (single source of truth)
- Improves code readability

### Quality Impact
- **Maintainability**: ⬆️ High improvement - configuration changes in one place
- **Readability**: ⬆️ Medium improvement - constants are self-documenting
- **Testability**: ➡️ No change
- **Risk**: ✅ Low - constants are compile-time safe

---

## Refactoring #2: Extract System Utilities

**Date**: 2025-11-28  
**Type**: Extract Method → Utility Class  
**Files Changed**: `src/SystemUtils.java` (new), `src/PointOfSale.java`, `src/POSSystem.java`, `src/POS.java`

### Before
```java
// Duplicate OS detection code in 10+ classes
if (System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
    // ...
}

// Duplicate line separator code
bw.write(System.getProperty("line.separator"));
```

### After
```java
// SystemUtils.java
public static boolean isWindows() {
    String osName = System.getProperty("os.name");
    return osName != null && (osName.startsWith("W") || osName.startsWith("w"));
}

public static String getLineSeparator() {
    return System.getProperty("line.separator");
}

// Usage
if (SystemUtils.isWindows()) { ... }
bw.write(SystemUtils.getLineSeparator());
```

### Rationale
- Eliminates duplicate code (code smell: Duplicate Code)
- Consolidates OS detection logic
- Makes testing easier (can mock SystemUtils)
- Single responsibility for system operations

### Quality Impact
- **Maintainability**: ⬆️ High improvement - OS logic in one place
- **DRY Principle**: ⬆️ High improvement - removed 10+ duplicate blocks
- **Testability**: ⬆️ Medium improvement - can unit test SystemUtils independently
- **Risk**: ✅ Low - pure utility methods, no side effects

---

## Refactoring #3: Replace Magic Numbers with Constants in PointOfSale

**Date**: 2025-11-28  
**Type**: Replace Magic Number  
**Files Changed**: `src/PointOfSale.java`

### Before
```java
private static float discount = 0.90f;
public double tax=1.06;
public static String couponNumber = "Database/couponNumber.txt";
public static String tempFile="Database/temp.txt";
```

### After
```java
private static float discount = Constants.COUPON_DISCOUNT;
public double tax=Constants.DEFAULT_TAX_RATE;
public static String couponNumber = Constants.COUPON_FILE;
public static String tempFile = Constants.TEMP_FILE;
```

### Rationale
- Part of Refactoring #1, applied to PointOfSale class
- Makes tax rates and file paths configurable
- Improves consistency across codebase

### Quality Impact
- **Maintainability**: ⬆️ Medium improvement
- **Consistency**: ⬆️ High improvement
- **Risk**: ✅ Very Low

---

## Refactoring #4: Replace Magic Numbers with Constants in POSSystem

**Date**: 2025-11-28  
**Type**: Replace Magic Number  
**Files Changed**: `src/POSSystem.java`

### Before
```java
public static String employeeDatabase = "Database/employeeDatabase.txt";
DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
if(((employees.get(index)).getPosition()).equals("Cashier")){
    return 1;
}
```

### After
```java
public static String employeeDatabase = Constants.EMPLOYEE_DATABASE;
DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG);
if(((employees.get(index)).getPosition()).equals(Constants.POSITION_CASHIER)){
    return Constants.LOGIN_CASHIER;
}
```

### Rationale
- Centralizes file paths and date formats
- Replaces magic return codes with named constants
- Improves code clarity

### Quality Impact
- **Readability**: ⬆️ High improvement - return codes are self-documenting
- **Maintainability**: ⬆️ Medium improvement
- **Risk**: ✅ Very Low

---

## Refactoring #5: Replace System Properties with SystemUtils in POS

**Date**: 2025-11-28  
**Type**: Replace Method Call  
**Files Changed**: `src/POS.java`

### Before
```java
if(System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
    // ...
}
writer.write(System.getProperty("line.separator"));
DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
String t = "Database/saleInvoiceRecord.txt";
```

### After
```java
if(SystemUtils.isWindows()){
    // ...
}
writer.write(SystemUtils.getLineSeparator());
DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG);
String t = Constants.SALE_INVOICE_RECORD;
```

### Rationale
- Applies SystemUtils and Constants refactorings to POS class
- Consistent with other refactorings
- Reduces duplication

### Quality Impact
- **Consistency**: ⬆️ High improvement
- **Maintainability**: ⬆️ Medium improvement
- **Risk**: ✅ Very Low

---

## Summary of Refactorings

| Refactoring | Type | Files Changed | Risk | Impact |
|------------|------|---------------|------|--------|
| #1: Extract Constants | Extract Constant | 4 files | Low | High |
| #2: Extract SystemUtils | Extract Method | 4 files | Low | High |
| #3: PointOfSale Constants | Replace Magic Number | 1 file | Very Low | Medium |
| #4: POSSystem Constants | Replace Magic Number | 1 file | Very Low | High |
| #5: POS SystemUtils | Replace Method Call | 1 file | Very Low | Medium |

### Overall Impact
- **Code Smells Eliminated**: Magic Numbers, Duplicate Code, Data Clumps (partial)
- **Lines of Code Reduced**: ~50 lines of duplicate code eliminated
- **Maintainability**: Significantly improved through centralization
- **Test Coverage**: No regression, new utility classes are testable

### Next Planned Refactorings
1. Extract duplicate `deleteTempItem()` method (POS, POR, POH)
2. Extract file I/O into repository pattern
3. Replace `float` with `BigDecimal` for currency
4. Extract date formatting into utility
5. Consolidate file path handling

---

## Refactoring #6: Apply Constants and SystemUtils to POR

**Date**: 2025-11-28  
**Type**: Replace Magic Number / Replace Method Call  
**Files Changed**: `src/POR.java`

### Before
```java
String temp = "Database/newTemp.txt";
if(System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
    // ...
}
writer.write(System.getProperty("line.separator"));
```

### After
```java
String temp = Constants.NEW_TEMP_FILE;
if(SystemUtils.isWindows()){
    // ...
}
writer.write(SystemUtils.getLineSeparator());
```

### Rationale
- Consistent with previous refactorings
- Eliminates remaining magic strings
- Uses centralized utilities

### Quality Impact
- **Consistency**: ⬆️ High improvement
- **Maintainability**: ⬆️ Medium improvement
- **Risk**: ✅ Very Low

---

## Refactoring #7: Apply Constants and SystemUtils to POH

**Date**: 2025-11-28  
**Type**: Replace Magic Number / Replace Method Call  
**Files Changed**: `src/POH.java`

### Before
```java
String temp = "Database/newTemp.txt";
String t = "Database/returnSale.txt";
writer.write(System.getProperty("line.separator"));
```

### After
```java
String temp = Constants.NEW_TEMP_FILE;
String t = Constants.RETURN_SALE_RECORD;
writer.write(SystemUtils.getLineSeparator());
```

### Rationale
- Same as Refactoring #6, applied to POH class
- Centralizes file paths and system utilities

### Quality Impact
- **Consistency**: ⬆️ High improvement
- **Maintainability**: ⬆️ Medium improvement
- **Risk**: ✅ Very Low

---

## Refactoring #8: Apply Constants and SystemUtils to Management

**Date**: 2025-11-28  
**Type**: Replace Magic Number / Replace Method Call  
**Files Changed**: `src/Management.java`

### Before
```java
private static String userDatabase = "Database/userDatabase.txt";
if (System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
    // ...
}
SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
```

### After
```java
private static String userDatabase = Constants.USER_DATABASE;
if (SystemUtils.isWindows()){
    // ...
}
SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT);
```

### Rationale
- Centralizes user database path
- Replaces OS detection with utility
- Standardizes date format constant (3 instances replaced)

### Quality Impact
- **Consistency**: ⬆️ High improvement
- **Maintainability**: ⬆️ Medium improvement
- **DRY Principle**: ⬆️ Medium improvement (date format used 3 times)
- **Risk**: ✅ Very Low

---

## Refactoring #9: Apply Constants to EmployeeManagement

**Date**: 2025-11-28  
**Type**: Replace Magic Number  
**Files Changed**: `src/EmployeeManagement.java`

### Before
```java
String temp = "Database/newEmployeeDatabase.txt";
public static String employeeDatabase = "Database/employeeDatabase.txt";
if (System.getProperty("os.name").startsWith("W")||System.getProperty("os.name").startsWith("w")){
```

### After
```java
String temp = Constants.DATABASE_DIR + "newEmployeeDatabase.txt";
public static String employeeDatabase = Constants.EMPLOYEE_DATABASE;
if (SystemUtils.isWindows()){
```

### Rationale
- Consistent with other refactorings
- Centralizes employee database path
- Uses SystemUtils for OS detection

### Quality Impact
- **Consistency**: ⬆️ High improvement
- **Maintainability**: ⬆️ Medium improvement
- **Risk**: ✅ Very Low

---

## Summary of Refactorings (Updated)

| Refactoring | Type | Files Changed | Risk | Impact |
|------------|------|---------------|------|--------|
| #1: Extract Constants | Extract Constant | 4 files | Low | High |
| #2: Extract SystemUtils | Extract Method | 4 files | Low | High |
| #3: PointOfSale Constants | Replace Magic Number | 1 file | Very Low | Medium |
| #4: POSSystem Constants | Replace Magic Number | 1 file | Very Low | High |
| #5: POS SystemUtils | Replace Method Call | 1 file | Very Low | Medium |
| #6: POR Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #7: POH Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #8: Management Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #9: EmployeeManagement Constants | Replace Magic Number | 1 file | Very Low | Medium |

### Overall Impact (Updated)
- **Code Smells Eliminated**: Magic Numbers, Duplicate Code, Data Clumps (partial)
- **Lines of Code Reduced**: ~80 lines of duplicate code eliminated
- **Files Refactored**: 9 files now use Constants and SystemUtils
- **Maintainability**: Significantly improved through centralization
- **Test Coverage**: No regression, all tests passing

---

## Refactoring #10: Extract Duplicate deleteTempItem Method

**Date**: 2025-11-28  
**Type**: Extract Method → Pull Up Method  
**Files Changed**: `src/PointOfSale.java`, `src/POS.java`, `src/POR.java`, `src/POH.java`

### Before
```java
// POS.java - 30+ lines
public void deleteTempItem(int id){
    try{
      String temp = Constants.NEW_TEMP_FILE;
      // ... 25+ lines of file I/O code ...
    }
    catch(FileNotFoundException ex) { ... }
    catch(IOException ex) { ... }
}

// POR.java - 35+ lines (nearly identical)
public void deleteTempItem(int id){
    try{
      String temp = Constants.NEW_TEMP_FILE;
      // ... includes phone number handling ...
    }
    catch(FileNotFoundException ex) { ... }
    catch(IOException ex) { ... }
}

// POH.java - 35+ lines (nearly identical to POR)
public void deleteTempItem(int id){
    // Same as POR
}
```

### After
```java
// PointOfSale.java - Base class helper method
protected void deleteTempItemHelper(int id, boolean hasPhoneNumber) {
    try{
      String temp = Constants.NEW_TEMP_FILE;
      // ... consolidated file I/O logic ...
      if (hasPhoneNumber) {
        String phone = reader.readLine();
        writer.write(phone);
        writer.write(SystemUtils.getLineSeparator());
      }
      // ... write remaining items ...
    }
    catch(FileNotFoundException ex) { ... }
    catch(IOException ex) { ... }
}

// POS.java - Now just 3 lines
public void deleteTempItem(int id){
    deleteTempItemHelper(id, false);
}

// POR.java - Now just 3 lines
public void deleteTempItem(int id){
    deleteTempItemHelper(id, true);
}

// POH.java - Now just 3 lines
public void deleteTempItem(int id){
    deleteTempItemHelper(id, true);
}
```

### Rationale
- **Eliminates duplicate code** (code smell: Duplicate Code)
- **DRY Principle**: Single implementation of temp file deletion logic
- **Maintainability**: Bug fixes and improvements in one place
- **Reduced LOC**: ~90 lines reduced to ~40 lines (helper) + 9 lines (3 calls)

### Quality Impact
- **Maintainability**: ⬆️ Very High improvement - single source of truth
- **DRY Principle**: ⬆️ Very High improvement - eliminated 3 duplicate implementations
- **Lines of Code**: ⬇️ Reduced by ~50 lines
- **Testability**: ⬆️ Medium improvement - can test helper method independently
- **Risk**: ✅ Low - behavior unchanged, just consolidated

### Notes
- Used Template Method pattern with boolean parameter to handle phone number variation
- Maintains backward compatibility (public method signatures unchanged)
- All three implementations now delegate to shared helper

---

## Summary of Refactorings (Final Update)

| Refactoring | Type | Files Changed | Risk | Impact |
|------------|------|---------------|------|--------|
| #1: Extract Constants | Extract Constant | 4 files | Low | High |
| #2: Extract SystemUtils | Extract Method | 4 files | Low | High |
| #3: PointOfSale Constants | Replace Magic Number | 1 file | Very Low | Medium |
| #4: POSSystem Constants | Replace Magic Number | 1 file | Very Low | High |
| #5: POS SystemUtils | Replace Method Call | 1 file | Very Low | Medium |
| #6: POR Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #7: POH Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #8: Management Constants/Utils | Replace Magic Number | 1 file | Very Low | Medium |
| #9: EmployeeManagement Constants | Replace Magic Number | 1 file | Very Low | Medium |
| #10: Extract deleteTempItem | Extract Method | 4 files | Low | Very High |

### Overall Impact (Final)
- **Code Smells Eliminated**: Magic Numbers, Duplicate Code, Data Clumps (partial)
- **Lines of Code Reduced**: ~130 lines of duplicate code eliminated
- **Files Refactored**: 10 files improved
- **Maintainability**: Significantly improved through centralization
- **Test Coverage**: No regression, all tests passing

### Next Planned Refactorings
1. Extract file I/O into repository pattern
2. Replace `float` with `BigDecimal` for currency
3. Extract date formatting into utility
4. Refactor long methods in Management class
5. Add more comprehensive test coverage

---

**Document Version**: 1.2  
**Last Updated**: 2025-11-28

