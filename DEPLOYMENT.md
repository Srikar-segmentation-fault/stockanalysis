# AI Stock Analysis - Deployment Guide

This guide covers deploying the AI Stock Analysis application to production.

## Deployment Options

Choose the deployment method that best fits your needs:

1. **Docker Deployment (Recommended)** - Easiest and most consistent
   - See `DOCKER_QUICK_START.md` for quick setup
   - See `DOCKER_DEPLOYMENT.md` for detailed documentation
   - Best for: Development, testing, and production deployments

2. **Manual Deployment** - Traditional deployment approach
   - See instructions below
   - Best for: Custom infrastructure requirements

## Architecture Overview

The application consists of two main components:
- **Frontend**: React + Vite application (TypeScript)
- **Backend**: Spring Boot REST API (Java 21)

## Prerequisites

- Node.js 18+ and npm
- Java 21
- Maven 3.8+
- PostgreSQL 14+
- Production server with HTTPS support

## Environment Variables

### Backend (.env)

Create a `.env` file in the `myapp/` directory with the following variables:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://your-production-db-host:5432/stock_analysis
DB_USERNAME=your_db_username
DB_PASSWORD=your_secure_db_password

# JWT Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_at_least_256_bits
JWT_EXPIRATION=86400000

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com

# Server Configuration
PORT=8080
```

### Frontend (.env.production)

Create a `.env.production` file in the `frontend/` directory:

```bash
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## Deployment Steps

### 1. Database Setup

```sql
-- Create production database
CREATE DATABASE stock_analysis;

-- Create user with appropriate permissions
CREATE USER stock_app WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE stock_analysis TO stock_app;
```

### 2. Backend Deployment

#### Build the application

```bash
cd myapp
mvn clean package -DskipTests
```

This creates an executable JAR file in `target/myapp-0.0.1-SNAPSHOT.jar`.

#### Run with production profile

```bash
java -jar target/myapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Or set the environment variable:

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar target/myapp-0.0.1-SNAPSHOT.jar
```

#### Using systemd (Linux)

Create a service file `/etc/systemd/system/stock-analysis-api.service`:

```ini
[Unit]
Description=AI Stock Analysis API
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/stock-analysis/myapp
ExecStart=/usr/bin/java -jar /opt/stock-analysis/myapp/target/myapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Enable and start the service:

```bash
sudo systemctl enable stock-analysis-api
sudo systemctl start stock-analysis-api
sudo systemctl status stock-analysis-api
```

### 3. Frontend Deployment

#### Build the application

```bash
cd frontend
npm install
npm run build
```

This creates optimized production files in the `frontend/dist/` directory.

#### Deploy to static hosting

The `dist/` folder can be deployed to:
- **Nginx**: Copy contents to `/var/www/html` or configure a custom location
- **Apache**: Copy to document root
- **CDN/Cloud**: Upload to AWS S3, Netlify, Vercel, or similar services

#### Nginx Configuration Example

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    root /var/www/stock-analysis/frontend/dist;
    index index.html;
    
    # Frontend routes
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API proxy (optional if backend is on same server)
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

## Health Checks

### Backend Health Endpoint

```bash
curl https://your-api-domain.com/api/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "AI Stock Analysis API"
}
```

### Spring Actuator Health Endpoint

```bash
curl https://your-api-domain.com/actuator/health
```

## Security Checklist

- [ ] Use HTTPS for all production traffic
- [ ] Set strong JWT secret (minimum 256 bits)
- [ ] Configure CORS to allow only your frontend domain
- [ ] Use environment variables for all sensitive data
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in production
- [ ] Disable SQL logging in production
- [ ] Configure database connection pooling
- [ ] Set up database backups
- [ ] Enable rate limiting on API endpoints
- [ ] Configure firewall rules to restrict database access

## Monitoring

### Application Logs

Backend logs can be viewed using:

```bash
# If using systemd
sudo journalctl -u stock-analysis-api -f

# If running directly
tail -f logs/application.log
```

### Database Monitoring

Monitor PostgreSQL performance:

```sql
-- Check active connections
SELECT count(*) FROM pg_stat_activity;

-- Check database size
SELECT pg_size_pretty(pg_database_size('stock_analysis'));
```

## Troubleshooting

### Backend won't start

1. Check environment variables are set correctly
2. Verify database is accessible: `psql -h <host> -U <user> -d stock_analysis`
3. Check logs for specific error messages
4. Ensure Java 21 is installed: `java -version`

### Frontend shows API errors

1. Verify `VITE_API_BASE_URL` is set correctly in `.env.production`
2. Check CORS configuration in backend
3. Verify backend is running and accessible
4. Check browser console for specific error messages

### Database connection errors

1. Verify PostgreSQL is running: `sudo systemctl status postgresql`
2. Check database credentials in `.env`
3. Ensure database exists: `psql -l`
4. Check firewall rules allow connection to PostgreSQL port (5432)

### CORS errors

1. Verify `CORS_ALLOWED_ORIGINS` includes your frontend domain
2. Ensure protocol (http/https) matches exactly
3. Check for trailing slashes in URLs
4. Clear browser cache and try again

## Rollback Procedure

### Backend Rollback

1. Stop the current service: `sudo systemctl stop stock-analysis-api`
2. Replace JAR with previous version
3. Start the service: `sudo systemctl start stock-analysis-api`

### Frontend Rollback

1. Replace `dist/` contents with previous build
2. Clear CDN cache if applicable
3. Verify application loads correctly

## Performance Optimization

### Backend

- Enable database connection pooling in `application-prod.properties`:
  ```properties
  spring.datasource.hikari.maximum-pool-size=10
  spring.datasource.hikari.minimum-idle=5
  ```
- Enable response compression
- Configure caching for frequently accessed data

### Frontend

- Assets are automatically optimized during build
- Code splitting is configured in `vite.config.ts`
- Enable gzip/brotli compression in web server
- Configure CDN for static assets

## Scaling Considerations

### Horizontal Scaling

- Backend: Deploy multiple instances behind a load balancer
- Database: Use read replicas for read-heavy operations
- Frontend: Serve from CDN for global distribution

### Vertical Scaling

- Increase JVM heap size: `-Xmx2g -Xms1g`
- Increase database resources (CPU, RAM)
- Optimize database queries and add indexes

## Support

For issues or questions:
- Check application logs first
- Review this troubleshooting guide
- Contact the development team with specific error messages and logs
