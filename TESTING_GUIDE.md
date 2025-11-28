# Testing Guide - POS System

## Overview

This guide provides instructions for testing the reengineered POS system, including backend unit tests, frontend component tests, and API integration tests.

## Backend Testing

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL (for integration tests)

### Running Backend Tests

```bash
cd pos-backend
mvn test
```

### Test Coverage

#### Unit Tests

1. **AuthControllerTest**
   - Tests login functionality
   - Tests invalid credentials handling
   - Tests user not found scenarios

2. **SaleServiceTest**
   - Tests sale processing
   - Tests multiple items in sale
   - Tests insufficient inventory handling

3. **RentalServiceTest**
   - Tests rental processing
   - Tests customer creation
   - Tests outstanding rentals lookup

4. **ItemRepositoryTest**
   - Tests repository queries
   - Tests item search functionality
   - Tests low stock queries

### Running Specific Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run with coverage
mvn test jacoco:report
```

## Frontend Testing

### Prerequisites

- Node.js 18+
- npm

### Running Frontend Tests

```bash
cd pos-frontend
npm install
npm test
```

### Test Coverage

#### Component Tests

1. **Login.test.tsx**
   - Tests login form rendering
   - Tests successful login flow
   - Tests error handling

### Running Specific Tests

```bash
# Run all tests
npm test

# Run in watch mode
npm test -- --watch

# Run with UI
npm run test:ui
```

## API Testing

### Using PowerShell Script (Windows)

```powershell
cd test-scripts
.\test-api.ps1
```

### Using Bash Script (Linux/Mac)

```bash
cd test-scripts
chmod +x test-api.sh
./test-api.sh
```

### Manual API Testing

#### 1. Health Check

```bash
curl http://localhost:8080/api/auth/health
```

#### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

#### 3. Get Items (with token)

```bash
curl http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Using Postman

1. Import the collection from `test-scripts/postman-collection.json`
2. Set environment variables:
   - `base_url`: http://localhost:8080/api
   - `token`: (will be set automatically after login)

## Integration Testing

### End-to-End Test Scenarios

#### Scenario 1: Complete Sale Flow

1. Login as cashier
2. Add items to cart
3. Apply coupon (optional)
4. Complete sale
5. Verify inventory updated
6. Verify sale recorded

#### Scenario 2: Rental Flow

1. Login as cashier
2. Enter customer phone
3. Add items for rental
4. Set due date
5. Complete rental
6. Verify customer created
7. Verify inventory updated

#### Scenario 3: Return Flow

1. Login as cashier
2. Lookup customer rentals
3. Select items to return
4. Process return
5. Verify inventory restored
6. Verify refund calculated

## Test Data Setup

### Creating Test Data

Before running tests, ensure test data exists:

```sql
-- Insert test employee
INSERT INTO employees (username, first_name, last_name, position, password_hash)
VALUES ('testuser', 'Test', 'User', 'Cashier', '$2a$10$...');

-- Insert test items
INSERT INTO items (item_id, name, price, quantity)
VALUES (1001, 'Test Item', 10.00, 100);
```

## Test Results

Test results are generated in:
- Backend: `pos-backend/target/surefire-reports/`
- Frontend: `pos-frontend/coverage/`

## Continuous Integration

For CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run Backend Tests
  run: |
    cd pos-backend
    mvn test

- name: Run Frontend Tests
  run: |
    cd pos-frontend
    npm test
```

## Troubleshooting

### Backend Tests Fail

- Ensure PostgreSQL is running
- Check database connection in `application.properties`
- Verify test data exists

### Frontend Tests Fail

- Clear node_modules and reinstall: `rm -rf node_modules && npm install`
- Check for TypeScript errors: `npm run build`

### API Tests Fail

- Ensure backend is running on port 8080
- Verify test user exists in database
- Check CORS configuration

