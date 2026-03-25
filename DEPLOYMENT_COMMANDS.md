# Quick Deployment Commands Reference

Quick reference for Git push and Docker deployment commands.

## 📋 Pre-Push Commands

```bash
# Check what will be committed
git status

# Verify .env files are ignored
git check-ignore .env frontend/.env myapp/.env

# Clean build artifacts
rm -rf frontend/node_modules frontend/dist myapp/target

# Test Docker configuration
docker-compose config
```

## 🔄 Git Push Commands

```bash
# Add all files
git add .

# Commit
git commit -m "feat: Complete MVP with Docker deployment"

# Add remote (first time only)
git remote add origin YOUR_REPO_URL

# Push to main
git push -u origin main

# Verify remote
git remote -v
```

## 🖥️ Server Setup Commands

```bash
# SSH to server
ssh user@your-server-ip

# Install Docker (Ubuntu/Debian)
sudo apt update
sudo apt install -y docker.io docker-compose

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -aG docker $USER

# Configure firewall
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

## 🚀 Deployment Commands

```bash
# Clone repository
cd /opt
sudo git clone YOUR_REPO_URL stock-analysis
cd stock-analysis
sudo chown -R $USER:$USER .

# Configure environment
cp .env.docker .env
nano .env  # Edit with your values

# Generate secrets
openssl rand -base64 64  # JWT_SECRET
openssl rand -base64 32  # DB_PASSWORD

# Build and start
docker-compose build
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## ✅ Verification Commands

```bash
# Health checks
curl http://localhost:8080/api/health
curl http://localhost:8080/actuator/health
curl -I http://localhost/

# Check logs
docker-compose logs --tail=50 backend
docker-compose logs --tail=50 frontend
docker-compose logs --tail=50 postgres

# Test database
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"

# Resource usage
docker stats
```

## 🔧 Management Commands

```bash
# Stop services
docker-compose stop

# Start services
docker-compose start

# Restart service
docker-compose restart backend

# Rebuild and restart
docker-compose down
docker-compose build
docker-compose up -d

# View service details
docker-compose ps
docker inspect stock-analysis-backend

# Execute command in container
docker-compose exec backend sh
docker-compose exec postgres psql -U postgres -d stock_analysis
```

## 📊 Monitoring Commands

```bash
# Real-time logs
docker-compose logs -f

# Last 100 lines
docker-compose logs --tail=100

# Logs for specific service
docker-compose logs -f backend

# Resource usage
docker stats

# Disk usage
docker system df

# Network info
docker network ls
docker network inspect stock-analysis_stock-analysis-network
```

## 💾 Backup Commands

```bash
# Manual backup
docker-compose exec postgres pg_dump -U postgres stock_analysis > backup_$(date +%Y%m%d).sql

# Compress backup
gzip backup_$(date +%Y%m%d).sql

# Restore backup
gunzip backup_20260325.sql.gz
docker-compose exec -T postgres psql -U postgres stock_analysis < backup_20260325.sql

# List backups
ls -lh backups/
```

## 🔄 Update Commands

```bash
# Pull latest code
cd /opt/stock-analysis
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose build
docker-compose up -d

# Verify
docker-compose ps
curl http://localhost:8080/api/health
```

## 🔙 Rollback Commands

```bash
# Stop current deployment
docker-compose down

# Checkout previous version
git log --oneline  # Find commit hash
git checkout <commit-hash>

# Rebuild and start
docker-compose build
docker-compose up -d

# Or restore from backup
docker-compose exec -T postgres psql -U postgres stock_analysis < backup.sql
```

## 🧹 Cleanup Commands

```bash
# Stop and remove containers
docker-compose down

# Remove containers and volumes (WARNING: deletes data)
docker-compose down -v

# Clean Docker system
docker system prune -a

# Remove unused volumes
docker volume prune

# Remove unused images
docker image prune -a
```

## 🔐 Security Commands

```bash
# Generate JWT secret
openssl rand -base64 64

# Generate database password
openssl rand -base64 32

# Check environment variables
docker-compose exec backend env | grep -E "DB_|JWT_|CORS_"

# Scan images for vulnerabilities
docker scan stock-analysis-backend
docker scan stock-analysis-frontend
```

## 🌐 HTTPS Setup Commands (with Certbot)

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Install Nginx
sudo apt install -y nginx

# Obtain certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test auto-renewal
sudo certbot renew --dry-run

# Renew certificates
sudo certbot renew
```

## 📈 Performance Commands

```bash
# Check container stats
docker stats --no-stream

