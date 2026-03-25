# 🚀 Your Deployment Guide

Repository: https://github.com/Srikar-segmentation-fault/stockanalysis

## ✅ Step 1: Code Pushed to GitHub - COMPLETE!

Your code is now live on GitHub with:
- ✅ 106 files committed
- ✅ All sensitive files (.env) excluded
- ✅ Complete Docker configuration
- ✅ Comprehensive documentation

**View your repository:** https://github.com/Srikar-segmentation-fault/stockanalysis

---

## 🖥️ Step 2: Deploy to Server

### Option A: Deploy on Your Own Server

#### Prerequisites
- Ubuntu 20.04+ / Debian 11+ server
- Docker and Docker Compose installed
- At least 2GB RAM, 10GB disk space
- SSH access to server

#### Quick Deployment Commands

```bash
# 1. SSH to your server
ssh user@your-server-ip

# 2. Install Docker (if not installed)
sudo apt update
sudo apt install -y docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# 3. Clone your repository
cd /opt
sudo git clone https://github.com/Srikar-segmentation-fault/stockanalysis.git
cd stockanalysis
sudo chown -R $USER:$USER .

# 4. Configure environment
cp .env.docker .env
nano .env

# Update these values in .env:
# DB_PASSWORD=<generate with: openssl rand -base64 32>
# JWT_SECRET=<generate with: openssl rand -base64 64>
# CORS_ALLOWED_ORIGINS=https://your-domain.com
# VITE_API_BASE_URL=https://your-domain.com/api

# 5. Deploy
docker-compose up -d

# 6. Verify
docker-compose ps
curl http://localhost:8080/api/health
```

### Option B: Deploy on Cloud Platform

#### AWS EC2
1. Launch EC2 instance (t2.medium or larger)
2. Configure security group (ports 80, 443, 22)
3. Follow "Option A" commands above

#### DigitalOcean Droplet
1. Create Droplet (2GB RAM minimum)
2. Choose Ubuntu 22.04
3. Follow "Option A" commands above

#### Google Cloud Platform
1. Create Compute Engine instance
2. Configure firewall rules
3. Follow "Option A" commands above

---

## 🔐 Step 3: Generate Secure Credentials

Before deploying, generate secure values:

```bash
# Generate JWT Secret (256 bits)
openssl rand -base64 64

# Generate Database Password
openssl rand -base64 32
```

Update your `.env` file with these values:

```bash
# Database Configuration
DB_PASSWORD=<paste_generated_password>

# JWT Configuration
JWT_SECRET=<paste_generated_secret>

# CORS (update with your domain)
CORS_ALLOWED_ORIGINS=https://your-domain.com

# API URL (update with your domain)
VITE_API_BASE_URL=https://your-domain.com/api
```

---

## ✅ Step 4: Verify Deployment

After running `docker-compose up -d`, verify:

```bash
# 1. Check all services are healthy
docker-compose ps

# Expected output:
# NAME                        STATUS
# stock-analysis-backend      Up (healthy)
# stock-analysis-frontend     Up (healthy)
# stock-analysis-db           Up (healthy)

# 2. Test backend health
curl http://localhost:8080/api/health

# Expected: {"status":"UP","service":"AI Stock Analysis API"}

# 3. Test frontend
curl -I http://localhost/

# Expected: HTTP/1.1 200 OK

# 4. View logs
docker-compose logs -f
```

---

## 🌐 Step 5: Set Up Domain and HTTPS (Production)

### Configure DNS
Point your domain to your server IP:
- A record: `your-domain.com` → `your-server-ip`
- A record: `www.your-domain.com` → `your-server-ip`

### Install SSL Certificate

```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx nginx

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/stockanalysis
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
sudo ln -s /etc/nginx/sites-available/stockanalysis /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test auto-renewal
sudo certbot renew --dry-run
```

---

## 📊 Step 6: Test Your Application

Once deployed, test these features:

1. **User Registration**
   - Navigate to your domain
   - Create a new account
   - Verify email/password validation

2. **User Login**
   - Login with created account
   - Verify JWT token authentication

