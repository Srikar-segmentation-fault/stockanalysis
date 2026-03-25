# AI Stock Analysis - Deployment Verification Script (PowerShell)
# This script verifies that the application is ready for deployment

$ErrorActionPreference = "Stop"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "AI Stock Analysis - Deployment Verification" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

function Print-Success {
    param($Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Print-Error {
    param($Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

function Print-Warning {
    param($Message)
    Write-Host "⚠ $Message" -ForegroundColor Yellow
}

# Check if Docker is installed
Write-Host "1. Checking Docker installation..."
try {
    $dockerVersion = docker --version
    Print-Success "Docker is installed: $dockerVersion"
} catch {
    Print-Error "Docker is not installed. Please install Docker Desktop first."
    exit 1
}

# Check if Docker Compose is installed
Write-Host ""
Write-Host "2. Checking Docker Compose installation..."
try {
    $composeVersion = docker-compose --version
    Print-Success "Docker Compose is installed: $composeVersion"
} catch {
    Print-Error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
}

# Check if .env file exists
Write-Host ""
Write-Host "3. Checking environment configuration..."
if (Test-Path ".env") {
    Print-Success ".env file exists"
    
    $envContent = Get-Content ".env" -Raw
    
    if ($envContent -match "JWT_SECRET=" -and $envContent -notmatch "JWT_SECRET=your_very_long") {
        Print-Success "JWT_SECRET is configured"
    } else {
        Print-Warning "JWT_SECRET needs to be updated in .env file"
    }
    
    if ($envContent -match "DB_PASSWORD=" -and $envContent -notmatch "DB_PASSWORD=change_this" -and $envContent -notmatch "DB_PASSWORD=postgres") {
        Print-Success "DB_PASSWORD is configured"
    } else {
        Print-Warning "DB_PASSWORD should be changed from default in .env file"
    }
} else {
    Print-Warning ".env file not found. Creating from .env.docker..."
    Copy-Item ".env.docker" ".env"
    Print-Warning "Please update .env file with your configuration"
}

# Check if Docker files exist
Write-Host ""
Write-Host "4. Checking Docker configuration files..."
$dockerFiles = @("docker-compose.yml", "Dockerfile.backend", "Dockerfile.frontend", "nginx.conf", "init-db.sql")
foreach ($file in $dockerFiles) {
    if (Test-Path $file) {
        Print-Success "$file exists"
    } else {
        Print-Error "$file is missing"
        exit 1
    }
}

# Check if backend source exists
Write-Host ""
Write-Host "5. Checking backend source code..."
if (Test-Path "myapp/src") {
    Print-Success "Backend source code exists"
} else {
    Print-Error "Backend source code not found in myapp/src"
    exit 1
}

# Check if frontend source exists
Write-Host ""
Write-Host "6. Checking frontend source code..."
if (Test-Path "frontend/src") {
    Print-Success "Frontend source code exists"
} else {
    Print-Error "Frontend source code not found in frontend/src"
    exit 1
}

# Check if pom.xml exists
Write-Host ""
Write-Host "7. Checking backend build configuration..."
if (Test-Path "myapp/pom.xml") {
    Print-Success "pom.xml exists"
} else {
    Print-Error "pom.xml not found"
    exit 1
}

# Check if package.json exists
Write-Host ""
Write-Host "8. Checking frontend build configuration..."
if (Test-Path "frontend/package.json") {
    Print-Success "package.json exists"
} else {
    Print-Error "package.json not found"
    exit 1
}

# Check Docker daemon
Write-Host ""
Write-Host "9. Checking Docker daemon..."
try {
    docker info | Out-Null
    Print-Success "Docker daemon is running"
} catch {
    Print-Error "Docker daemon is not running. Please start Docker Desktop."
    exit 1
}

# Check disk space
Write-Host ""
Write-Host "10. Checking disk space..."
$drive = (Get-Location).Drive
$freeSpace = [math]::Round((Get-PSDrive $drive.Name).Free / 1GB, 2)
if ($freeSpace -gt 5) {
    Print-Success "Sufficient disk space available (${freeSpace}GB)"
} else {
    Print-Warning "Low disk space (${freeSpace}GB). At least 5GB recommended."
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Verification Complete!" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Update .env file with your configuration (if not done already)"
Write-Host "2. Run: docker-compose up -d"
Write-Host "3. Check logs: docker-compose logs -f"
Write-Host "4. Verify health: curl http://localhost:8080/api/health"
Write-Host ""
