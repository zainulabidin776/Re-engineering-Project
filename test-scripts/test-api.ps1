# PowerShell API Testing Script for POS System
# This script tests all major API endpoints

$BaseUrl = "http://localhost:8081/api"
$Token = ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "POS System API Test Suite" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Health Check
Write-Host "Test 1: Health Check" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/auth/health" -Method Get
    Write-Host "Status: OK" -ForegroundColor Green
    Write-Host "Response: $response" -ForegroundColor Green
} catch {
    Write-Host "Status: Failed" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: Login
Write-Host "Test 2: Login" -ForegroundColor Yellow
Write-Host "-------------" -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "testuser"
        password = "password123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $Token = $loginResponse.token
    Write-Host "Status: OK" -ForegroundColor Green
    Write-Host "Token received: $($Token.Substring(0, [Math]::Min(20, $Token.Length)))..." -ForegroundColor Green
    Write-Host "Username: $($loginResponse.username)" -ForegroundColor Green
    Write-Host "Position: $($loginResponse.position)" -ForegroundColor Green
} catch {
    Write-Host "Status: Failed" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "Note: Make sure test user exists in database" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Test 3: Get All Items
Write-Host "Test 3: Get All Items" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $Token"
    }
    $items = Invoke-RestMethod -Uri "$BaseUrl/items" -Method Get -Headers $headers
    Write-Host "Status: OK" -ForegroundColor Green
    Write-Host "Items count: $($items.Count)" -ForegroundColor Green
} catch {
    Write-Host "Status: Failed" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get Item by ID
Write-Host "Test 4: Get Item by Item ID" -ForegroundColor Yellow
Write-Host "---------------------------" -ForegroundColor Yellow
try {
    $item = Invoke-RestMethod -Uri "$BaseUrl/items/item-id/1001" -Method Get -Headers $headers
    Write-Host "Status: OK" -ForegroundColor Green
    Write-Host "Item Name: $($item.name)" -ForegroundColor Green
    Write-Host "Item Price: $($item.price)" -ForegroundColor Green
} catch {
    Write-Host "Status: Failed (Item may not exist)" -ForegroundColor Yellow
}
Write-Host ""

# Test 5: Search Items
Write-Host "Test 5: Search Items" -ForegroundColor Yellow
Write-Host "-------------------" -ForegroundColor Yellow
try {
    $searchResults = Invoke-RestMethod -Uri "$BaseUrl/items/search?name=test" -Method Get -Headers $headers
    Write-Host "Status: OK" -ForegroundColor Green
    Write-Host "Search results count: $($searchResults.Count)" -ForegroundColor Green
} catch {
    Write-Host "Status: Failed" -ForegroundColor Red
}
Write-Host ""

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "API Tests Complete" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

