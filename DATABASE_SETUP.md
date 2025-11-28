# Database Setup Guide

## PostgreSQL Setup Instructions

### Step 1: Install PostgreSQL (if not installed)

Download and install PostgreSQL from: https://www.postgresql.org/download/

### Step 2: Start PostgreSQL Service

**Windows:**
```powershell
# Check if PostgreSQL is running
Get-Service -Name postgresql*

# Start PostgreSQL (replace X with your version number)
Start-Service postgresql-x64-XX
```

**Or use Services:**
- Press `Win + R`, type `services.msc`
- Find "postgresql" service
- Right-click → Start

### Step 3: Create Database

Open PostgreSQL command line (psql) or pgAdmin:

```sql
-- Connect to PostgreSQL (you'll be prompted for password)
psql -U postgres

-- Create database
CREATE DATABASE pos_db;

-- Verify database was created
\l
```

### Step 4: Update Application Configuration

The application is configured to use:
- **Username**: `postgres`
- **Password**: `postgres` (default)
- **Database**: `pos_db`
- **Host**: `localhost:5432`

**If your PostgreSQL password is different**, update `pos-backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=postgres
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

### Step 5: Create Database Schema

Run the schema SQL file:

```powershell
# Using psql command line
psql -U postgres -d pos_db -f Database/schema.sql

# Or using pgAdmin:
# 1. Connect to pos_db database
# 2. Right-click → Query Tool
# 3. Open Database/schema.sql
# 4. Execute
```

### Step 6: Verify Connection

Test the connection:

```powershell
# Test PostgreSQL connection
psql -U postgres -d pos_db -c "SELECT version();"
```

### Step 7: Start Backend

After database is set up:

```powershell
cd pos-backend
mvn spring-boot:run
```

## Troubleshooting

### Error: "password authentication failed"

**Solution 1: Update password in application.properties**
- Find your PostgreSQL password
- Update `application.properties` with correct password

**Solution 2: Reset PostgreSQL password**
```sql
-- Connect as superuser
psql -U postgres

-- Change password
ALTER USER postgres WITH PASSWORD 'postgres';
```

**Solution 3: Use a different user**
```sql
-- Create new user
CREATE USER pos_user WITH PASSWORD 'pos_password';
CREATE DATABASE pos_db OWNER pos_user;
GRANT ALL PRIVILEGES ON DATABASE pos_db TO pos_user;
```

Then update `application.properties`:
```properties
spring.datasource.username=pos_user
spring.datasource.password=pos_password
```

### Error: "database pos_db does not exist"

**Solution:**
```sql
CREATE DATABASE pos_db;
```

### Error: "connection refused"

**Solution:**
- Ensure PostgreSQL service is running
- Check if PostgreSQL is listening on port 5432
- Verify firewall settings

### Check PostgreSQL Status

```powershell
# Windows - Check service status
Get-Service -Name postgresql*

# Check if port 5432 is listening
netstat -an | findstr 5432
```

## Quick Setup Script

Create a PowerShell script to set up everything:

```powershell
# setup-database.ps1
$env:PGPASSWORD = "postgres"
psql -U postgres -c "CREATE DATABASE pos_db;"
psql -U postgres -d pos_db -f Database/schema.sql
Write-Host "Database setup complete!"
```

## Default Credentials

The application expects:
- **Database**: `pos_db`
- **Username**: `postgres`
- **Password**: `postgres` (change if your PostgreSQL uses different password)

After setup, you can migrate data using:
```powershell
.\test-scripts\migrate-data.ps1
```

