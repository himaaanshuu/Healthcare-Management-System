@echo off
title Hospital Management System - PostgreSQL
echo ========================================
echo    Hospital Management System Setup
echo ========================================
echo PostgreSQL Version
echo.

echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

echo Java found!
echo.

echo Checking project structure...
if not exist "src\main\Main.java" (
    echo ERROR: Main.java not found in src\main\
    echo Please ensure Main.java is in src\main\ folder
    pause
    exit /b 1
)

if not exist "lib\postgresql-42.6.0.jar" (
    echo WARNING: PostgreSQL JDBC Driver not found in lib\ folder
    echo Please download from: https://jdbc.postgresql.org/download.html
    echo and place in lib\ folder as postgresql-42.6.0.jar
    echo.
    echo You can download it directly from:
    echo https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
    echo.
)

echo Creating bin directory...
if not exist bin mkdir bin

echo.
echo ========================================
echo        Compiling Java Files
echo ========================================
echo.

echo Step 1: Compiling model classes...
javac -cp ".;lib\*" -d bin src\model\*.java
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile model classes
    pause
    exit /b 1
)

echo Step 2: Compiling DAO classes...
javac -cp ".;lib\*;bin" -d bin src\dao\*.java
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile DAO classes
    pause
    exit /b 1
)

echo Step 3: Compiling GUI classes...
javac -cp ".;lib\*;bin" -d bin src\gui\*.java
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile GUI classes
    pause
    exit /b 1
)

echo Step 4: Compiling main class...
javac -cp ".;lib\*;bin" -d bin src\main\Main.java
if %errorlevel% neq 0 (
    echo ERROR: Failed to compile main class
    pause
    exit /b 1
)

echo.
echo ========================================
echo        Compilation Successful!
echo ========================================
echo.

echo Testing database connection...
java -cp ".;bin;lib\*" -Dtest.db=true main.Main
if %errorlevel% neq 0 (
    echo WARNING: Database connection test failed
    echo Please check:
    echo 1. PostgreSQL server is running on port 5432
    echo 2. Database 'hospital_management' exists
    echo 3. Update credentials in DatabaseConnection.java
    echo 4. Username and password are correct
    echo.
    set /p CONTINUE="Continue anyway? (Y/N): "
    if /i "%CONTINUE%" neq "Y" (
        echo Operation cancelled by user
        pause
        exit /b 0
    )
)

echo.
echo ========================================
echo    Starting Hospital Management System
echo ========================================
echo.
echo PostgreSQL Database: hospital_management
echo Server: localhost:5432
echo.

java -cp ".;bin;lib\*" main.Main

echo.
echo ========================================
echo    Application Closed
echo ========================================
echo.
pause