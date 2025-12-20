# #!/bin/bash

# echo "========================================"
# echo "   Hospital Management System Setup"
# echo "========================================"
# echo "PostgreSQL Version"
# echo

# # Check Java installation
# echo "Checking Java installation..."
# if ! command -v java &> /dev/null; then
#     echo "ERROR: Java is not installed or not in PATH"
#     echo "Please install Java JDK 8 or higher"
#     exit 1
# fi

# echo "Java found!"
# echo

# # Check project structure
# echo "Checking project structure..."
# if [ ! -f "src/main/Main.java" ]; then
#     echo "ERROR: Main.java not found in src/main/"
#     echo "Please ensure Main.java is in src/main/ folder"
#     exit 1
# fi

# if [ ! -f "lib/postgresql-42.6.0.jar" ]; then
#     echo "WARNING: PostgreSQL JDBC Driver not found in lib/ folder"
#     echo "Please download from: https://jdbc.postgresql.org/download.html"
#     echo "and place in lib/ folder as postgresql-42.6.0.jar"
#     echo
#     echo "You can download it directly from:"
#     echo "https://jdbc.postgresql.org/download/postgresql-42.6.0.jar"
#     echo
# fi

# # Create bin directory
# echo "Creating bin directory..."
# mkdir -p bin

# echo
# echo "========================================"
# echo "        Compiling Java Files"
# echo "========================================"
# echo

# # Compile step by step for better error handling
# echo "Step 1: Compiling model classes..."
# javac -cp ".:lib/*" -d bin src/model/*.java
# if [ $? -ne 0 ]; then
#     echo "ERROR: Failed to compile model classes"
#     exit 1
# fi

# echo "Step 2: Compiling DAO classes..."
# javac -cp ".:lib/*:bin" -d bin src/dao/*.java
# if [ $? -ne 0 ]; then
#     echo "ERROR: Failed to compile DAO classes"
#     exit 1
# fi

# echo "Step 3: Compiling GUI classes..."
# javac -cp ".:lib/*:bin" -d bin src/gui/*.java
# if [ $? -ne 0 ]; then
#     echo "ERROR: Failed to compile GUI classes"
#     exit 1
# fi

# echo "Step 4: Compiling main class..."
# javac -cp ".:lib/*:bin" -d bin src/main/Main.java
# if [ $? -ne 0 ]; then
#     echo "ERROR: Failed to compile main class"
#     exit 1
# fi

# echo
# echo "========================================"
# echo "        Compilation Successful!"
# echo "========================================"
# echo

# # Test database connection
# echo "Testing database connection..."
# java -cp ".:bin:lib/*" -Dtest.db=true main.Main
# if [ $? -ne 0 ]; then
#     echo "WARNING: Database connection test failed"
#     echo "Please check:"
#     echo "1. PostgreSQL server is running on port 5432"
#     echo "2. Database 'hospital_management' exists"
#     echo "3. Update credentials in DatabaseConnection.java"
#     echo "4. Username and password are correct"
#     echo
#     read -p "Continue anyway? (y/n): " -n 1 -r
#     echo
#     if [[ ! $REPLY =~ ^[Yy]$ ]]; then
#         echo "Operation cancelled by user"
#         exit 0
#     fi
# fi

# echo
# echo "========================================"
# echo "    Starting Hospital Management System"
# echo "========================================"
# echo
# echo "PostgreSQL Database: hospital_management"
# echo "Server: localhost:5432"
# echo

# java -cp ".:bin:lib/*" main.Main

# echo
# echo "========================================"
# echo "    Application Closed"
# echo "========================================"
# echo

#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Hospital Management System Build Tool${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo -e "${RED}Error: Java compiler (javac) not found!${NC}"
    echo "Please install Java JDK 8 or higher"
    exit 1
fi

echo -e "${GREEN}✓ Java compiler found${NC}"
JAVA_VERSION=$(javac -version 2>&1 | head -n 1)
echo "  Using: $JAVA_VERSION"
echo ""

# Create necessary directories
echo -e "${YELLOW}Setting up directories...${NC}"
mkdir -p bin
mkdir -p lib
mkdir -p logs
echo -e "${GREEN}✓ Directories created${NC}"
echo ""

