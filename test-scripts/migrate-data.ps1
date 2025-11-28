# PowerShell Script to Migrate Legacy Data to PostgreSQL
# This script calls the migration endpoint to import data from legacy .txt files

$BaseUrl = "http://localhost:8081/api"
# Backend runs from pos-backend directory, so we need to go up one level
$DatabasePath = "../Database"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Data Migration Script" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Starting data migration..." -ForegroundColor Yellow
Write-Host "Database Path: $DatabasePath" -ForegroundColor Yellow
Write-Host ""

try {
    $migrationUrl = "$BaseUrl/migration/migrate?databasePath=$DatabasePath"
    
    Write-Host "Calling migration endpoint: $migrationUrl" -ForegroundColor Cyan
    Write-Host ""
    
    $response = Invoke-RestMethod -Uri $migrationUrl -Method Post -ContentType "application/json"
    
    Write-Host "Migration completed successfully!" -ForegroundColor Green
    Write-Host "Response: $response" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now login with credentials from Database/employeeDatabase.txt" -ForegroundColor Cyan
    
} catch {
    Write-Host "Migration failed!" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Make sure:" -ForegroundColor Yellow
        Write-Host "  1. Backend server is running on http://localhost:8081" -ForegroundColor Yellow
    Write-Host "  2. PostgreSQL database is running and accessible" -ForegroundColor Yellow
    Write-Host "  3. Database schema has been created (run schema.sql)" -ForegroundColor Yellow
    Write-Host "  4. Database folder exists with employeeDatabase.txt, itemDatabase.txt, etc." -ForegroundColor Yellow
    exit 1
}

