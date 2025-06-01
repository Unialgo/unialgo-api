#!/bin/bash

# Test script to verify Judge0 is running and accessible

echo "==================================="
echo "Judge0 Integration Test Script"
echo "==================================="

# Wait for services to be ready
echo "Waiting for Judge0 services to be ready..."
sleep 10

# Test 1: Check if Judge0 API is accessible
echo -e "\n[Test 1] Checking Judge0 API availability..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:2358/about)

if [ "$RESPONSE" -eq 200 ]; then
    echo "✅ Judge0 API is accessible"
    
    # Get more details about the Judge0 instance
    echo -e "\n[Test 1.1] Getting Judge0 system info..."
    curl -s http://localhost:2358/about | jq '.'
else
    echo "❌ Judge0 API is not accessible (HTTP $RESPONSE)"
    exit 1
fi

# Test 2: Submit a simple code for execution
echo -e "\n[Test 2] Submitting a simple Python code..."
SUBMISSION_RESPONSE=$(curl -s -X POST http://localhost:2358/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "source_code": "print(\"Hello from Judge0!\")",
    "language_id": 71,
    "stdin": "",
    "expected_output": "Hello from Judge0!\n"
  }')

TOKEN=$(echo $SUBMISSION_RESPONSE | jq -r '.token')

if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
    echo "✅ Submission created with token: $TOKEN"
    
    # Wait for execution
    echo "Waiting for execution to complete..."
    sleep 3
    
    # Get submission result
    echo -e "\n[Test 2.1] Getting submission result..."
    RESULT=$(curl -s "http://localhost:2358/submissions/$TOKEN?base64_encoded=false&fields=*")
    
    echo "Result:"
    echo $RESULT | jq '.'
    
    STATUS_ID=$(echo $RESULT | jq -r '.status.id')
    if [ "$STATUS_ID" -eq 3 ]; then
        echo "✅ Code executed successfully"
    else
        echo "❌ Code execution failed"
    fi
else
    echo "❌ Failed to create submission"
    exit 1
fi

# Test 3: Check available languages
echo -e "\n[Test 3] Getting available languages..."
LANGUAGES=$(curl -s http://localhost:2358/languages)
LANG_COUNT=$(echo $LANGUAGES | jq '. | length')

if [ "$LANG_COUNT" -gt 0 ]; then
    echo "✅ Found $LANG_COUNT available languages"
    echo "Sample languages:"
    echo $LANGUAGES | jq '.[0:5] | .[] | {id: .id, name: .name}'
else
    echo "❌ No languages found"
fi

# Test 4: Test from within Docker network (simulating unialgo-api access)
echo -e "\n[Test 4] Testing Judge0 access from within Docker network..."
docker run --rm --network unialgo-api_unialgo-network alpine/curl:latest \
  curl -s http://judge0-server:2358/about > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Judge0 is accessible from within the Docker network"
else
    echo "❌ Judge0 is not accessible from within the Docker network"
fi

echo -e "\n==================================="
echo "Test completed!"
echo "====================================="