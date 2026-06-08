#!/bin/bash

echo "========================================"
echo "  DT SaaS Spring Boot Backend"
echo "========================================"
echo

cd "$(dirname "$0")"

echo "Starting Spring Boot application..."
echo

./mvnw spring-boot:run