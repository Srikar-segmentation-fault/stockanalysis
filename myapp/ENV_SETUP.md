# Environment Variables Setup

## Overview
This application uses environment variables to store sensitive credentials like database passwords and JWT secrets. These are loaded from a `.env` file during development.

## Setup Instructions

### 1. Create your .env file
Copy the example file and fill in your actual credentials:

```bash
cp .env.example .env
```

### 2. Edit .env with your credentials
Open `.env` and replace the placeholder values with your actual credentials:

```properties
# Database Configuration
DB_URL=jdbc:postgresql://your-actual-host/your-database
DB_USERNAME=your-username
DB_PASSWORD=your-password

# JWT Configuration
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000
```

### 3. Verify .gitignore
Make sure `.env` is listed in `.gitignore` to prevent committing secrets:

```
.env
```

## How It Works

1. The `DotenvConfig` class loads variables from `.env` file at application startup
2. Spring Boot's `application.properties` references these variables using `${VARIABLE_NAME}` syntax
3. The `.env` file is ignored by git, keeping your secrets safe

## Production Deployment

In production environments, set these as actual environment variables instead of using a `.env` file:

```bash
export DB_URL="jdbc:postgresql://production-host/db"
export DB_USERNAME="prod-user"
export DB_PASSWORD="prod-password"
export JWT_SECRET="production-secret"
export JWT_EXPIRATION="86400000"
```

## Security Best Practices

- ✅ Never commit `.env` to version control
- ✅ Use different credentials for development and production
- ✅ Rotate secrets regularly
- ✅ Use strong, randomly generated JWT secrets
- ✅ Keep `.env.example` updated with required variables (but no actual values)