3. **Stock Search**
   - Search for stocks (e.g., "AAPL", "GOOGL")
   - Verify search results display

4. **Portfolio Operations**
   - Buy a stock
   - Check portfolio summary
   - Sell a stock
   - Verify P&L calculations

---

## 🔧 Management Commands

```bash
# View logs
docker-compose logs -f

# Restart a service
docker-compose restart backend

# Stop all services
docker-compose down

# Update application
git pull origin main
docker-compose down
docker-compose build
docker-compose up -d

# Backup database
docker-compose exec postgres pg_dump -U postgres stock_analysis > backup.sql

# Restore database
docker-compose exec -T postgres psql -U postgres stock_analysis < backup.sql
```

---

## 🐛 Troubleshooting

### Backend won't start
```bash
docker-compose logs backend
docker-compose restart backend
```

### Frontend shows CORS errors
```bash
# Check CORS configuration
docker-compose exec backend env | grep CORS

# Update .env and rebuild
nano .env
docker-compose down
docker-compose build backend
docker-compose up -d
```

### Database connection failed
```bash
docker-compose ps postgres
docker-compose logs postgres
docker-compose restart postgres
docker-compose restart backend
```

### Port already in use
```bash
# Find process using port
sudo lsof -i :80
sudo lsof -i :8080

# Change port in .env
nano .env
# Update FRONTEND_PORT or BACKEND_PORT
docker-compose down
docker-compose up -d
```

---

## 📈 Monitoring

### Set Up Health Check Monitoring

Use services like:
- **UptimeRobot** (free): https://uptimerobot.com
- **Pingdom**: https://www.pingdom.com
- **StatusCake**: https://www.statuscake.com

Monitor these endpoints:
- `https://your-domain.com/api/health`
- `https://your-domain.com/`

### Set Up Automated Backups

```bash
# Create backup script
nano /opt/stockanalysis/backup.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/opt/stockanalysis/backups"
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
cd /opt/stockanalysis
docker-compose exec -T postgres pg_dump -U postgres stock_analysis > "$BACKUP_DIR/backup_$DATE.sql"
gzip "$BACKUP_DIR/backup_$DATE.sql"
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

```bash
# Make executable
chmod +x /opt/stockanalysis/backup.sh

# Schedule daily backups (2 AM)
crontab -e
# Add: 0 2 * * * /opt/stockanalysis/backup.sh
```

---

## 📚 Documentation Reference

Your repository includes comprehensive documentation:

- **READY_TO_DEPLOY.md** - Quick start guide
- **GIT_AND_DEPLOYMENT_GUIDE.md** - Complete deployment guide
- **DEPLOYMENT_COMMANDS.md** - Command reference
- **DOCKER_QUICK_START.md** - Docker quick start
- **DOCKER_DEPLOYMENT.md** - Detailed Docker guide

---

## 🎯 Next Steps

1. **Deploy to staging/production server**
2. **Configure domain and HTTPS**
3. **Set up monitoring**
4. **Configure automated backups**
5. **Test all features end-to-end**
6. **Plan Phase 1 features** (real-time data & charts)

---

## 📞 Quick Reference

**Repository:** https://github.com/Srikar-segmentation-fault/stockanalysis

**Clone Command:**
```bash
git clone https://github.com/Srikar-segmentation-fault/stockanalysis.git
```

**Deploy Command:**
```bash
cd stockanalysis
cp .env.docker .env
nano .env  # Update with secure values
docker-compose up -d
```

**Health Check:**
```bash
curl http://localhost:8080/api/health
```

---

## ✅ Deployment Checklist

- [x] Code pushed to GitHub
- [ ] Server provisioned
- [ ] Docker installed on server
- [ ] Repository cloned on server
- [ ] .env file configured with secure values
- [ ] Services deployed with docker-compose
- [ ] Health checks passing
- [ ] All features tested
- [ ] Domain configured (production)
- [ ] HTTPS enabled (production)
- [ ] Monitoring set up (production)
- [ ] Backups automated (production)

---

**Your application is ready to deploy! 🚀**

Follow the steps above and you'll have your AI Stock Analysis application running in production!
