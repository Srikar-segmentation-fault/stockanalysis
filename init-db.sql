-- Initialize database for AI Stock Analysis
-- This script runs automatically when the PostgreSQL container starts for the first time

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE stock_analysis TO postgres;

-- Create schema (tables will be created by Hibernate on first run)
-- This is just a placeholder for any initial setup needed
