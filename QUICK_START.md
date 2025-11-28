# Quick Start Guide - POS System Demo

This guide provides quick commands to run the reengineered POS system for demonstration purposes.

## Prerequisites

- Java 17+ installed
- Maven installed
- Node.js and npm installed
- PostgreSQL installed and running
- Git (optional, for cloning)

## Step 1: Database Setup

### Create Database

```powershell
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE pos_db;

# Exit psql
\q
```

### Update Database Password

Edit `pos-backend/src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

## Step 2: Start Backend

```powershell
# Navigate to backend directory
cd pos-backend

# Start Spring Boot application
mvn spring-boot:run
```

**Wait for**: `Tomcat started on port(s): 8081 (http)`

## Step 3: Migrate Data (In New Terminal)

```powershell
# From project root directory
.\test-scripts\migrate-data.ps1
```

**Expected Output**: `Migration completed successfully!`

## Step 4: Start Frontend (In New Terminal)

```powershell
# Navigate to frontend directory
cd pos-frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

**Wait for**: `Local: http://localhost:3000`

## Step 5: Access Application

Open browser and navigate to:
```
http://localhost:3000
```

## Login Credentials

### Admin Account
- **Username**: `110001`
- **Password**: `1`

### Cashier Account
- **Username**: `110002`
- **Password**: `lehigh2016`

## Quick Commands Summary

### Windows (PowerShell)

```powershell
# Terminal 1: Backend
cd pos-backend
mvn spring-boot:run

# Terminal 2: Migrate Data
.\test-scripts\migrate-data.ps1

# Terminal 3: Frontend
cd pos-frontend
npm run dev
```

### Linux/Mac (Bash)

```bash
# Terminal 1: Backend
cd pos-backend
mvn spring-boot:run

# Terminal 2: Migrate Data
./test-scripts/migrate-data.ps1

# Terminal 3: Frontend
cd pos-frontend
npm run dev
```

## Troubleshooting

### Port 8081 Already in Use
```powershell
# Find process using port 8081
netstat -ano | findstr :8081

# Kill process (replace PID with actual process ID)
taskkill /F /PID <PID>
```

### Port 3000 Already in Use
```powershell
# Find process using port 3000
netstat -ano | findstr :3000

# Kill process
taskkill /F /PID <PID>
```

### Database Connection Error
- Verify PostgreSQL is running: `Get-Service postgresql*`
- Check password in `application.properties`
- Ensure database `pos_db` exists

### Migration Fails
- Ensure backend is running on port 8081
- Check that `Database/` folder exists with `.txt` files
- Verify PostgreSQL is accessible

## Demo Flow

1. **Login** → Use admin credentials (`110001` / `1`)
2. **Admin Dashboard** → View Employee Management, Inventory Management
3. **Logout** → Logout and login as Cashier (`110002` / `lehigh2016`)
4. **Cashier Dashboard** → Process Sales, Rentals, Returns
5. **Test Features**:
   - Add items to sale
   - Apply coupon codes
   - Create rental transactions
   - Process returns

## Stop Services

Press `Ctrl+C` in each terminal to stop:
- Backend server
- Frontend dev server

## One-Line Quick Start (After Initial Setup)

```powershell
# Terminal 1
cd pos-backend && mvn spring-boot:run

# Terminal 2  
cd pos-frontend && npm run dev
```

**Note**: Data migration only needs to be run once after database setup.

---

**Last Updated**: 2025-11-28  
**System Version**: Reengineered POS System v1.0.0

