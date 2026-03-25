# Quick Deployment Reference

## Frontend Deployment

### 1. Configure Environment
```bash
cd frontend
cp .env.example .env.production
# Edit .env.production with your production API URL
```

### 2. Build
```bash
npm install
npm run build
```

### 3. Deploy
Upload the `dist/` folder contents to your web server or CDN.

**Nginx Example:**
```bash
sudo cp -r dist/* /var/www/html/
```

## Backend Deployment

### 1. Configure Environment
```bash
cd myapp
cp .env.example .env
# Edit .env with your production database and JWT settings
```

### 2. Build
```bash
mvn clean package -DskipTests
```

### 3. Run
```bash
java -jar target/myapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Environment Variables

### Frontend
- `VITE_API_BASE_URL` - Your backend API URL

### Backend
- `DB_URL` - PostgreSQL connection string
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT tokens (256+ bits)
- `JWT_EXPIRATION` - Token expiration in milliseconds (default: 86400000 = 24h)
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed frontend URLs
- `PORT` - Server port (optional, default: 8080)

## Health Checks

Test your deployment:
```bash
# Custom health endpoint
curl https://your-domain.com/api/health

# Actuator health endpoint
curl https://your-domain.com/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "AI Stock Analysis API"
}
```

## Troubleshooting

### Frontend shows CORS errors
- Check `CORS_ALLOWED_ORIGINS` in backend `.env`
- Ensure it matches your frontend domain exactly (including protocol)

### Backend won't connect to database
- Verify `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in `.env`
- Check database is running: `psql -h <host> -U <user> -d stock_analysis`

### Build fails
- Frontend: Ensure Node.js 18+ is installed
- Backend: Ensure Java 21 and Maven 3.8+ are installed

For detailed instructions, see `DEPLOYMENT.md`.