# Find all Java source files
echo -e "${YELLOW}Scanning for source files...${NC}"
SOURCE_FILES=$(find src -name "*.java" | tr '\n' ' ')
SOURCE_COUNT=$(echo "$SOURCE_FILES" | wc -w)
echo "Found $SOURCE_COUNT Java source files"

# Check for duplicate utils folder issue
if [ -d "src/utils/utils" ]; then
    echo -e "${RED}⚠ Warning: Found nested utils folder (src/utils/utils/)${NC}"
    echo "  This may cause package declaration issues."
    echo "  Consider moving files from src/utils/utils/ to src/utils/"
    read -p "  Continue anyway? (y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Build cancelled."
        exit 1
    fi
fi

# Check package declarations
echo ""
echo -e "${YELLOW}Checking package declarations...${NC}"
ERROR_COUNT=0
for file in src/utils/*.java; do
    if [ -f "$file" ]; then
        if grep -q "package utils;" "$file"; then
            echo -e "${GREEN}✓ $(basename "$file") has correct package${NC}"
        else
            echo -e "${RED}✗ $(basename "$file") missing package declaration${NC}"
            ERROR_COUNT=$((ERROR_COUNT + 1))
        fi
    fi
done

if [ $ERROR_COUNT -gt 0 ]; then
    echo -e "${RED}Found $ERROR_COUNT package errors${NC}"
    read -p "Continue anyway? (y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Compile the project
echo ""
echo -e "${YELLOW}Compiling source files...${NC}"
echo "This may take a moment..."
echo ""

# Build classpath
CLASSPATH="."
if [ -d "lib" ] && [ "$(ls -A lib/*.jar 2>/dev/null)" ]; then
    for jar in lib/*.jar; do
        CLASSPATH="$CLASSPATH:$jar"
    done
fi

# Compile command
javac -d bin \
      -cp "$CLASSPATH" \
      -Xlint:unchecked \
      -Xlint:deprecation \
      $SOURCE_FILES

COMPILE_STATUS=$?

if [ $COMPILE_STATUS -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful!${NC}"
    echo "  Output directory: bin/"
    
    # Count compiled classes
    CLASS_COUNT=$(find bin -name "*.class" | wc -l)
    echo "  Compiled $CLASS_COUNT class files"
    
    # Create manifest for JAR
    echo ""
    echo -e "${YELLOW}Creating JAR manifest...${NC}"
    cat > manifest.txt << EOF
Manifest-Version: 1.0
Main-Class: main.Main
Created-By: Hospital Management System Build Tool
Build-Date: $(date)
EOF
    echo -e "${GREEN}✓ Manifest created${NC}"
    
    # Package into JAR (optional)
    echo ""
    echo -e "${YELLOW}Creating JAR file...${NC}"
    jar cvfm HospitalManagementSystem.jar manifest.txt -C bin . > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        JAR_SIZE=$(du -h HospitalManagementSystem.jar | cut -f1)
        echo -e "${GREEN}✓ JAR created: HospitalManagementSystem.jar ($JAR_SIZE)${NC}"
    fi
    
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  Build Summary:${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo -e "Source files: $SOURCE_COUNT"
    echo -e "Class files:  $CLASS_COUNT"
    echo -e "Output:       bin/"
    echo -e "JAR file:     HospitalManagementSystem.jar"
    echo ""
    
    # Ask to run the application
    read -p "Run the application now? (y/n): " -n 1 -r
    echo ""
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Starting Hospital Management System...${NC}"
        echo ""
        java -cp "bin:." main.Main
    else
        echo ""
        echo -e "${GREEN}Build completed successfully!${NC}"
        echo "To run manually:"
        echo "  java -cp \"bin:.\" main.Main"
        echo "Or using the JAR:"
        echo "  java -jar HospitalManagementSystem.jar"
    fi
    
else
    echo ""
    echo -e "${RED}✗ Compilation failed!${NC}"
    echo "Check the errors above and fix your code."
    echo ""
    echo "Common issues:"
    echo "  1. Missing package declarations in utils/ folder"
    echo "  2. Nested utils folder (src/utils/utils/)"
    echo "  3. Missing imports or dependencies"
    exit 1
fi

# Cleanup
rm -f manifest.txt 2>/dev/null

echo ""
echo -e "${BLUE}========================================${NC}"
echo ""
