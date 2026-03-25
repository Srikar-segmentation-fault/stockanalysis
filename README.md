# AI-Powered Stock Analysis Engine

A comprehensive stock portfolio management and analysis system with real-time data, machine learning predictions, and advanced analytics.

## 🚀 Quick Start with Docker

Get started in 5 minutes:

```bash
# 1. Verify prerequisites
.\verify-deployment.ps1  # Windows
./verify-deployment.sh   # Linux/Mac

# 2. Configure environment
cp .env.docker .env
# Edit .env with your secure values

# 3. Start the application
docker-compose up -d

# 4. Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080/api
# Health Check: http://localhost:8080/api/health
```

See [DOCKER_QUICK_START.md](DOCKER_QUICK_START.md) for detailed instructions.

## 📋 Features

### MVP (Currently Implemented)
- ✅ Stock search and details
- ✅ Portfolio management
- ✅ Buy/sell transactions
- ✅ Portfolio dashboard with P&L calculations
- ✅ Responsive UI with Material-UI
- ✅ User authentication with JWT
- ✅ Real-time portfolio updates
- ✅ Error handling and validation
- ✅ Health check endpoints

### Coming Soon (Post-MVP)
- 📈 Real-time stock data integration
- 📊 Interactive price charts
- 🤖 ML-powered price predictions
- 📉 Technical analysis indicators (RSI, MACD, Moving Averages)
- 🎯 Portfolio optimization recommendations
- ⚠️ Risk assessment and alerts
- 💹 Market sentiment analysis

## 🏗️ Architecture

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

### Technology Stack

**Frontend:**
- React 19 with TypeScript
- Material-UI (MUI) for components
- Zustand for state management
- React Router for navigation
- Vite for build tooling

**Backend:**
- Spring Boot 3.2
- Java 21
- PostgreSQL 16
- Spring Security with JWT
- Spring Data JPA
- Maven for build management

**Deployment:**
- Docker & Docker Compose
- Nginx for frontend serving
- Multi-stage builds for optimization

## 📚 Documentation

- [DOCKER_QUICK_START.md](DOCKER_QUICK_START.md) - Get started in 5 minutes
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Comprehensive Docker guide
- [DEPLOYMENT.md](DEPLOYMENT.md) - Manual deployment guide
- [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) - Pre-deployment checklist
- [MVP_DEPLOYMENT_SUMMARY.md](MVP_DEPLOYMENT_SUMMARY.md) - Deployment status
- [frontend/README.md](frontend/README.md) - Frontend documentation
- [frontend/SETUP_SUMMARY.md](frontend/SETUP_SUMMARY.md) - Frontend setup guide

## 🔧 Development

### Prerequisites

- Docker Desktop (for Docker deployment)
- OR:
  - Node.js 18+
  - Java 21
  - Maven 3.8+
  - PostgreSQL 14+

### Local Development (Without Docker)

**Backend:**
```bash
cd myapp
cp .env.example .env
# Edit .env with your database credentials
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### Running Tests

**Backend:**
```bash
cd myapp
mvn test
```

**Frontend:**
```bash
cd frontend
npm test
```

## 🔐 Security

- JWT-based authentication
- Password hashing with BCrypt
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection

See [DEPLOYMENT.md](DEPLOYMENT.md) for security checklist.

## 📊 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user

### Stocks
- `GET /api/stocks` - List all stocks
- `GET /api/stocks/{id}` - Get stock details
- `GET /api/stocks/search?query={query}` - Search stocks

### Portfolio
- `GET /api/portfolio` - Get user portfolio
- `GET /api/portfolio/summary` - Get portfolio summary
- `POST /api/portfolio/buy` - Buy stock
- `POST /api/portfolio/sell` - Sell stock

### Health
- `GET /api/health` - Application health check
- `GET /actuator/health` - Spring Boot actuator health

## 🐳 Docker Commands

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose stop

# Restart service
docker-compose restart backend

# Rebuild and restart
docker-compose up -d --build

# Stop and remove everything
docker-compose down

# Stop and remove including data
docker-compose down -v
```

## 🔍 Monitoring

### Health Checks
```bash
# Backend health
curl http://localhost:8080/api/health

# Actuator health
curl http://localhost:8080/actuator/health
```

### Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

### Resource Usage
```bash
docker stats
```

## 🗄️ Database

### Backup
```bash
docker-compose exec postgres pg_dump -U postgres stock_analysis > backup.sql
```

### Restore
```bash
docker-compose exec -T postgres psql -U postgres stock_analysis < backup.sql
```

### Access Database
```bash
docker-compose exec postgres psql -U postgres -d stock_analysis
```

## 🚨 Troubleshooting

### Backend won't start
```bash
docker-compose logs backend
docker-compose restart backend
```

### Frontend shows API errors
- Check CORS configuration in `.env`
- Verify `VITE_API_BASE_URL` is correct
- Rebuild frontend: `docker-compose build frontend`

### Database connection errors
```bash
docker-compose ps postgres
docker-compose exec postgres psql -U postgres -d stock_analysis -c "SELECT 1;"
```

See [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) for detailed troubleshooting.

## 📈 Roadmap

### Phase 1: Real-Time Data & Charts (Next)
- External API integration (Yahoo Finance/Alpha Vantage)
- Real-time price updates
- Interactive price charts
- Historical data storage

### Phase 2: Advanced Analytics & ML
- ML prediction service (FastAPI)
- Technical analysis engine
- Portfolio optimization
- Risk assessment
- Market sentiment analysis

### Phase 3: Production Enhancements
- Redis caching
- WebSocket for real-time updates
- Advanced monitoring
- Performance optimization
- CI/CD pipeline

## 🤝 Contributing

This is a portfolio project. For issues or suggestions, please create an issue.

## 📄 License

This project is for educational and portfolio purposes.

## 🙏 Acknowledgments

- Spring Boot for the backend framework
- React and Material-UI for the frontend
- Docker for containerization
- PostgreSQL for the database

## 📞 Support

For deployment issues:
1. Check logs: `docker-compose logs`
2. Verify health: `docker-compose ps`
3. Review troubleshooting guides in documentation
4. Check [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md)

---

**Status:** ✅ MVP Complete and Ready for Deployment

**Last Updated:** March 2026
