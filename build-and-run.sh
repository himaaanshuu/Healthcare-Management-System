#!/bin/bash

echo "========================================"
echo "   Hospital Management System Setup"
echo "========================================"
echo "PostgreSQL Version"
echo

# Check Java installation
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java JDK 8 or higher"
    exit 1
fi

echo "Java found!"
echo

# Check project structure
echo "Checking project structure..."
if [ ! -f "src/main/Main.java" ]; then
    echo "ERROR: Main.java not found in src/main/"
    echo "Please ensure Main.java is in src/main/ folder"
    exit 1
fi

if [ ! -f "lib/postgresql-42.6.0.jar" ]; then
    echo "WARNING: PostgreSQL JDBC Driver not found in lib/ folder"
    echo "Please download from: https://jdbc.postgresql.org/download.html"
    echo "and place in lib/ folder as postgresql-42.6.0.jar"
    echo
    echo "You can download it directly from:"
    echo "https://jdbc.postgresql.org/download/postgresql-42.6.0.jar"
    echo
fi

# Create bin directory
echo "Creating bin directory..."
mkdir -p bin

echo
echo "========================================"
echo "        Compiling Java Files"
echo "========================================"
echo

# Compile step by step for better error handling
echo "Step 1: Compiling model classes..."
javac -cp ".:lib/*" -d bin src/model/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile model classes"
    exit 1
fi

echo "Step 2: Compiling DAO classes..."
javac -cp ".:lib/*:bin" -d bin src/dao/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile DAO classes"
    exit 1
fi

echo "Step 3: Compiling GUI classes..."
javac -cp ".:lib/*:bin" -d bin src/gui/*.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile GUI classes"
    exit 1
fi

echo "Step 4: Compiling main class..."
javac -cp ".:lib/*:bin" -d bin src/main/Main.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile main class"
    exit 1
fi

echo
echo "========================================"
echo "        Compilation Successful!"
echo "========================================"
echo

# Test database connection
echo "Testing database connection..."
java -cp ".:bin:lib/*" -Dtest.db=true main.Main
if [ $? -ne 0 ]; then
    echo "WARNING: Database connection test failed"
    echo "Please check:"
    echo "1. PostgreSQL server is running on port 5432"
    echo "2. Database 'hospital_management' exists"
    echo "3. Update credentials in DatabaseConnection.java"
    echo "4. Username and password are correct"
    echo
    read -p "Continue anyway? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Operation cancelled by user"
        exit 0
    fi
fi

echo
echo "========================================"
echo "    Starting Hospital Management System"
echo "========================================"
echo
echo "PostgreSQL Database: hospital_management"
echo "Server: localhost:5432"
echo

java -cp ".:bin:lib/*" main.Main

echo
echo "========================================"
echo "    Application Closed"
echo "========================================"
echo