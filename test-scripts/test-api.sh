#!/bin/bash

# API Testing Script for POS System
# This script tests all major API endpoints

BASE_URL="http://localhost:8081/api"
TOKEN=""

echo "========================================="
echo "POS System API Test Suite"
echo "========================================="
echo ""

# Test 1: Health Check
echo "Test 1: Health Check"
echo "-------------------"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/auth/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')
echo "Status: $http_code"
echo "Response: $body"
echo ""

# Test 2: Login
echo "Test 2: Login"
echo "-------------"
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}')
echo "Response: $login_response"
TOKEN=$(echo $login_response | grep -o '"token":"[^"]*' | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
  echo "ERROR: Login failed - no token received"
  exit 1
fi
echo "Token received: ${TOKEN:0:20}..."
echo ""

# Test 3: Get All Items
echo "Test 3: Get All Items"
echo "-------------------"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/items" \
  -H "Authorization: Bearer $TOKEN")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')
echo "Status: $http_code"
echo "Items count: $(echo $body | grep -o '"id"' | wc -l)"
echo ""

# Test 4: Get Item by ID
echo "Test 4: Get Item by Item ID"
echo "---------------------------"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/item-id/1001" \
  -H "Authorization: Bearer $TOKEN")
http_code=$(echo "$response" | tail -n1)
echo "Status: $http_code"
echo ""

# Test 5: Search Items
echo "Test 5: Search Items"
echo "-------------------"
response=$(curl -s -w "\n%{http_code}" "$BASE_URL/items/search?name=test" \
  -H "Authorization: Bearer $TOKEN")
http_code=$(echo "$response" | tail -n1)
echo "Status: $http_code"
echo ""

echo "========================================="
echo "API Tests Complete"
echo "========================================="

