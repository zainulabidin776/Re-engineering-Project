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

**Document Version**: 1.0  
**Last Updated**: 2025-11-28

