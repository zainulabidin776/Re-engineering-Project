# Error Fixes Applied

## Backend Compilation Errors Fixed

### 1. DataMigrationUtil.java - Missing PasswordEncoder Import

**Error:**
```
cannot find symbol: class PasswordEncoder
cannot find symbol: class BCryptPasswordEncoder
```

**Fix Applied:**
- Changed to use fully qualified class names:
  ```java
  private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = 
      new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
  ```

### 2. ReturnRequest.java - Missing UUID Import

**Error:**
```
cannot find symbol: class UUID
```

**Fix Applied:**
- Added missing import:
  ```java
  import java.util.UUID;
  ```

## Frontend Test Errors Fixed

### 1. Login.test.tsx - Jest vs Vitest

**Error:**
```
ReferenceError: jest is not defined
```

**Fix Applied:**
- Replaced Jest syntax with Vitest:
  - `jest.mock()` → `vi.mock()`
  - `jest.clearAllMocks()` → `vi.clearAllMocks()`
  - `jest.Mock` → `vi.fn()`
- Added Vitest imports:
  ```typescript
  import { describe, test, expect, vi, beforeEach } from 'vitest';
  ```

### 2. Added Vitest Configuration

**Files Created:**
- `vitest.config.ts` - Vitest configuration
- `src/test/setup.ts` - Test setup file

**Configuration:**
- Environment: jsdom
- Globals enabled
- Setup file for test initialization

## Verification

To verify fixes:

**Backend:**
```bash
cd pos-backend
mvn clean compile
```

**Frontend:**
```bash
cd pos-frontend
npm install
npm test
```

All compilation errors should now be resolved.



Step 1: Set JAVA_HOME correctly

In Cursor PowerShell, run:

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"


This prepends Java’s bin folder to PATH, which Maven needs.

Step 2: Check it
echo $env:JAVA_HOME
java -version
mvn --version


echo $env:JAVA_HOME → should print the Java path.

java -version → should show Java 25.0.1.

mvn --version → should now work.

Step 3: Permanent fix for Cursor

Open Cursor → Settings → Profiles → PowerShell.

In Startup Script, add:

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;C:\apache-maven-3.9.11\bin;$env:PATH"


Save and restart Cursor.

✅ Now Maven and Java will always work in Cursor.

---

## Error: Spring Data JPA Query Method Name Issue

**Date:** 2025-11-28

**Error:**
```
No property 'equal' found for type 'Item'
Failed to create query for method public abstract java.util.List 
com.sgtech.pos.repository.ItemRepository.findByQuantityLessThanOrEqual(int)
ApplicationContext failure threshold (1) exceeded
```

**Root Cause:**
Spring Data JPA doesn't support the keyword `LessThanOrEqual` in method names. The parser was trying to find a property called "equal" instead of recognizing it as a comparison operator.

**Solution:**
Changed the repository method name from `findByQuantityLessThanOrEqual` to `findByQuantityLessThanEqual` (removed "Or" from the keyword).

**Files Changed:**
- `pos-backend/src/main/java/com/sgtech/pos/repository/ItemRepository.java`
- `pos-backend/src/main/java/com/sgtech/pos/service/InventoryService.java`

**Fix:**
```java
// Before (incorrect):
List<Item> findByQuantityLessThanOrEqual(int maxQuantity);

// After (correct):
List<Item> findByQuantityLessThanEqual(int maxQuantity);
```

**Note:** Spring Data JPA supports keywords like `LessThan`, `LessThanEqual`, `GreaterThan`, `GreaterThanEqual`, but NOT `LessThanOrEqual` or `GreaterThanOrEqual`. For "or" conditions, use `@Query` annotation with custom JPQL.

✅ All 30 tests should now pass after this fix.