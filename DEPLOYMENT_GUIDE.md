# 🚀 LibreScholarHub Quick Deployment Guide

This is a simplified guide to get LibreScholarHub up and running quickly.

## ⚡ One-Command Deployment

```bash
# Clone, build, and deploy everything
git clone <repository-url> && cd libreScholarHub && git checkout deploy && chmod +x build.sh && ./build.sh && docker-compose -f docker-compose.dev.yml up -d
```

⚠️ **Important**: The deployment uses the `deploy` branch which contains the production-ready configuration.

## 📋 Step-by-Step Deployment

### Step 1: Prerequisites Check

```bash
# Check Docker is installed
docker --version
# Should show: Docker version 20.10.x or higher

# Check Docker Compose is installed
docker-compose --version
# Should show: Docker Compose version 2.x.x or higher

# Check available memory
free -h
# Recommended: At least 8GB RAM
```

### Step 2: Build All Services

```bash
# Clone the repository
git clone <repository-url>
cd libreScholarHub

# IMPORTANT: Checkout the deploy branch
git checkout deploy

# Make build script executable
chmod +x build.sh

# Build all microservices (takes 5-10 minutes)
./build.sh
```

⚠️ **Critical Step**: You must checkout the `deploy` branch before building!

**What this does:**
- Compiles all Java services with Maven
- Creates Docker images for each service
- Tags images appropriately

### Step 3: Start Infrastructure Services

```bash
# Start databases, cache, and messaging services
docker-compose -f docker-compose.dev.yml up -d postgres redis rabbitmq elasticsearch

# Wait for services to initialize (30-60 seconds)
sleep 60

# Verify they're healthy
docker-compose -f docker-compose.dev.yml ps
```

**Expected output:**
```
NAME                  STATUS
postgres-db-dev       Up (healthy)
redis-cache-dev       Up (healthy)
rabbitmq-broker-dev   Up (healthy)
elasticsearch-dev     Up (healthy)
```

### Step 4: Start Core Services

```bash
# Start Eureka Service Discovery
docker-compose -f docker-compose.dev.yml up -d eureka-server

# Wait for Eureka to be ready
sleep 30

# Start all microservices
docker-compose -f docker-compose.dev.yml up -d
```

### Step 5: Verify Deployment

```bash
# Check all services are running
docker-compose -f docker-compose.dev.yml ps

# View Eureka dashboard - should show 4-5 registered services
curl -s http://localhost:8761/eureka/apps | grep -oP '(?<=<name>)[^<]+' | sort -u
```

**Expected registered services:**
- API-GATEWAY
- USER-SERVICE
- RESEARCH-PAPER-SERVICE
- SEARCH-SERVICE
- SPRINGBOOT-RABBITMQ (notification-service)

### Step 6: Test the System

```bash
# Test API Gateway is responding
curl http://localhost:8080

# Test Eureka dashboard
open http://localhost:8761  # macOS
# or
xdg-open http://localhost:8761  # Linux

# Test RabbitMQ Management UI
open http://localhost:15672  # Username: guest, Password: guest
```

## 🎯 Common Commands

### Start Services

```bash
# Start all services
docker-compose -f docker-compose.dev.yml up -d

# Start specific service
docker-compose -f docker-compose.dev.yml up -d user-service
```

### Stop Services

```bash
# Stop all services
docker-compose -f docker-compose.dev.yml stop

# Stop and remove containers
docker-compose -f docker-compose.dev.yml down

# Stop and remove everything including volumes (⚠️ deletes all data)
docker-compose -f docker-compose.dev.yml down -v
```

### View Logs

```bash
# View all logs
docker-compose -f docker-compose.dev.yml logs -f

# View specific service logs
docker-compose -f docker-compose.dev.yml logs -f user-service

# View last 100 lines
docker logs user-service-dev --tail 100
```

### Restart Services

```bash
# Restart all services
docker-compose -f docker-compose.dev.yml restart

# Restart specific service
docker-compose -f docker-compose.dev.yml restart user-service
```

### Check Status

```bash
# List all containers and their status
docker-compose -f docker-compose.dev.yml ps

# Check which services are registered with Eureka
curl -s http://localhost:8761/eureka/apps | grep -E "<name>|<status>"
```

## 🔧 Quick Fixes

### Problem: Port Already in Use

```bash
# Find what's using port 8080
sudo lsof -i :8080
# or
sudo netstat -tulpn | grep 8080

# Kill the process
sudo kill -9 <PID>
```

### Problem: Out of Memory

```bash
# Check Docker memory
docker stats

# Increase memory limit in Docker Desktop settings
# Or adjust JAVA_OPTS in docker-compose.dev.yml:
JAVA_OPTS: -Xmx1024m -Xms512m
```

