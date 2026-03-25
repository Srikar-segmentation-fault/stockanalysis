# Docker Deployment Guide

This guide covers deploying the AI Stock Analysis application using Docker and Docker Compose.

## Prerequisites

- Docker 20.10+ installed
- Docker Compose 2.0+ installed
- At least 2GB of available RAM
- At least 5GB of available disk space

## Quick Start

### 1. Configure Environment Variables

Copy the example environment file and update with your values:

```bash
cp .env.docker .env
```

Edit `.env` and update these critical values:
- `DB_PASSWORD` - Set a strong database password
- `JWT_SECRET` - Set a secure JWT secret (minimum 256 bits)
- `CORS_ALLOWED_ORIGINS` - Set your frontend domain(s)

### 2. Build and Start Services

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

### 3. Verify Deployment

Check that all services are healthy:

```bash
# Backend health check
curl http://localhost:8080/api/health

# Frontend access
curl http://localhost/

# Database connection
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"
```

Expected backend response:
```json
{
  "status": "UP",
  "service": "AI Stock Analysis API"
}
```

## Architecture

The Docker deployment consists of three services:

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                        │
│                                                          │
│  ┌──────────────┐   ┌──────────────┐   ┌────────────┐ │
│  │   Frontend   │   │   Backend    │   │ PostgreSQL │ │
│  │   (Nginx)    │──▶│ (Spring Boot)│──▶│  Database  │ │
│  │   Port 80    │   │  Port 8080   │   │ Port 5432  │ │
│  └──────────────┘   └──────────────┘   └────────────┘ │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Services

1. **postgres** - PostgreSQL 16 database
   - Persistent data storage with Docker volume
   - Automatic initialization with `init-db.sql`
   - Health checks enabled

2. **backend** - Spring Boot API
   - Multi-stage build for optimized image size
   - Runs as non-root user for security
   - Waits for database to be healthy before starting
   - Health checks via `/api/health` endpoint

3. **frontend** - React application served by Nginx
   - Multi-stage build (Node.js build + Nginx serve)
   - Optimized static asset serving
   - Gzip compression enabled
   - SPA routing support

## Docker Commands

### Starting Services

```bash
# Start all services in background
docker-compose up -d

# Start specific service
docker-compose up -d backend

# Start with build (rebuild images)
docker-compose up -d --build

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f backend
```

### Stopping Services

```bash
# Stop all services
docker-compose stop

# Stop specific service
docker-compose stop backend

# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (WARNING: deletes database data)
docker-compose down -v
```

### Managing Services

```bash
# Check service status
docker-compose ps

# Restart service
docker-compose restart backend

# Execute command in container
docker-compose exec backend sh
docker-compose exec postgres psql -U postgres -d stock_analysis

# View resource usage
docker stats
```

### Building Images

```bash
# Build all images
docker-compose build

# Build specific service
docker-compose build backend

# Build without cache
docker-compose build --no-cache

# Pull latest base images
docker-compose pull
```

## Environment Variables

### Database Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_NAME` | `stock_analysis` | PostgreSQL database name |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password (CHANGE THIS!) |
| `DB_PORT` | `5432` | Database port on host |

### Backend Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `BACKEND_PORT` | `8080` | Backend API port on host |
| `JWT_SECRET` | (required) | JWT signing secret (256+ bits) |
| `JWT_EXPIRATION` | `86400000` | JWT expiration (milliseconds) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Allowed CORS origins |

### Frontend Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `FRONTEND_PORT` | `80` | Frontend port on host |
| `VITE_API_BASE_URL` | `http://localhost:8080/api` | Backend API URL |

## Production Deployment

### 1. Update Environment Variables

For production, update `.env` with:

```bash
# Use strong passwords
DB_PASSWORD=your_very_secure_database_password_here

# Use a secure JWT secret (generate with: openssl rand -base64 64)
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_at_least_256_bits

# Set your production domain
CORS_ALLOWED_ORIGINS=https://your-domain.com

# Update API URL for frontend
VITE_API_BASE_URL=https://api.your-domain.com/api
```

### 2. Use Production Ports

```bash
# Frontend on port 80 (HTTP) or 443 (HTTPS)
FRONTEND_PORT=80

# Backend on internal port (use reverse proxy)
BACKEND_PORT=8080
```

### 3. Enable HTTPS

For production, use a reverse proxy (Nginx/Traefik) with SSL certificates:

```yaml
# Add to docker-compose.yml
services:
  nginx-proxy:
    image: nginx:alpine
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./nginx-proxy.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
```

### 4. Database Backups

Set up automated backups:

