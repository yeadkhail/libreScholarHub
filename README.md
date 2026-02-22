# LibreScholarHub

A microservices-based research paper management and search platform built with Spring Boot, Spring Cloud, and Elasticsearch.

##  Table of Contents

- [Architecture](#architecture)
- [Services](#services)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Deployment](#deployment)
- [Service Endpoints](#service-endpoints)
- [Configuration](#configuration)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)
- [Development](#development)

## Architecture

LibreScholarHub uses a microservices architecture with the following components:

```
                    ┌─────────────────┐
                    │   API Gateway   │ :8080
                    │   (Entry Point) │
                    └────────┬────────┘
                             │
                        ┌────┴─────┐
                        │  Eureka  │ :8761
                        │  Server  │ (Service Discovery)
                        └────┬─────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼────────┐  ┌────────▼────────┐  ┌───────▼────────┐
│ User Service   │  │ Research Paper  │  │ Search Service │
│    :8090       │  │    Service      │  │    :8097       │
│                │  │    :8091        │  │                │
└───────┬────────┘  └────────┬────────┘  └────────┬───────┘
        │                    │                     │
        └────────────────────┼─────────────────────┘
                             │
        ┌────────────────────┼─────────────────────────┐
        │                    │                          │
┌───────▼────────┐  ┌────────▼────────┐  ┌─────────────▼──────┐
│   PostgreSQL   │  │     Redis       │  │    Elasticsearch   │
│    :5432       │  │     :6379       │  │       :9200        │
└────────────────┘  └─────────────────┘  └────────────────────┘
                             │
                    ┌────────▼────────┐
                    │    RabbitMQ     │
                    │  :5672/:15672   │
                    └─────────────────┘
```

## Services

### Core Services

1. **Eureka Server** (Port: 8761)
   - Service discovery and registration
   - Health monitoring
   - Load balancing support

2. **API Gateway** (Port: 8080)
   - Single entry point for all requests
   - Request routing
   - Load balancing
   - Authentication integration

3. **User Service** (Port: 8090)
   - User authentication and authorization
   - JWT token management
   - User profile management

4. **Research Paper Service** (Port: 8091)
   - Research paper upload and management
   - PDF storage and retrieval
   - Metadata management

5. **Search Service** (Port: 8097)
   - Elasticsearch integration
   - Full-text search
   - Advanced filtering

6. **Notification Service** (Port: 8082)
   - Event-driven notifications
   - RabbitMQ message consumer
   - Email/notification dispatching

### Infrastructure Services

- **PostgreSQL** (Port: 5432) - Primary database
- **Redis** (Port: 6379) - Caching layer
- **RabbitMQ** (Port: 5672, Management: 15672) - Message broker
- **Elasticsearch** (Port: 9200) - Search engine

## Prerequisites

Before deploying LibreScholarHub, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Java 17** (for local development)
- **Maven 3.8+** (for building from source)
- **Git** (for cloning the repository)


## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yeadkhail/libreScholarHub
cd libreScholarHub

# Checkout the deploy branch
git checkout deploy
```

⚠️ **Important**: Make sure to checkout the `deploy` branch before building and deploying the services.

### 2. Build All Services

```bash
# Make the build script executable
chmod +x build.sh

# Build all microservices
./build.sh
```

This script will:
- Build all microservices using Maven
- Create Docker images for each service
- Tag images appropriately

### 3. Deploy with Docker Compose

#### Development Environment

```bash
# Start all services
docker-compose -f docker-compose.dev.yml up -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Check service status
docker-compose -f docker-compose.dev.yml ps
```

#### Production Environment

```bash
# Deploy to production
docker-compose -f docker-compose.prod.yml up -d
```

### 4. Verify Deployment

Wait 1-2 minutes for all services to start, then verify:

```bash
# Check all services are running
docker-compose -f docker-compose.dev.yml ps

# Check Eureka dashboard (should show all services registered)
curl http://localhost:8761/

# Check service health
curl http://localhost:8761/eureka/apps
```

## Deployment

### Step-by-Step Deployment Guide

#### Step 1: Environment Setup

Create a `.env` file (optional) for custom configurations:

```bash
# Database Configuration
POSTGRES_USER=libreresearchhub
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=libreresearchhub

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# RabbitMQ Configuration
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# JWT Secret (Base64 encoded)
JWT_SECRET=UkFOVHJhdkluZVRlbXBlcnlCb2R5c0NvdWx0Wk1hcmFSVE1hY2tIb2RzdHJhbUE=
```

#### Step 2: Build Services

```bash
# Make sure you're on the deploy branch
git checkout deploy

# Build all services
./build.sh

# Or build individual services
cd userManagementService && mvn clean package -DskipTests
cd ../ResearchPaperService && mvn clean package -DskipTests
cd ../SearchService && mvn clean package -DskipTests
cd ../api-gateway && mvn clean package -DskipTests
cd ../NotificationService && mvn clean package -DskipTests
cd ../Eureka\ Server && mvn clean package -DskipTests
```

#### Step 3: Start Infrastructure Services First

```bash
# Start database and caching services
docker-compose -f docker-compose.dev.yml up -d postgres redis rabbitmq elasticsearch

# Wait for services to be healthy (30-60 seconds)
docker-compose -f docker-compose.dev.yml ps
```

#### Step 4: Start Core Services

```bash
# Start Eureka Server
docker-compose -f docker-compose.dev.yml up -d eureka-server

# Wait for Eureka to be healthy (30 seconds)
sleep 30

# Start all microservices
docker-compose -f docker-compose.dev.yml up -d
```

#### Step 5: Verify Deployment

```bash
# Check all containers are running
docker-compose -f docker-compose.dev.yml ps

# View Eureka dashboard
open http://localhost:8761

# Check registered services
curl -s http://localhost:8761/eureka/apps | grep -E "<name>|<status>"
```

### Using the Deployment Script

```bash
# Make deployment script executable
chmod +x deploy.sh

# Deploy
./deploy.sh
```

## Service Endpoints

### Eureka Dashboard
- **URL**: http://localhost:8761
- **Description**: Service registry and health monitoring

### API Gateway
- **Base URL**: http://localhost:8080
- **Routes**:
  - `/api/auth/**` → User Service
  - `/api/research-paper/**` → Research Paper Service
  - `/api/search/**` → Search Service

### User Service (via API Gateway)
```bash
# Register user
POST http://localhost:8080/api/auth/register
Content-Type: application/json
{
  "username": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

# Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json
{
  "username": "user@example.com",
  "password": "password123"
}
```

### Research Paper Service (via API Gateway)
```bash
# Upload research paper
POST http://localhost:8080/api/research-paper/upload
Authorization: Bearer <your-jwt-token>
Content-Type: multipart/form-data

# Get all papers
GET http://localhost:8080/api/research-paper/papers
Authorization: Bearer <your-jwt-token>
```

### Search Service (via API Gateway)
```bash
# Search papers
GET http://localhost:8080/api/search?query=machine+learning
Authorization: Bearer <your-jwt-token>
```

### RabbitMQ Management
- **URL**: http://localhost:15672
- **Username**: guest
- **Password**: guest

### Elasticsearch
- **URL**: http://localhost:9200
- **Cluster Health**: http://localhost:9200/_cluster/health
- **Indices**: http://localhost:9200/_cat/indices?v

## Configuration

### Environment Variables

Each service can be configured using environment variables in `docker-compose.dev.yml`:

#### Common Variables
```yaml
SPRING_PROFILES_ACTIVE: dev
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
JAVA_OPTS: -Xmx512m -Xms256m
```

#### Database Configuration
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/database_name
SPRING_DATASOURCE_USERNAME: libreresearchhub
SPRING_DATASOURCE_PASSWORD: 123456
```

#### Redis Configuration
```yaml
SPRING_DATA_REDIS_HOST: redis
SPRING_DATA_REDIS_PORT: 6379
```

#### RabbitMQ Configuration
```yaml
SPRING_RABBITMQ_HOST: rabbitmq
SPRING_RABBITMQ_PORT: 5672
SPRING_RABBITMQ_USERNAME: guest
SPRING_RABBITMQ_PASSWORD: guest
```

#### Elasticsearch Configuration
```yaml
SPRING_ELASTICSEARCH_URIS: http://elasticsearch:9200
```

### Resource Limits

Adjust memory limits in `docker-compose.dev.yml`:

```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m  # Increase heap size
```

For Elasticsearch:
```yaml
environment:
  - "ES_JAVA_OPTS=-Xms1g -Xmx1g"  # Set to half of available RAM
```

## Monitoring

### Health Checks

All services have health checks configured:

```bash
# Check all service health
docker-compose -f docker-compose.dev.yml ps

# Check specific service logs
docker logs user-service-dev --tail 100 -f
docker logs api-gateway-dev --tail 100 -f
docker logs search-service-dev --tail 100 -f
```

### Service Discovery Status

```bash
# List registered services
curl -s http://localhost:8761/eureka/apps | grep -E "<name>|<status>"

# Get detailed service info
curl http://localhost:8761/eureka/apps/USER-SERVICE
```

### Database Monitoring

```bash
# Connect to PostgreSQL
docker exec -it postgres-db-dev psql -U libreresearchhub -d libreresearchhub

# List databases
\l

# Connect to specific database
\c user_service_db

# List tables
\dt
```

### Elasticsearch Monitoring

```bash
# Cluster health
curl http://localhost:9200/_cluster/health?pretty

# List indices
curl http://localhost:9200/_cat/indices?v

# Node stats
curl http://localhost:9200/_nodes/stats?pretty
```

## Troubleshooting

### Common Issues

#### 1. Services Not Registering with Eureka

**Symptom**: Services show as "unhealthy" or not appearing in Eureka dashboard

**Solution**:
```bash
# Check Eureka logs
docker logs eureka-server-dev --tail 100

# Restart the service
docker-compose -f docker-compose.dev.yml restart user-service

# Check network connectivity
docker exec user-service-dev ping -c 2 eureka-server
```

#### 2. Elasticsearch Red Status

**Symptom**: Elasticsearch cluster shows RED status

**Solution**:
```bash
# Check cluster health
curl http://localhost:9200/_cluster/health?pretty

# Delete problematic indices
curl -X DELETE http://localhost:9200/problematic_index

# Elasticsearch is running in single-node mode, ensure replicas are set to 0
curl -X PUT "http://localhost:9200/_settings" -H 'Content-Type: application/json' -d'
{
  "index": {
    "number_of_replicas": 0
  }
}'
```

#### 3. Out of Memory Errors

**Symptom**: Services crashing with OOM errors

**Solution**:
- Increase Docker memory limits
- Adjust JAVA_OPTS in docker-compose.dev.yml:
```yaml
JAVA_OPTS: -Xmx1024m -Xms512m
```

#### 4. Database Connection Issues

**Symptom**: Services can't connect to PostgreSQL

**Solution**:
```bash
# Check PostgreSQL is running
docker-compose -f docker-compose.dev.yml ps postgres

# Check database logs
docker logs postgres-db-dev --tail 50

# Test connection
docker exec -it postgres-db-dev psql -U libreresearchhub -c "SELECT 1"
```

#### 5. Port Conflicts

**Symptom**: "Port already in use" error

**Solution**:
```bash
# Find process using the port
lsof -i :8080  # Replace with your port
# Or on Linux
netstat -tulpn | grep 8080

# Kill the process or change port in docker-compose.dev.yml
```

### Viewing Logs

```bash
# All services
docker-compose -f docker-compose.dev.yml logs -f

# Specific service
docker-compose -f docker-compose.dev.yml logs -f user-service

# Last N lines
docker logs user-service-dev --tail 100

# Follow logs
docker logs -f user-service-dev
```

### Resetting the Environment

```bash
# Stop all services
docker-compose -f docker-compose.dev.yml down

# Remove volumes (WARNING: This deletes all data)
docker-compose -f docker-compose.dev.yml down -v

# Clean up Docker system
docker system prune -a --volumes

# Rebuild and restart
./build.sh
docker-compose -f docker-compose.dev.yml up -d
```

## Development

### Local Development Setup

#### Running Services Locally (Outside Docker)

1. **Start Infrastructure Services**:
```bash
docker-compose -f docker-compose.dev.yml up -d postgres redis rabbitmq elasticsearch
```

2. **Run Eureka Server**:
```bash
cd "Eureka Server"
mvn spring-boot:run
```

3. **Run Individual Microservices**:
```bash
# User Service
cd userManagementService
mvn spring-boot:run

# Research Paper Service
cd ResearchPaperService
mvn spring-boot:run

# Search Service
cd SearchService
mvn spring-boot:run

# API Gateway
cd api-gateway
mvn spring-boot:run
```

### Hot Reload with Docker

The development docker-compose mounts source code as read-only volumes for debugging:

```yaml
volumes:
  - ./userManagementService/src:/workspace/src:ro
```

To enable hot reload:
1. Configure Spring Boot DevTools in your services
2. Rebuild the image after code changes
3. Or use volume mounts with auto-rebuild

### Testing

```bash
# Run all tests
mvn test

# Run tests for specific service
cd userManagementService && mvn test

# Skip tests during build
mvn clean package -DskipTests
```

### API Documentation

Access Swagger UI for each service:

- User Service: http://localhost:8090/swagger-ui.html
- Research Paper Service: http://localhost:8091/swagger-ui.html
- Search Service: http://localhost:8097/swagger-ui.html

## Environment-Specific Configurations

### Development (`docker-compose.dev.yml`)
- Debug logging enabled
- Hot reload support
- Exposed ports for direct access
- Lower resource limits

### Production (`docker-compose.prod.yml`)
- Optimized performance settings
- Higher resource limits
- Security hardening
- Health checks enabled

## Security Notes

** Important for Production:**

1. **Change Default Passwords**:
   - PostgreSQL password
   - RabbitMQ credentials
   - JWT secret key

2. **Enable HTTPS**:
   - Configure SSL certificates
   - Update API Gateway with HTTPS

3. **Secure Elasticsearch**:
   - Enable X-Pack security
   - Set up authentication

4. **Network Security**:
   - Use private Docker networks
   - Limit port exposure
   - Implement firewall rules

5. **Environment Variables**:
   - Use Docker secrets for sensitive data
   - Never commit credentials to version control


##  Acknowledgments

Built with:
- Spring Boot & Spring Cloud
- Netflix Eureka
- Elasticsearch
- PostgreSQL
- Redis
- RabbitMQ
- Docker & Docker Compose

---

