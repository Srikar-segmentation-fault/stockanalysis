# Deployment Steps - Complete Checklist

Follow these steps to deploy the AI Stock Analysis application using Docker.

## Pre-Deployment Checklist

### 1. Prerequisites ✓
- [ ] Docker Desktop installed and running
- [ ] At least 2GB RAM available
- [ ] At least 5GB disk space available
- [ ] Git repository cloned

### 2. Verify Installation ✓
Run the verification script:

**Windows:**
```powershell
.\verify-deployment.ps1
```

**Linux/Mac:**
```bash
chmod +x verify-deployment.sh
./verify-deployment.sh
```

## Deployment Steps

### Step 1: Environment Configuration

1. Copy the environment template:
```bash
cp .env.docker .env
```

2. Generate a secure JWT secret:
```bash
# Linux/Mac
openssl rand -base64 64

# Windows (PowerShell)
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

3. Edit `.env` file with your values:
```bash
# Required changes:
DB_PASSWORD=<your_secure_password>
JWT_SECRET=<generated_secret_from_step_2>

# For production:
CORS_ALLOWED_ORIGINS=https://your-domain.com
VITE_API_BASE_URL=https://api.your-domain.com/api
```

**Checklist:**
- [ ] `.env` file created
- [ ] `DB_PASSWORD` changed from default
- [ ] `JWT_SECRET` set to secure value (256+ bits)
- [ ] `CORS_ALLOWED_ORIGINS` configured for your domain
- [ ] `VITE_API_BASE_URL` configured for your API

### Step 2: Build and Start Services

1. Build and start all services:
```bash
docker-compose up -d
```

This will:
- Build the backend Docker image (~5 minutes)
- Build the frontend Docker image (~3 minutes)
- Pull PostgreSQL image
- Start all services
- Initialize the database

2. Wait for services to be healthy:
```bash
docker-compose ps
```

All services should show `(healthy)` status.

**Checklist:**
- [ ] Backend container running and healthy
- [ ] Frontend container running and healthy
- [ ] PostgreSQL container running and healthy
- [ ] No error messages in `docker-compose ps`

### Step 3: Verify Deployment

1. Check backend health:
```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "AI Stock Analysis API"
}
```

2. Check actuator health:
```bash
curl http://localhost:8080/actuator/health
```

3. Test frontend:
- Open browser to `http://localhost`
- Verify page loads without errors
- Check browser console for errors

4. Test database connection:
```bash
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"
```

**Checklist:**
- [ ] Backend health endpoint returns `UP`
- [ ] Actuator health endpoint accessible
- [ ] Frontend loads in browser
- [ ] No console errors in browser
- [ ] Database connection successful

### Step 4: Functional Testing

1. Test user registration:
- Navigate to signup page
- Create a test account
- Verify successful registration

2. Test user login:
- Login with test account
- Verify JWT token received
- Verify redirect to dashboard

3. Test stock search:
- Search for a stock (e.g., "AAPL")
- Verify search results appear
- Click on a stock to view details

4. Test portfolio operations:
- Buy a stock
- Verify transaction appears in portfolio
- Check portfolio summary updates
- Sell a stock
- Verify portfolio updates correctly

**Checklist:**
- [ ] User registration works
- [ ] User login works
- [ ] Stock search works
- [ ] Stock details display correctly
- [ ] Buy transaction works
- [ ] Sell transaction works
- [ ] Portfolio summary calculates correctly
- [ ] P&L calculations are accurate

### Step 5: Log Verification

1. Check backend logs:
```bash
docker-compose logs backend | tail -50
```

Look for:
- No ERROR messages
- Successful database connection
- Application started successfully

2. Check frontend logs:
```bash
docker-compose logs frontend | tail -20
```

3. Check database logs:
```bash
docker-compose logs postgres | tail -20
```

**Checklist:**
- [ ] No ERROR messages in backend logs
- [ ] No ERROR messages in frontend logs
- [ ] No ERROR messages in database logs
- [ ] All services started successfully

### Step 6: Performance Check

1. Check resource usage:
```bash
docker stats
```

Verify:
- Backend: < 1GB RAM
- Frontend: < 100MB RAM
- PostgreSQL: < 500MB RAM

2. Test response times:
```bash
# Backend health (should be < 100ms)
time curl http://localhost:8080/api/health

# Frontend (should be < 500ms)
time curl http://localhost/
```

**Checklist:**
- [ ] Resource usage within acceptable limits
- [ ] Response times acceptable
- [ ] No performance warnings

## Post-Deployment Steps

