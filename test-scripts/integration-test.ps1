# Comprehensive Integration Test Script for POS System
# Tests complete workflows end-to-end

$BaseUrl = "http://localhost:8081/api"
$Token = ""
$EmployeeId = ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "POS System Integration Test Suite" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Login
Write-Host "Test 1: Login" -ForegroundColor Yellow
Write-Host "-------------" -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "testuser"
        password = "password123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $Token = $loginResponse.token
    $EmployeeId = $loginResponse.employeeId
    Write-Host "Login successful" -ForegroundColor Green
    Write-Host "  Token: $($Token.Substring(0, [Math]::Min(20, $Token.Length)))..." -ForegroundColor Green
} catch {
    Write-Host "Login failed: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Test 2: Get Items
Write-Host "Test 2: Get All Items" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $Token"
        "X-Employee-Id" = $EmployeeId
    }
    $items = Invoke-RestMethod -Uri "$BaseUrl/items" -Method Get -Headers $headers
    Write-Host "Retrieved $($items.Count) items" -ForegroundColor Green
} catch {
    Write-Host "Failed to get items: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Process Sale
Write-Host "Test 3: Process Sale" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $saleBody = @{
        items = @(
            @{ itemId = 1001; quantity = 2 }
        )
        couponCode = ""
    } | ConvertTo-Json

    $sale = Invoke-RestMethod -Uri "$BaseUrl/sales" -Method Post -Body $saleBody -ContentType "application/json" -Headers $headers
    Write-Host "Sale processed successfully" -ForegroundColor Green
    Write-Host "  Sale ID: $($sale.id)" -ForegroundColor Green
    Write-Host "  Total: $($sale.finalTotal)" -ForegroundColor Green
} catch {
    Write-Host "Sale failed: $_" -ForegroundColor Red
    Write-Host "  Note: Make sure item with ID 1001 exists" -ForegroundColor Yellow
}
Write-Host ""

# Test 4: Process Rental
Write-Host "Test 4: Process Rental" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $rentalBody = @{
        customerPhone = "5551234567"
        dueDate = (Get-Date).AddDays(7).ToString("yyyy-MM-dd")
        items = @(
            @{ itemId = 2001; quantity = 1 }
        )
    } | ConvertTo-Json

    $rental = Invoke-RestMethod -Uri "$BaseUrl/rentals" -Method Post -Body $rentalBody -ContentType "application/json" -Headers $headers
    Write-Host "Rental processed successfully" -ForegroundColor Green
    Write-Host "  Rental ID: $($rental.id)" -ForegroundColor Green
} catch {
    Write-Host "Rental failed: $_" -ForegroundColor Red
    Write-Host "  Note: Make sure item with ID 2001 exists" -ForegroundColor Yellow
}
Write-Host ""

# Test 5: Get Outstanding Rentals
Write-Host "Test 5: Get Outstanding Rentals" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $outstanding = Invoke-RestMethod -Uri "$BaseUrl/rentals/outstanding/5551234567" -Method Get -Headers $headers
    Write-Host "Retrieved $($outstanding.Count) outstanding rentals" -ForegroundColor Green
} catch {
    Write-Host "Failed to get outstanding rentals: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Integration Tests Complete" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
