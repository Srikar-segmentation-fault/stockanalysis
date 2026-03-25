#!/bin/bash

# AI Stock Analysis - Deployment Verification Script
# This script verifies that the application is ready for deployment

set -e

echo "========================================="
echo "AI Stock Analysis - Deployment Verification"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print success
print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Function to print error
print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Function to print warning
print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Check if Docker is installed
echo "1. Checking Docker installation..."
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_success "Docker is installed: $DOCKER_VERSION"
else
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
echo ""
echo "2. Checking Docker Compose installation..."
if command -v docker-compose &> /dev/null; then
    COMPOSE_VERSION=$(docker-compose --version)
    print_success "Docker Compose is installed: $COMPOSE_VERSION"
else
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if .env file exists
echo ""
echo "3. Checking environment configuration..."
if [ -f ".env" ]; then
    print_success ".env file exists"
    
    # Check for required variables
    if grep -q "JWT_SECRET=" .env && ! grep -q "JWT_SECRET=your_very_long" .env; then
        print_success "JWT_SECRET is configured"
    else
        print_warning "JWT_SECRET needs to be updated in .env file"
    fi
    
    if grep -q "DB_PASSWORD=" .env && ! grep -q "DB_PASSWORD=change_this" .env && ! grep -q "DB_PASSWORD=postgres" .env; then
        print_success "DB_PASSWORD is configured"
    else
        print_warning "DB_PASSWORD should be changed from default in .env file"
    fi
else
    print_warning ".env file not found. Creating from .env.docker..."
    cp .env.docker .env
    print_warning "Please update .env file with your configuration"
fi

# Check if Docker files exist
echo ""
echo "4. Checking Docker configuration files..."
DOCKER_FILES=("docker-compose.yml" "Dockerfile.backend" "Dockerfile.frontend" "nginx.conf" "init-db.sql")
for file in "${DOCKER_FILES[@]}"; do
    if [ -f "$file" ]; then
        print_success "$file exists"
    else
        print_error "$file is missing"
        exit 1
    fi
done

# Check if backend source exists
echo ""
echo "5. Checking backend source code..."
if [ -d "myapp/src" ]; then
    print_success "Backend source code exists"
else
    print_error "Backend source code not found in myapp/src"
    exit 1
fi

# Check if frontend source exists
echo ""
echo "6. Checking frontend source code..."
if [ -d "frontend/src" ]; then
    print_success "Frontend source code exists"
else
    print_error "Frontend source code not found in frontend/src"
    exit 1
fi

# Check if pom.xml exists
echo ""
echo "7. Checking backend build configuration..."
if [ -f "myapp/pom.xml" ]; then
    print_success "pom.xml exists"
else
    print_error "pom.xml not found"
    exit 1
fi

# Check if package.json exists
echo ""
echo "8. Checking frontend build configuration..."
if [ -f "frontend/package.json" ]; then
    print_success "package.json exists"
else
    print_error "package.json not found"
    exit 1
fi

# Check Docker daemon
echo ""
echo "9. Checking Docker daemon..."
if docker info &> /dev/null; then
    print_success "Docker daemon is running"
else
    print_error "Docker daemon is not running. Please start Docker."
    exit 1
fi

# Check disk space
echo ""
echo "10. Checking disk space..."
AVAILABLE_SPACE=$(df -BG . | tail -1 | awk '{print $4}' | sed 's/G//')
if [ "$AVAILABLE_SPACE" -gt 5 ]; then
    print_success "Sufficient disk space available (${AVAILABLE_SPACE}GB)"
else
    print_warning "Low disk space (${AVAILABLE_SPACE}GB). At least 5GB recommended."
fi

echo ""
echo "========================================="
echo "Verification Complete!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Update .env file with your configuration (if not done already)"
echo "2. Run: docker-compose up -d"
echo "3. Check logs: docker-compose logs -f"
echo "4. Verify health: curl http://localhost:8080/api/health"
echo ""
