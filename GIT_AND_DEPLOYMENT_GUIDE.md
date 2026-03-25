# Git Push and Deployment Guide

Complete guide for pushing your code to Git and deploying with Docker.

## Table of Contents
1. [Pre-Push Checklist](#pre-push-checklist)
2. [Git Setup and Push](#git-setup-and-push)
3. [Server Setup](#server-setup)
4. [Deployment](#deployment)
5. [Post-Deployment Verification](#post-deployment-verification)
6. [Troubleshooting](#troubleshooting)

---

## Pre-Push Checklist

### 1. Verify .gitignore

Ensure sensitive files are excluded:

```bash
# Check what will be committed
git status

# Verify .env files are NOT listed
# Verify node_modules/ is NOT listed
# Verify target/ is NOT listed
```

**Critical files that MUST be ignored:**
- ✅ `.env` files (all environments)
- ✅ `frontend/node_modules/`
- ✅ `myapp/target/`
- ✅ Database backups (*.sql except init-db.sql)
- ✅ IDE files (.idea/, .vscode/)

### 2. Clean Up Local Files

```bash
# Remove any accidentally tracked sensitive files
git rm --cached .env
git rm --cached frontend/.env
git rm --cached myapp/.env

# Remove build artifacts
rm -rf frontend/node_modules
rm -rf frontend/dist
rm -rf myapp/target
```

### 3. Verify Example Files Exist

Ensure these template files are present:
- ✅ `.env.docker` (environment template)
- ✅ `frontend/.env.example`
- ✅ `myapp/.env.example`

### 4. Test Docker Build Locally

```bash
# Verify Docker files are correct
docker-compose config

# Test build (optional but recommended)
docker-compose build
```

---

## Git Setup and Push

### Step 1: Initialize Git (if not already done)

```bash
# Check if git is initialized
git status

# If not initialized:
git init
```

### Step 2: Add Remote Repository

**Replace `YOUR_REPO_URL` with your actual repository URL:**

```bash
# Add remote
git remote add origin YOUR_REPO_URL

# Verify remote
git remote -v
```

### Step 3: Stage Files

```bash
# Add all files
git add .

# Verify what's being added
git status

# IMPORTANT: Verify no .env files are listed!
```

### Step 4: Commit Changes

```bash
# Create commit
git commit -m "Initial commit: AI Stock Analysis MVP with Docker deployment"

# Or more detailed:
git commit -m "feat: Complete MVP with Docker deployment

- Implemented stock search and portfolio management
- Added Docker deployment configuration
- Created comprehensive deployment documentation
- Configured health checks and monitoring
- Added security configurations"
```

### Step 5: Push to Remote

```bash
# Push to main branch
git push -u origin main

# Or if using master:
git push -u origin master

# If you need to force push (use with caution):
# git push -u origin main --force
```

### Step 6: Verify Push

```bash
# Check remote repository in browser
# Verify all files are present
# Verify .env files are NOT present
```

---

## Server Setup

### Prerequisites

Your server needs:
- Ubuntu 20.04+ / Debian 11+ / CentOS 8+ (or similar)
- Docker 20.10+
- Docker Compose 2.0+
- At least 2GB RAM
- At least 10GB disk space
- Open ports: 80, 443 (and optionally 8080 for API)

### Step 1: Connect to Server

```bash
# SSH into your server
ssh user@your-server-ip

# Or if using a key:
ssh -i /path/to/key.pem user@your-server-ip
```

### Step 2: Install Docker (if not installed)

**Ubuntu/Debian:**
```bash
# Update package index
sudo apt update

# Install prerequisites
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Add Docker's official GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

**CentOS/RHEL:**
```bash
# Install Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### Step 3: Add User to Docker Group (Optional)

```bash
# Add current user to docker group
sudo usermod -aG docker $USER

# Log out and back in for changes to take effect
exit
# SSH back in
```

### Step 4: Configure Firewall

```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp  # SSH
sudo ufw enable

# CentOS/RHEL (firewalld)
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --reload
```

---

## Deployment

### Step 1: Clone Repository on Server

```bash
# Navigate to deployment directory
cd /opt  # or your preferred location

# Clone repository
sudo git clone YOUR_REPO_URL stock-analysis
cd stock-analysis

# Set permissions
sudo chown -R $USER:$USER /opt/stock-analysis
```

### Step 2: Configure Environment

```bash
# Copy environment template
cp .env.docker .env

# Edit environment file
nano .env  # or vim .env
```

**Update these values in .env:**

```bash
# Database Configuration
DB_NAME=stock_analysis
DB_USERNAME=postgres
DB_PASSWORD=YOUR_SECURE_PASSWORD_HERE  # CHANGE THIS!
DB_PORT=5432

# Backend Configuration
BACKEND_PORT=8080

# JWT Configuration - Generate with: openssl rand -base64 64
JWT_SECRET=YOUR_GENERATED_JWT_SECRET_HERE  # CHANGE THIS!
JWT_EXPIRATION=86400000

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-domain.com,https://www.your-domain.com

# Frontend Configuration
FRONTEND_PORT=80
VITE_API_BASE_URL=https://your-domain.com/api
```

**Generate secure values:**

```bash
# Generate JWT secret
openssl rand -base64 64

# Generate database password
openssl rand -base64 32
```

### Step 3: Build and Start Services

```bash
# Build images
docker-compose build

# Start services in detached mode
docker-compose up -d

# View logs
docker-compose logs -f
```

**Wait for services to be healthy (2-5 minutes):**

```bash
# Check status
docker-compose ps

# All services should show (healthy)
```

### Step 4: Verify Deployment

```bash
# Test backend health
curl http://localhost:8080/api/health

# Expected response:
# {"status":"UP","service":"AI Stock Analysis API"}

# Test frontend
curl http://localhost/

# Should return HTML
```

---

## Post-Deployment Verification

### 1. Health Checks

```bash
# Backend health
curl http://localhost:8080/api/health

# Actuator health
curl http://localhost:8080/actuator/health

# Frontend
curl -I http://localhost/
```

### 2. Check Logs

```bash
# All services
docker-compose logs --tail=50

# Backend only
docker-compose logs --tail=50 backend

# Frontend only
docker-compose logs --tail=50 frontend

# Database only
docker-compose logs --tail=50 postgres
```

### 3. Test Database Connection

```bash
# Connect to database
docker-compose exec postgres psql -U postgres -d stock_analysis

# Run test query
SELECT 1;

# Exit
\q
```

### 4. Resource Usage

```bash
# Check resource usage
docker stats

# Should show:
# - Backend: < 1GB RAM
# - Frontend: < 100MB RAM
# - PostgreSQL: < 500MB RAM
```

### 5. Functional Testing

Test these features:
1. ✅ User registration
2. ✅ User login
3. ✅ Stock search
4. ✅ Stock details
5. ✅ Buy stock
6. ✅ Sell stock
7. ✅ Portfolio summary

---

## Setting Up HTTPS (Production)

### Option 1: Using Nginx Reverse Proxy with Let's Encrypt

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Install Nginx
sudo apt install -y nginx

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/stock-analysis
```

**Nginx configuration:**

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    location / {
        proxy_pass http://localhost:80;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/stock-analysis /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Auto-renewal is configured automatically
```

### Option 2: Using Traefik (Docker-based)

See `DOCKER_DEPLOYMENT.md` for Traefik configuration.

---

## Automated Backups

### Create Backup Script

```bash
# Create backup directory
mkdir -p /opt/stock-analysis/backups

# Create backup script
nano /opt/stock-analysis/backup.sh
```

**Backup script content:**

```bash
#!/bin/bash
set -e

BACKUP_DIR="/opt/stock-analysis/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/backup_$DATE.sql"

# Create backup
cd /opt/stock-analysis
docker-compose exec -T postgres pg_dump -U postgres stock_analysis > "$BACKUP_FILE"

# Compress backup
gzip "$BACKUP_FILE"

# Keep only last 7 days of backups
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: ${BACKUP_FILE}.gz"
```

```bash
# Make executable
chmod +x /opt/stock-analysis/backup.sh

# Test backup
./backup.sh
```

### Schedule Automated Backups

```bash
# Edit crontab
crontab -e

# Add daily backup at 2 AM
0 2 * * * /opt/stock-analysis/backup.sh >> /opt/stock-analysis/backups/backup.log 2>&1
```

---

## Monitoring Setup

### 1. Set Up Log Rotation

```bash
# Create logrotate configuration
sudo nano /etc/logrotate.d/stock-analysis
```

```
/opt/stock-analysis/logs/*.log {
    daily
    rotate 7
    compress
    delaycompress
    missingok
    notifempty
    create 0644 $USER $USER
}
```

### 2. Set Up Health Check Monitoring

Use a service like:
- UptimeRobot (free tier available)
- Pingdom
- StatusCake
- Custom monitoring script

**Monitor these endpoints:**
- `https://your-domain.com/api/health`
- `https://your-domain.com/`

---

## Updating the Application

### Pull Latest Changes

```bash
# Navigate to project directory
cd /opt/stock-analysis

# Pull latest code
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose build
docker-compose up -d

# Verify
docker-compose ps
docker-compose logs -f
```

### Rollback if Needed

```bash
# Stop current deployment
docker-compose down

# Checkout previous version
git log  # Find commit hash
git checkout <previous-commit-hash>

# Rebuild and start
docker-compose build
docker-compose up -d
```

---

## Troubleshooting

### Issue: Git push rejected

```bash
# Pull latest changes first
git pull origin main --rebase

# Then push
git push origin main
```

### Issue: Docker build fails

```bash
# Check Docker is running
docker info

# Clean Docker cache
docker system prune -a

# Rebuild
docker-compose build --no-cache
```

### Issue: Backend won't start

```bash
# Check logs
docker-compose logs backend

# Common fixes:
# 1. Check database is healthy
docker-compose ps postgres

# 2. Verify environment variables
docker-compose exec backend env | grep DB_

# 3. Restart backend
docker-compose restart backend
```

### Issue: Frontend shows CORS errors

```bash
# Check CORS configuration
docker-compose exec backend env | grep CORS

# Update .env file
nano .env
# Update CORS_ALLOWED_ORIGINS

# Rebuild and restart
docker-compose down
docker-compose build backend
docker-compose up -d
```

### Issue: Database connection failed

```bash
# Check database is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Test connection
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"

# Restart database
docker-compose restart postgres
docker-compose restart backend
```

### Issue: Port already in use

```bash
# Find process using port
sudo lsof -i :80
sudo lsof -i :8080

# Kill process or change port in .env
nano .env
# Update FRONTEND_PORT or BACKEND_PORT

# Restart
docker-compose down
docker-compose up -d
```

---

## Complete Deployment Checklist

### Pre-Deployment
- [ ] Code pushed to Git repository
- [ ] .env files NOT in repository
- [ ] Example files (.env.docker, .env.example) in repository
- [ ] Docker files tested locally

### Server Setup
- [ ] Server provisioned
- [ ] Docker installed
- [ ] Docker Compose installed
- [ ] Firewall configured
- [ ] Domain DNS configured (if using domain)

### Deployment
- [ ] Repository cloned on server
- [ ] .env file configured with secure values
- [ ] Services built successfully
- [ ] Services started successfully
- [ ] All services showing (healthy)

### Verification
- [ ] Backend health check passing
- [ ] Frontend accessible
- [ ] Database connection working
- [ ] User registration works
- [ ] User login works
- [ ] Stock search works
- [ ] Portfolio operations work

### Production Hardening
- [ ] HTTPS configured
- [ ] Automated backups set up
- [ ] Monitoring configured
- [ ] Log rotation configured
- [ ] Firewall rules verified
- [ ] Security checklist completed

---

## Quick Reference Commands

```bash
# Git
git status
git add .
git commit -m "message"
git push origin main

# Docker
docker-compose build
docker-compose up -d
docker-compose down
docker-compose ps
docker-compose logs -f
docker-compose restart <service>

# Health Checks
curl http://localhost:8080/api/health
curl http://localhost/

# Backups
./backup.sh

# Updates
git pull origin main
docker-compose down
docker-compose build
docker-compose up -d
```

---

## Support

For issues:
1. Check logs: `docker-compose logs`
2. Verify health: `docker-compose ps`
3. Review troubleshooting section
4. Check [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)

---

**Ready to deploy!** 🚀

Follow the steps in order, and you'll have your application running in production.
