# Script to update PostgreSQL password in application.properties
# Usage: .\update-db-password.ps1 -Password "your_password_here"

param(
    [Parameter(Mandatory=$true)]
    [string]$Password
)

$propertiesFile = "pos-backend\src\main\resources\application.properties"

if (Test-Path $propertiesFile) {
    $content = Get-Content $propertiesFile -Raw
    $content = $content -replace 'spring\.datasource\.password=.*', "spring.datasource.password=$Password"
    Set-Content -Path $propertiesFile -Value $content -NoNewline
    Write-Host "Password updated successfully in $propertiesFile" -ForegroundColor Green
} else {
    Write-Host "Error: $propertiesFile not found!" -ForegroundColor Red
    exit 1
}

