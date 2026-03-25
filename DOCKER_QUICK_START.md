# Docker Quick Start Guide

Get the AI Stock Analysis application running in 5 minutes using Docker.

## Prerequisites

- Docker Desktop installed and running
- At least 2GB RAM available
- At least 5GB disk space

## Quick Start

### 1. Verify Prerequisites

Run the verification script:

**Windows (PowerShell):**
```powershell
.\verify-deployment.ps1
```

**Linux/Mac:**
```bash
chmod +x verify-deployment.sh
./verify-deployment.sh
```

### 2. Configure Environment

Copy the example environment file:

```bash
cp .env.docker .env
```

**IMPORTANT:** Edit `.env` and update these values:

```bash
# Generate a secure JWT secret (run this command):
# openssl rand -base64 64

JWT_SECRET=your_generated_secure_jwt_secret_here

# Set a strong database password
DB_PASSWORD=your_secure_database_password_here

# For production, set your domain
CORS_ALLOWED_ORIGINS=https://your-domain.com
VITE_API_BASE_URL=https://api.your-domain.com/api
```

### 3. Start the Application

```bash
docker-compose up -d
```

This will:
- Build the backend (Spring Boot) image
- Build the frontend (React) image
- Pull PostgreSQL image
- Start all services
- Create the database automatically

### 4. Verify Deployment

Check that all services are running:

```bash
docker-compose ps
```

You should see 3 services running:
- `stock-analysis-backend` (healthy)
- `stock-analysis-frontend` (healthy)
- `stock-analysis-db` (healthy)

### 5. Test the Application

**Backend Health Check:**
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

**Frontend:**
Open your browser and navigate to:
```
http://localhost
```

### 6. View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

## Common Commands

### Stop the Application

```bash
docker-compose stop
```

### Start the Application

```bash
docker-compose start
```

### Restart a Service

```bash
docker-compose restart backend
```

### Rebuild and Restart

```bash
docker-compose up -d --build
```

### Stop and Remove Everything

```bash
# Keep database data
docker-compose down

# Remove database data too (WARNING: deletes all data)
docker-compose down -v
```

### Access Database

```bash
docker-compose exec postgres psql -U postgres -d stock_analysis
```

### View Resource Usage

```bash
docker stats
```

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker-compose logs backend

# Common fix: restart the service
docker-compose restart backend
```

### Frontend shows API errors

Check CORS configuration:
```bash
docker-compose exec backend env | grep CORS
```

Rebuild frontend with correct API URL:
```bash
docker-compose build --no-cache frontend
docker-compose up -d frontend
```

### Database connection errors

```bash
# Check database is running
docker-compose ps postgres

# Test connection
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"

# Reset database (WARNING: deletes all data)
docker-compose down -v
docker-compose up -d
```

### Port already in use

If ports 80, 8080, or 5432 are already in use, edit `.env`:

```bash
FRONTEND_PORT=3000
BACKEND_PORT=8081
DB_PORT=5433
```

Then restart:
```bash
docker-compose down
docker-compose up -d
```

## Production Deployment

For production deployment:

1. Update `.env` with production values
2. Use strong passwords and secrets
3. Set up HTTPS with a reverse proxy (Nginx/Traefik)
4. Configure automated backups
5. Set up monitoring and logging

See `DOCKER_DEPLOYMENT.md` for detailed production deployment instructions.

## Next Steps

- Read `DOCKER_DEPLOYMENT.md` for detailed documentation
- Set up automated backups
- Configure monitoring
- Enable HTTPS for production

## Support

For issues:
1. Check logs: `docker-compose logs`
2. Verify health: `docker-compose ps`
3. Review troubleshooting section above
4. Check `DOCKER_DEPLOYMENT.md` for detailed guides
