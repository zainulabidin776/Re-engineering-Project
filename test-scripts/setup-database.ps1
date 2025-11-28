# Database Setup Script for POS System
# This script helps set up PostgreSQL database

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "PostgreSQL Database Setup" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Check if PostgreSQL is installed
Write-Host "Step 1: Checking PostgreSQL installation..." -ForegroundColor Yellow
try {
    $pgVersion = psql --version 2>&1
    Write-Host "PostgreSQL found: $pgVersion" -ForegroundColor Green
} catch {
    Write-Host "PostgreSQL not found in PATH" -ForegroundColor Red
    Write-Host "Please install PostgreSQL from: https://www.postgresql.org/download/" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Check if PostgreSQL service is running
Write-Host "Step 2: Checking PostgreSQL service..." -ForegroundColor Yellow
$pgService = Get-Service -Name postgresql* -ErrorAction SilentlyContinue
if ($pgService) {
    $running = $pgService | Where-Object { $_.Status -eq 'Running' }
    if ($running) {
        Write-Host "PostgreSQL service is running" -ForegroundColor Green
    } else {
        Write-Host "PostgreSQL service is not running" -ForegroundColor Red
        Write-Host "Attempting to start PostgreSQL service..." -ForegroundColor Yellow
        try {
            Start-Service -Name $pgService[0].Name
            Write-Host "PostgreSQL service started" -ForegroundColor Green
        } catch {
            Write-Host "Failed to start PostgreSQL service. Please start it manually." -ForegroundColor Red
            Write-Host "Run: Start-Service -Name postgresql-x64-XX" -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "PostgreSQL service not found" -ForegroundColor Yellow
    Write-Host "Please ensure PostgreSQL is installed and service is running" -ForegroundColor Yellow
}

Write-Host ""

# Prompt for PostgreSQL password
Write-Host "Step 3: Database Setup" -ForegroundColor Yellow
$pgPassword = Read-Host "Enter PostgreSQL password for user 'postgres' (default: postgres)" 
if ([string]::IsNullOrWhiteSpace($pgPassword)) {
    $pgPassword = "postgres"
}

$env:PGPASSWORD = $pgPassword

Write-Host ""
Write-Host "Attempting to connect to PostgreSQL..." -ForegroundColor Yellow

# Test connection
try {
    $testResult = psql -U postgres -c "SELECT version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Connection successful!" -ForegroundColor Green
    } else {
        Write-Host "Connection failed. Please check:" -ForegroundColor Red
        Write-Host "  1. PostgreSQL is running" -ForegroundColor Yellow
        Write-Host "  2. Password is correct" -ForegroundColor Yellow
        Write-Host "  3. PostgreSQL is listening on port 5432" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Failed to connect to PostgreSQL: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Create database
Write-Host "Step 4: Creating database 'pos_db'..." -ForegroundColor Yellow
try {
    $dbExists = psql -U postgres -lqt 2>&1 | Select-String -Pattern "pos_db"
    if ($dbExists) {
        Write-Host "Database 'pos_db' already exists" -ForegroundColor Green
    } else {
        psql -U postgres -c "CREATE DATABASE pos_db;" 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Database 'pos_db' created successfully" -ForegroundColor Green
        } else {
            Write-Host "Failed to create database" -ForegroundColor Red
            exit 1
        }
    }
} catch {
    Write-Host "Error creating database: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Check if schema file exists
Write-Host "Step 5: Creating database schema..." -ForegroundColor Yellow
$schemaFile = "Database\schema.sql"
if (Test-Path $schemaFile) {
    try {
        psql -U postgres -d pos_db -f $schemaFile 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Schema created successfully" -ForegroundColor Green
        } else {
            Write-Host "Schema may already exist or there were errors" -ForegroundColor Yellow
            Write-Host "You can manually run: psql -U postgres -d pos_db -f Database\schema.sql" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "Error creating schema: $_" -ForegroundColor Red
    }
} else {
    Write-Host "Schema file not found at: $schemaFile" -ForegroundColor Yellow
    Write-Host "Please ensure Database/schema.sql exists" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Database Setup Complete!" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Update application.properties if your PostgreSQL password is not 'postgres'" -ForegroundColor Yellow
Write-Host "  2. Start the backend: cd pos-backend && mvn spring-boot:run" -ForegroundColor Yellow
Write-Host "  3. Migrate data: .\test-scripts\migrate-data.ps1" -ForegroundColor Yellow
Write-Host ""