# Check disk usage
df -h

# Check memory usage
free -h

# Check CPU usage
top

# Check network connections
netstat -tulpn | grep -E "80|8080|5432"
```

## 🐛 Troubleshooting Commands

```bash
# Check Docker daemon
docker info

# Check service health
docker-compose ps

# Inspect container
docker inspect stock-analysis-backend

# View container logs
docker logs stock-analysis-backend

# Execute shell in container
docker-compose exec backend sh

# Check port usage
sudo lsof -i :80
sudo lsof -i :8080
sudo lsof -i :5432

# Test network connectivity
docker-compose exec backend ping postgres
docker-compose exec backend wget -O- http://localhost:8080/api/health

# Restart Docker daemon
sudo systemctl restart docker
```

## 📝 Logging Commands

```bash
# Follow logs
docker-compose logs -f

# Logs since timestamp
docker-compose logs --since 2024-03-25T10:00:00

# Logs until timestamp
docker-compose logs --until 2024-03-25T12:00:00

# Export logs to file
docker-compose logs > logs_$(date +%Y%m%d).txt

# Search logs
docker-compose logs | grep ERROR
docker-compose logs backend | grep "Started DemoApplication"
```

## 🔍 Database Commands

```bash
# Connect to database
docker-compose exec postgres psql -U postgres -d stock_analysis

# List databases
docker-compose exec postgres psql -U postgres -c "\l"

# List tables
docker-compose exec postgres psql -U postgres -d stock_analysis -c "\dt"

# Check database size
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT pg_size_pretty(pg_database_size('stock_analysis'));"

# Check active connections
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT count(*) FROM pg_stat_activity;"

# Vacuum database
docker-compose exec postgres psql -U postgres -d stock_analysis -c "VACUUM ANALYZE;"
```

## 🎯 One-Line Deployment

```bash
# Complete deployment in one command (after git clone and .env setup)
docker-compose build && docker-compose up -d && docker-compose ps && curl http://localhost:8080/api/health
```

## 📱 Quick Health Check

```bash
# Check all services
docker-compose ps && curl -s http://localhost:8080/api/health && curl -sI http://localhost/ | head -1
```

## 🔄 Quick Restart

```bash
# Restart all services
docker-compose restart && docker-compose ps
```

## 📦 Complete Rebuild

```bash
# Complete rebuild from scratch
docker-compose down -v && docker-compose build --no-cache && docker-compose up -d && docker-compose logs -f
```

---

## Environment Variables Template

```bash
# Copy this to .env and update values

# Database
DB_NAME=stock_analysis
DB_USERNAME=postgres
DB_PASSWORD=CHANGE_THIS_SECURE_PASSWORD
DB_PORT=5432

# Backend
BACKEND_PORT=8080
JWT_SECRET=CHANGE_THIS_GENERATED_SECRET
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=https://your-domain.com

# Frontend
FRONTEND_PORT=80
VITE_API_BASE_URL=https://your-domain.com/api
```

---

## Quick Troubleshooting

| Issue | Command |
|-------|---------|
| Backend won't start | `docker-compose logs backend` |
| Frontend shows errors | `docker-compose logs frontend` |
| Database connection failed | `docker-compose exec postgres psql -U postgres -d stock_analysis` |
| Port already in use | `sudo lsof -i :80` or `sudo lsof -i :8080` |
| Out of disk space | `docker system prune -a` |
| Container keeps restarting | `docker logs stock-analysis-backend` |
| Can't connect to service | `docker-compose ps` |
| Need to reset everything | `docker-compose down -v && docker-compose up -d` |

---

## Useful Aliases (Optional)

Add to `~/.bashrc` or `~/.zshrc`:

```bash
# Docker Compose aliases
alias dc='docker-compose'
alias dcu='docker-compose up -d'
alias dcd='docker-compose down'
alias dcl='docker-compose logs -f'
alias dcp='docker-compose ps'
alias dcr='docker-compose restart'

# Stock Analysis specific
alias sa-logs='cd /opt/stock-analysis && docker-compose logs -f'
alias sa-status='cd /opt/stock-analysis && docker-compose ps'
alias sa-health='curl http://localhost:8080/api/health'
alias sa-restart='cd /opt/stock-analysis && docker-compose restart'
```

Then reload: `source ~/.bashrc`

---

**Quick Start:**
1. `git push origin main`
2. SSH to server
3. `git clone YOUR_REPO_URL`
4. `cp .env.docker .env` and edit
5. `docker-compose up -d`
6. `curl http://localhost:8080/api/health`

Done! 🚀
