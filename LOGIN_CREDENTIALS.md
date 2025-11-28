# Login Credentials

This document contains all login credentials for the Point-of-Sale (POS) System.

## ⚠️ Security Notice

**IMPORTANT:** These are default credentials from the legacy system. For production use:
- Change all passwords immediately
- Use strong, unique passwords
- Implement password policies
- Store passwords securely (hashed, never in plain text)

---

## Employee Login Credentials

After migrating data from the legacy system, you can use the following credentials to log in to the web application.

### Format
- **Username**: Employee ID (e.g., `110001`)
- **Password**: As listed below
- **Position**: Admin or Cashier (determines dashboard access)

### Admin Accounts

| Employee ID | Username | Name | Position | Password |
|-------------|----------|------|----------|----------|
| 110001 | `110001` | Harry Larry | Admin | `1` |
| 110003 | `110003` | Clayton Watson | Admin | `lehigh2017` |
| 110005 | `110005` | Amy Adams | Admin | `110` |
| 110009 | `110009` | John Candle | Admin | `candles` |

### Cashier Accounts

| Employee ID | Username | Name | Position | Password |
|-------------|----------|------|----------|----------|
| 110002 | `110002` | Debra Cooper | Cashier | `lehigh2016` |
| 110004 | `110004` | Seth Moss | Cashier | `lehigh2018` |
| 110006 | `110006` | Mike Spears | Cashier | `lehigh` |
| 110011 | `110011` | Anthony Hopkins | Cashier | `theman` |
| 110012 | `110012` | Robert Lek | Cashier | `huehue` |
| 110013 | `110013` | Johnny Cage | Cashier | `mortalkombat` |
| 110014 | `110014` | Eim Lou | Cashier | `cowboybebop` |
| 110015 | `110015` | Michael Scott | Cashier | `thatswhatshesaid` |

---

## Quick Reference - Test Accounts

### Recommended Test Accounts

**For Admin Testing:**
- **Username**: `110001`
- **Password**: `1`
- **Access**: Full admin dashboard (employee management, inventory management, all transactions)

**For Cashier Testing:**
- **Username**: `110002`
- **Password**: `lehigh2016`
- **Access**: Cashier dashboard (sales, rentals, returns only)

---

## Database Credentials

### PostgreSQL Database

- **Host**: `localhost`
- **Port**: `5432`
- **Database Name**: `pos_db`
- **Username**: `postgres`
- **Password**: `Complicated@1` (as configured in `application.properties`)

**Note**: Update the password in `pos-backend/src/main/resources/application.properties` if your PostgreSQL password is different.

---

## Application URLs

### Frontend
- **URL**: `http://localhost:3000` (or `http://localhost:5173` if using Vite default port)
- **Login Page**: `http://localhost:3000/login`

### Backend API
- **Base URL**: `http://localhost:8081/api`
- **Login Endpoint**: `POST http://localhost:8081/api/auth/login`

---

## How to Log In

1. **Start the Backend**:
   ```powershell
   cd pos-backend
   mvn spring-boot:run
   ```

2. **Start the Frontend** (in a new terminal):
   ```powershell
   cd pos-frontend
   npm run dev
   ```

3. **Open Browser**: Navigate to `http://localhost:3000`

4. **Enter Credentials**:
   - **Username**: Employee ID (e.g., `110001`)
   - **Password**: Password from the table above (e.g., `1`)

5. **Access Dashboard**:
   - **Admin users** → Redirected to `/admin` dashboard
   - **Cashier users** → Redirected to `/cashier` dashboard

---

## Data Migration

If you haven't migrated data yet, the employee accounts won't exist in the database. To migrate:

1. **Ensure backend is running**

2. **Run migration script**:
   ```powershell
   .\test-scripts\migrate-data.ps1
   ```

   Or manually call the migration endpoint:
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8081/api/migration/migrate?databasePath=Database" -Method Post
   ```

3. **After migration**, all employees from `Database/employeeDatabase.txt` will be available for login.

---

## Password Security Notes

### Current Implementation

- **Legacy System**: Passwords stored in plain text in `Database/employeeDatabase.txt`
- **Reengineered System**: 
  - Passwords are hashed using BCrypt during migration
  - Authentication supports both plain text (for legacy migration) and hashed passwords
  - All new passwords should be hashed

### Password Hashing

The system uses BCrypt password hashing:
- Passwords are automatically hashed when migrated from legacy system
- New employees created through the admin panel will have hashed passwords
- Authentication service checks both plain text (for legacy compatibility) and hashed passwords

### Changing Passwords

Currently, password changes must be done through:
1. **Database directly** (using BCrypt hash)
2. **Admin panel** (if employee management feature is implemented)
3. **Data migration** (re-migrate with updated employee database file)

---

## Troubleshooting

### "Invalid username or password" Error

1. **Check if data was migrated**:
   - Verify employees exist in database
   - Check `Database/employeeDatabase.txt` for correct format

2. **Verify credentials**:
   - Username is the Employee ID (e.g., `110001`, not `Harry Larry`)
   - Password matches exactly (case-sensitive)

3. **Check backend logs**:
   - Look for authentication errors
   - Verify database connection

### "User not found" Error

- Employee may not exist in database
- Run data migration: `.\test-scripts\migrate-data.ps1`
- Verify employee exists in `Database/employeeDatabase.txt`

### Cannot Access Admin Dashboard

- Verify employee position is `Admin` (not `Cashier`)
- Check user object in browser console after login
- Ensure JWT token includes correct position

---

## Legacy System Format

The legacy system stores employees in `Database/employeeDatabase.txt` with the following format:

```
EmployeeID Position FirstName LastName Password
```

Example:
```
110001 Admin Harry Larry 1
110002 Cashier Debra Cooper lehigh2016
```

---

## Additional Resources

- **Database Setup**: See `DATABASE_SETUP.md`
- **Setup Instructions**: See `SETUP_INSTRUCTIONS.md`
- **Migration Guide**: See `test-scripts/migrate-data.ps1`
- **API Documentation**: See backend README or API endpoints

---

## Summary Table

| Role | Username Example | Password Example | Dashboard |
|------|------------------|------------------|-----------|
| Admin | `110001` | `1` | `/admin` |
| Cashier | `110002` | `lehigh2016` | `/cashier` |

---

**Last Updated**: 2025-11-28  
**System Version**: Reengineered POS System v1.0.0