### Problem: Services Not Registering with Eureka

```bash
# Restart Eureka first
docker-compose -f docker-compose.dev.yml restart eureka-server

# Wait 30 seconds
sleep 30

# Restart microservices
docker-compose -f docker-compose.dev.yml restart api-gateway user-service research-paper-service search-service notification-service
```

### Problem: Database Connection Failed

```bash
# Check PostgreSQL is running
docker logs postgres-db-dev --tail 50

# Restart PostgreSQL
docker-compose -f docker-compose.dev.yml restart postgres

# Recreate database
docker-compose -f docker-compose.dev.yml down
docker volume rm librescholarhub_postgres-data-dev
docker-compose -f docker-compose.dev.yml up -d postgres
```

### Problem: Elasticsearch Red Status

```bash
# Check cluster health
curl http://localhost:9200/_cluster/health?pretty

# Set replicas to 0 for single-node setup
curl -X PUT "http://localhost:9200/_settings" -H 'Content-Type: application/json' -d'
{
  "index": {
    "number_of_replicas": 0
  }
}'
```

## 🧹 Clean Slate Restart

If things go wrong, start fresh:

```bash
# Stop everything
docker-compose -f docker-compose.dev.yml down -v

# Remove all images (optional)
docker rmi $(docker images 'librescholarhub*' -q)

# Rebuild
./build.sh

# Start fresh
docker-compose -f docker-compose.dev.yml up -d
```

## 📊 Monitoring URLs

Once deployed, access these URLs:

| Service | URL | Credentials |
|---------|-----|-------------|
| Eureka Dashboard | http://localhost:8761 | None |
| API Gateway | http://localhost:8080 | JWT Token Required |
| RabbitMQ Management | http://localhost:15672 | guest/guest |
| Elasticsearch | http://localhost:9200 | None |
| User Service Swagger | http://localhost:8090/swagger-ui.html | None |
| Research Paper Swagger | http://localhost:8091/swagger-ui.html | None |
| Search Service Swagger | http://localhost:8097/swagger-ui.html | None |

## 🧪 Testing the Deployment

### Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser@example.com",
    "password": "Test123!"
  }'
```

Save the JWT token from the response for authenticated requests.

### Upload Research Paper

```bash
curl -X POST http://localhost:8080/api/research-paper/upload \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -F "file=@/path/to/paper.pdf" \
  -F "title=My Research Paper" \
  -F "authors=John Doe, Jane Smith" \
  -F "abstract=This is the abstract"
```

### Search Papers

```bash
curl -X GET "http://localhost:8080/api/search?query=machine+learning" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

## 🐛 Debug Mode

To see detailed logs:

```bash
# Enable debug logging
docker-compose -f docker-compose.dev.yml logs -f | grep -i "error\|exception\|warn"

# Check specific service with debug
docker logs user-service-dev 2>&1 | grep -i "debug"
```

## 📈 Performance Tuning

### For Better Performance:

1. **Increase Memory Limits**:
Edit `docker-compose.dev.yml`:
```yaml
JAVA_OPTS: -Xmx2048m -Xms1024m
```

2. **Increase Elasticsearch Heap**:
```yaml
ES_JAVA_OPTS: "-Xms1g -Xmx1g"
```

3. **Adjust Docker Resources**:
- Docker Desktop → Preferences → Resources
- Set memory to at least 8GB
- Set CPUs to at least 4 cores

## 🎓 Next Steps

After successful deployment:

1. Read the main [README.md](README.md) for detailed API documentation
2. Check [DOCKER_README.md](DOCKER_README.md) for advanced Docker configurations
3. Explore the Swagger UI for each service
4. Review service logs to understand the system behavior
5. Set up your IDE for local development

## 🆘 Need Help?

1. Check service logs: `docker-compose -f docker-compose.dev.yml logs <service-name>`
2. Verify Eureka dashboard shows all services: http://localhost:8761
3. Check database connectivity: `docker exec -it postgres-db-dev psql -U libreresearchhub`
4. Test network connectivity: `docker exec user-service-dev ping -c 2 postgres`

## 🔒 Security Reminder

⚠️ **This is a DEVELOPMENT configuration!**

For production:
1. Change all default passwords
2. Enable HTTPS/SSL
3. Use Docker secrets for credentials
4. Enable Elasticsearch security
5. Configure proper firewall rules
6. Use environment-specific configurations

---

**Deployment Time Estimate:**
- First time: 15-20 minutes
- Subsequent deployments: 5-10 minutes

**Happy Deploying! 🎉**
