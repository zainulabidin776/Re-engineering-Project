#!/bin/bash

# End-to-End Test Script for POS System
# Tests complete user workflows

BASE_URL="http://localhost:8081/api"
TOKEN=""
EMPLOYEE_ID=""

echo "========================================="
echo "POS System End-to-End Test Suite"
echo "========================================="
echo ""

# Test 1: Login
echo "Test 1: Login"
echo "-------------"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
EMPLOYEE_ID=$(echo $LOGIN_RESPONSE | grep -o '"employeeId":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "ERROR: Login failed"
  exit 1
fi

echo "✓ Login successful"
echo ""

# Test 2: Get Items
echo "Test 2: Get All Items"
echo "-------------------"
ITEMS_RESPONSE=$(curl -s -w "\n%{http_code}" "$BASE_URL/items" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Employee-Id: $EMPLOYEE_ID")
HTTP_CODE=$(echo "$ITEMS_RESPONSE" | tail -n1)
BODY=$(echo "$ITEMS_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" -eq 200 ]; then
  ITEM_COUNT=$(echo $BODY | grep -o '"id"' | wc -l)
  echo "✓ Retrieved $ITEM_COUNT items"
else
  echo "✗ Failed to get items (HTTP $HTTP_CODE)"
fi
echo ""

# Test 3: Process Sale
echo "Test 3: Process Sale"
echo "-------------------"
SALE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/sales" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Employee-Id: $EMPLOYEE_ID" \
  -H "Content-Type: application/json" \
  -d '{"items":[{"itemId":1001,"quantity":1}],"couponCode":""}')
HTTP_CODE=$(echo "$SALE_RESPONSE" | tail -n1)

if [ "$HTTP_CODE" -eq 200 ]; then
  echo "✓ Sale processed successfully"
else
  echo "✗ Sale failed (HTTP $HTTP_CODE)"
  echo "  Note: Make sure item with ID 1001 exists"
fi
echo ""

# Test 4: Process Rental
echo "Test 4: Process Rental"
echo "-------------------"
DUE_DATE=$(date -d "+7 days" +%Y-%m-%d 2>/dev/null || date -v+7d +%Y-%m-%d)
RENTAL_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/rentals" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-Employee-Id: $EMPLOYEE_ID" \
  -H "Content-Type: application/json" \
  -d "{\"customerPhone\":\"5551234567\",\"dueDate\":\"$DUE_DATE\",\"items\":[{\"itemId\":2001,\"quantity\":1}]}")
HTTP_CODE=$(echo "$RENTAL_RESPONSE" | tail -n1)

if [ "$HTTP_CODE" -eq 200 ]; then
  echo "✓ Rental processed successfully"
else
  echo "✗ Rental failed (HTTP $HTTP_CODE)"
  echo "  Note: Make sure item with ID 2001 exists"
fi
echo ""

echo "========================================="
echo "End-to-End Tests Complete"
echo "========================================="