### Step 7: Security Hardening (Production Only)

1. Enable HTTPS:
- Set up SSL certificates (Let's Encrypt)
- Configure reverse proxy (Nginx/Traefik)
- Update CORS and API URLs

2. Configure firewall:
```bash
# Allow only necessary ports
# 80 (HTTP), 443 (HTTPS)
# Block direct access to 8080, 5432
```

3. Set up database backups:
```bash
# Create backup script
cat > backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="./backups"
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
docker-compose exec -T postgres pg_dump -U postgres stock_analysis > "$BACKUP_DIR/backup_$DATE.sql"
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete
EOF

chmod +x backup.sh

# Add to crontab for daily backups
# 0 2 * * * /path/to/backup.sh
```

**Checklist:**
- [ ] HTTPS enabled
- [ ] Firewall configured
- [ ] Database backups automated
- [ ] Monitoring set up
- [ ] Log rotation configured

### Step 8: Monitoring Setup

1. Set up health check monitoring:
```bash
# Add to monitoring tool (e.g., UptimeRobot, Pingdom)
# Monitor: http://your-domain.com/api/health
```

2. Set up log aggregation:
```bash
# Configure log shipping to centralized logging
# (e.g., ELK Stack, Splunk, CloudWatch)
```

3. Set up alerts:
- Service down alerts
- High resource usage alerts
- Error rate alerts

**Checklist:**
- [ ] Health check monitoring configured
- [ ] Log aggregation set up
- [ ] Alerts configured
- [ ] Dashboard created

### Step 9: Documentation

1. Document your deployment:
- Server details
- Domain names
- Database credentials (securely)
- Backup procedures
- Rollback procedures

2. Create runbook:
- Common issues and solutions
- Restart procedures
- Backup/restore procedures
- Scaling procedures

**Checklist:**
- [ ] Deployment documented
- [ ] Runbook created
- [ ] Team trained
- [ ] Emergency contacts listed

### Step 10: Final Verification

1. Run end-to-end tests:
- Complete user workflow
- All features working
- No errors in logs

2. Load testing (optional):
```bash
# Use tools like Apache Bench, JMeter, or k6
ab -n 1000 -c 10 http://localhost:8080/api/health
```

3. Security scan (optional):
```bash
# Scan Docker images for vulnerabilities
docker scan stock-analysis-backend
docker scan stock-analysis-frontend
```

**Checklist:**
- [ ] End-to-end tests passing
- [ ] Load testing completed (if applicable)
- [ ] Security scan completed (if applicable)
- [ ] All stakeholders notified
- [ ] Deployment documented

## Rollback Procedure

If issues occur:

1. Stop current deployment:
```bash
docker-compose down
```

2. Restore previous version:
```bash
git checkout <previous-commit>
docker-compose up -d --build
```

3. Restore database (if needed):
```bash
docker-compose exec -T postgres psql -U postgres stock_analysis < backup.sql
```

## Common Issues and Solutions

### Issue: Backend won't start
**Solution:**
```bash
docker-compose logs backend
docker-compose restart backend
```

### Issue: Frontend shows CORS errors
**Solution:**
1. Check `CORS_ALLOWED_ORIGINS` in `.env`
2. Rebuild frontend: `docker-compose build frontend`
3. Restart: `docker-compose up -d frontend`

### Issue: Database connection failed
**Solution:**
```bash
docker-compose ps postgres
docker-compose restart postgres
docker-compose restart backend
```

### Issue: Out of disk space
**Solution:**
```bash
docker system prune -a
docker volume prune
```

## Success Criteria

Deployment is successful when:
- ✅ All services are running and healthy
- ✅ Health checks return positive status
- ✅ Frontend loads without errors
- ✅ All core features work end-to-end
- ✅ No errors in logs
- ✅ Performance is acceptable
- ✅ Security measures in place (production)
- ✅ Monitoring configured (production)
- ✅ Backups automated (production)

## Next Steps After Deployment

1. Monitor application for 24-48 hours
2. Gather user feedback
3. Plan Phase 1 features (real-time data & charts)
4. Set up CI/CD pipeline
5. Implement additional monitoring

## Support

For issues during deployment:
1. Check logs: `docker-compose logs`
2. Verify health: `docker-compose ps`
3. Review [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)
4. Check [DOCKER_QUICK_START.md](DOCKER_QUICK_START.md)

---

**Deployment Status:** Ready ✅

**Estimated Time:** 
- Initial setup: 15-30 minutes
- Testing: 15-30 minutes
- Production hardening: 1-2 hours
- Total: 1.5-3 hours