```bash
# Backup database
docker-compose exec postgres pg_dump -U postgres stock_analysis > backup.sql

# Restore database
docker-compose exec -T postgres psql -U postgres stock_analysis < backup.sql

# Automated backup script
cat > backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="./backups"
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
docker-compose exec -T postgres pg_dump -U postgres stock_analysis > "$BACKUP_DIR/backup_$DATE.sql"
# Keep only last 7 days
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete
EOF

chmod +x backup.sh

# Add to crontab for daily backups at 2 AM
# 0 2 * * * /path/to/backup.sh
```

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker-compose logs backend

# Common issues:
# 1. Database not ready - wait for postgres health check
# 2. Environment variables missing - check .env file
# 3. Port already in use - change BACKEND_PORT in .env

# Restart backend
docker-compose restart backend
```

### Frontend shows API errors

```bash
# Check CORS configuration
docker-compose exec backend env | grep CORS

# Check API URL in frontend
docker-compose exec frontend cat /usr/share/nginx/html/index.html | grep VITE_API_BASE_URL

# Rebuild frontend with correct API URL
docker-compose build --no-cache frontend
docker-compose up -d frontend
```

### Database connection errors

```bash
# Check database is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Test database connection
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"

# Reset database (WARNING: deletes all data)
docker-compose down -v
docker-compose up -d
```

### Out of disk space

```bash
# Check Docker disk usage
docker system df

# Clean up unused images
docker image prune -a

# Clean up unused volumes
docker volume prune

# Clean up everything (WARNING: removes all unused Docker resources)
docker system prune -a --volumes
```

### Container keeps restarting

```bash
# Check container logs
docker-compose logs --tail=100 backend

# Check health status
docker-compose ps

# Inspect container
docker inspect stock-analysis-backend

# Run container interactively for debugging
docker-compose run --rm backend sh
```

## Performance Optimization

### 1. Resource Limits

Add resource limits to `docker-compose.yml`:

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### 2. Database Tuning

Create `postgres.conf` and mount it:

```yaml
services:
  postgres:
    volumes:
      - ./postgres.conf:/etc/postgresql/postgresql.conf
    command: postgres -c config_file=/etc/postgresql/postgresql.conf
```

### 3. Nginx Caching

Update `nginx.conf` with caching directives (already included).

## Monitoring

### Health Checks

All services have health checks configured:

```bash
# Check health status
docker-compose ps

# Manual health checks
curl http://localhost:8080/api/health
curl http://localhost:8080/actuator/health
curl http://localhost/
```

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend

# View last 100 lines
docker-compose logs --tail=100 backend

# Export logs to file
docker-compose logs > logs.txt
```

### Resource Monitoring

```bash
# Real-time resource usage
docker stats

# Container details
docker inspect stock-analysis-backend
```

## Security Best Practices

1. **Change default passwords** - Update `DB_PASSWORD` and `JWT_SECRET`
2. **Use secrets management** - Consider Docker secrets for sensitive data
3. **Run as non-root** - Backend already configured to run as non-root user
4. **Keep images updated** - Regularly update base images
5. **Scan for vulnerabilities** - Use `docker scan` to check images
6. **Limit network exposure** - Only expose necessary ports
7. **Enable HTTPS** - Use SSL/TLS in production
8. **Regular backups** - Automate database backups

## Updating the Application

### Update Backend

```bash
# Pull latest code
git pull

# Rebuild and restart backend
docker-compose build backend
docker-compose up -d backend

# Verify update
docker-compose logs -f backend
curl http://localhost:8080/api/health
```

### Update Frontend

```bash
# Pull latest code
git pull

# Rebuild and restart frontend
docker-compose build frontend
docker-compose up -d frontend

# Verify update
curl http://localhost/
```

### Update Database Schema

```bash
# Hibernate will auto-update schema on startup (dev mode)
# For production, use manual migrations

# Connect to database
docker-compose exec postgres psql -U postgres -d stock_analysis

# Run migration SQL
\i /path/to/migration.sql
```

## Scaling

### Horizontal Scaling

Use Docker Swarm or Kubernetes for multi-instance deployment:

```bash
# Docker Swarm example
docker swarm init
docker stack deploy -c docker-compose.yml stock-analysis

# Scale backend
docker service scale stock-analysis_backend=3
```

### Load Balancing

Add Nginx load balancer:

```yaml
services:
  load-balancer:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./load-balancer.conf:/etc/nginx/nginx.conf
    depends_on:
      - backend
```

## Support

For issues:
1. Check logs: `docker-compose logs`
2. Verify health: `docker-compose ps`
3. Review this troubleshooting guide
4. Check Docker documentation: https://docs.docker.com/

