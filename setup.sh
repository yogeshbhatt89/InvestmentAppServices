#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🚀 Starting project setup...${NC}"

# 1. Make this script executable if it's not already
if [ ! -x "$0" ]; then
    chmod +x "$0"
    echo -e "${GREEN}✓ Made setup script executable${NC}"
fi

# 2. Set up git hooks
echo -e "\n${YELLOW}🔧 Setting up git hooks...${NC}"
if [ ! -d ".githooks" ]; then
    echo -e "❌ Error: .githooks directory not found. Please make sure you're in the project root."
    exit 1
fi

# Set git to use our hooks directory
git config core.hooksPath .githooks
chmod +x .githooks/pre-push

# Make sure the pre-push hook is executable
if [ -x ".githooks/pre-push" ]; then
    echo -e "${GREEN}✓ Git hooks configured successfully${NC}"
else
    echo -e "❌ Failed to make pre-push hook executable"
    exit 1
fi

# 3. Install Maven wrapper if not present
if [ ! -f "mvnw" ]; then
    echo -e "\n${YELLOW}📦 Installing Maven Wrapper...${NC}"
    mvn -N wrapper:wrapper
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Maven Wrapper installed${NC}"
    else
        echo -e "❌ Failed to install Maven Wrapper"
        exit 1
    fi
fi

# 4. Install dependencies
echo -e "\n${YELLOW}📦 Installing Maven dependencies...${NC}"
./mvnw clean install -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Dependencies installed successfully${NC}"
else
    echo -e "❌ Failed to install dependencies"
    exit 1
fi

# 5. Format code
echo -e "\n${YELLOW}✨ Formatting code...${NC}"
./mvnw spotless:apply
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Code formatted successfully${NC}"
else
    echo -e "❌ Failed to format code"
    exit 1
fi

echo -e "\n${GREEN}✅ Setup completed successfully!${NC}"
echo -e "You can now start developing. Happy coding! 🚀\n"

echo -e "${YELLOW}Next steps:${NC}"
echo "1. Configure your environment variables in application-local.properties"
echo "2. Run the application with: ./mvnw spring-boot:run"
echo "\nFor more information, check the README.md file"
