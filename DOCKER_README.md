# LibreScholarHub Docker Deployment Guide

This guide provides comprehensive instructions for deploying the LibreScholarHub microservices architecture using Docker and Docker Compose.

## 📋 Prerequisites

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Git** (for cloning the repository)
- **Java 21** (for local development)
- **Maven** (for building JAR files)

## 🏗️ Architecture Overview

The LibreScholarHub consists of the following microservices:

- **Eureka Server** (Port 8761) - Service discovery
- **API Gateway** (Port 8080) - Entry point for all requests
- **User Management Service** (Port 8090) - User authentication and management
- **Research Paper Service** (Port 8091) - Paper management and storage
- **Search Service** (Port 8097) - Search functionality
- **Notification Service** (Port 8082) - Notifications and messaging

### Supporting Services

- **PostgreSQL** (Port 5432) - Primary database
- **Redis** (Port 6379) - Caching layer
- **RabbitMQ** (Port 5672, Management: 15672) - Message broker

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd libreScholarHub

# IMPORTANT: Checkout the deploy branch
git checkout deploy
```

⚠️ **Note**: The `deploy` branch contains the production-ready Docker configuration and built artifacts.

### 2. Build and Start Development Environment

```bash
# Make scripts executable
chmod +x build.sh deploy.sh

# Build all services and start development environment
./deploy.sh build dev
```

### 3. Access the Services

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 🐳 Docker Files Overview

Each microservice has been optimized with:

- **Multi-stage builds** for smaller image sizes
- **Java 21 Alpine** base images for security and performance
- **Non-root user** execution for security
- **Health checks** for container monitoring
- **Optimized JVM settings** for containerized environments
- **Proper layer caching** for faster builds

### Service-Specific Dockerfiles

| Service | Dockerfile Location | Special Features |
|---------|-------------------|------------------|
| API Gateway | `api-gateway/Dockerfile` | Security-focused with OAuth2 |
| Eureka Server | `Eureka Server/Dockerfile` | Service discovery optimized |
| User Service | `userManagementService/Dockerfile` | Database and cache integration |
| Research Paper Service | `ResearchPaperService/Dockerfile` | File storage volume mounting |
| Search Service | `SearchService/Dockerfile` | Search optimization |
| Notification Service | `NotificationService/Dockerfile` | Message queue integration |

## 📝 Configuration Files

### Docker Compose Files

- **`docker-compose.yml`** - Base configuration
- **`docker-compose.dev.yml`** - Development environment overrides
- **`docker-compose.prod.yml`** - Production environment overrides

### Environment Variables

Create a `.env` file in the root directory for production:

```env
# Database Configuration
POSTGRES_USER=libreresearchhub
POSTGRES_PASSWORD=your_secure_password_here
POSTGRES_DB=libreresearchhub

# RabbitMQ Configuration
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=your_secure_password_here

# Spring Profiles
SPRING_PROFILES_ACTIVE=prod
```

## 🛠️ Build Scripts

### Build Script (`build.sh`)

Builds all microservices and Docker images:

```bash
# Development build
./build.sh dev

# Production build
./build.sh prod
```

### Deploy Script (`deploy.sh`)

Manages the deployment lifecycle:

```bash
# Start development environment
./deploy.sh up dev

# Start production environment
./deploy.sh up prod

# Stop services
./deploy.sh down [env]

# View logs
./deploy.sh logs [env]

# Check status
./deploy.sh status [env]

# Restart services
./deploy.sh restart [env]

# Build and deploy
./deploy.sh build [env]
```

## 🔧 Development Workflow

### 1. Development Environment

For active development with hot reloading:

```bash
# Start development environment
./deploy.sh up dev

# View logs for specific service
docker-compose -f docker-compose.dev.yml logs -f user-service

# Execute into a container
docker exec -it user-service-dev bash
```

### 2. Building Individual Services

```bash
# Build specific service
cd userManagementService
./mvnw clean package -DskipTests
docker build -t user-service:latest .
```

### 3. Database Management

```bash
# Connect to PostgreSQL
docker exec -it postgres-db psql -U libreresearchhub -d libreresearchhub

# View Redis data
docker exec -it redis-cache redis-cli

# Access RabbitMQ management
# Open http://localhost:15672 in browser
```

## 🚀 Production Deployment

### 1. Pre-deployment Checklist

- [ ] Update environment variables in `.env` file
- [ ] Ensure all services build successfully
- [ ] Review security configurations
- [ ] Set up monitoring and logging
- [ ] Configure backup strategies

### 2. Production Deployment

```bash
# Build production images
./build.sh prod

# Start production environment
./deploy.sh up prod

# Monitor deployment
./deploy.sh status prod
```

### 3. Production Monitoring

```bash
# View resource usage
docker stats

# Check service health
curl http://localhost:8080/actuator/health

# View logs
./deploy.sh logs prod
```

## 🔍 Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using ports
   sudo netstat -tulpn | grep :8080
   
   # Kill processes on specific port
   sudo fuser -k 8080/tcp
   ```

2. **Memory Issues**
   ```bash
   # Increase Docker memory limit
   # Docker Desktop > Settings > Resources > Memory
   
   # Check container memory usage
   docker stats --no-stream
   ```

3. **Build Failures**
   ```bash
   # Clean Docker build cache
   docker system prune -a
   
   # Rebuild without cache
   docker-compose build --no-cache
   ```

4. **Service Discovery Issues**
   ```bash
   # Check Eureka server logs
   docker-compose logs eureka-server
   
   # Verify service registration
   curl http://localhost:8761/eureka/apps
   ```

### Health Checks

All services include health checks accessible via:

- Individual service: `http://localhost:[port]/actuator/health`
- Via API Gateway: `http://localhost:8080/[service]/actuator/health`

### Log Aggregation

For production, consider using centralized logging:

```bash
# View all service logs
docker-compose logs -f

# Filter specific service logs
docker-compose logs -f user-service

# Export logs to file
docker-compose logs > logs/application.log
```

## 📊 Performance Optimization

### JVM Tuning

The Dockerfiles include optimized JVM settings:

- `-XX:+UseContainerSupport` - Container-aware JVM
- `-XX:MaxRAMPercentage=75.0` - Memory allocation
- `-XX:+UseG1GC` - Garbage collector (production)

### Docker Optimization

- Multi-stage builds reduce image size
- Layer caching speeds up builds
- Health checks enable automatic recovery
- Resource limits prevent container sprawl

## 🔐 Security Considerations

- Non-root user execution in containers
- Secure environment variable handling
- Network isolation via Docker networks
- Regular base image updates
- Secret management for production

## 📚 Additional Resources

- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and test with Docker
4. Submit a pull request

## 📞 Support

For issues and questions:

1. Check the troubleshooting section
2. View container logs: `./deploy.sh logs`
3. Check service status: `./deploy.sh status`
4. Open an issue in the repository
