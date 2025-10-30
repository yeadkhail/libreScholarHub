# 🚀 LibreScholarHub - Quick Start Guide

## ⚡ Super Quick Deploy (Copy & Paste)

```bash
# One command to rule them all
git clone <repository-url> && \
cd libreScholarHub && \
git checkout deploy && \
chmod +x build.sh && \
./build.sh && \
docker-compose -f docker-compose.dev.yml up -d
```

Wait 2 minutes, then open: http://localhost:8761

---

## 📝 Step-by-Step (5 Steps)

### Step 1️⃣: Clone and Checkout Deploy Branch
```bash
git clone <repository-url>
cd libreScholarHub
git checkout deploy
```
⚠️ **CRITICAL**: You must be on the `deploy` branch!

### Step 2️⃣: Build Services
```bash
chmod +x build.sh
./build.sh
```
⏱️ Takes ~5-10 minutes

### Step 3️⃣: Start Infrastructure
```bash
docker-compose -f docker-compose.dev.yml up -d postgres redis rabbitmq elasticsearch
```
⏱️ Wait 60 seconds for databases to initialize

### Step 4️⃣: Start Microservices
```bash
docker-compose -f docker-compose.dev.yml up -d
```
⏱️ Wait 90 seconds for services to register

### Step 5️⃣: Verify Deployment
```bash
# Check all services are running
docker-compose -f docker-compose.dev.yml ps

# Open Eureka Dashboard - should show 5 registered services
open http://localhost:8761  # or xdg-open on Linux
```

---

## ✅ What You Should See

### In Terminal:
```
NAME                         STATUS
eureka-server-dev            Up (healthy)
api-gateway-dev              Up
user-service-dev             Up
research-paper-service-dev   Up
search-service-dev           Up
notification-service-dev     Up
postgres-db-dev              Up (healthy)
redis-cache-dev              Up (healthy)
rabbitmq-broker-dev          Up (healthy)
elasticsearch-dev            Up (healthy)
```

### In Eureka Dashboard (http://localhost:8761):
- API-GATEWAY
- USER-SERVICE
- RESEARCH-PAPER-SERVICE
- SEARCH-SERVICE
- SPRINGBOOT-RABBITMQ

---

## 🔍 Quick Health Check

```bash
# Check all services are registered
curl -s http://localhost:8761/eureka/apps | grep -oP '(?<=<name>)[^<]+' | sort -u
```

**Expected Output:**
```
API-GATEWAY
RESEARCH-PAPER-SERVICE
SEARCH-SERVICE
SPRINGBOOT-RABBITMQ
USER-SERVICE
```

---

## 🌐 Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | None |
| **API Gateway** | http://localhost:8080 | JWT Token |
| **RabbitMQ UI** | http://localhost:15672 | guest/guest |
| **Elasticsearch** | http://localhost:9200 | None |

---

## 🧪 Test the API

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "Test123!"
  }'
```

Copy the JWT token from the response.

### 3. Test Search
```bash
curl -X GET "http://localhost:8080/api/search?query=test" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

---

## 🛑 Stop Services

```bash
# Stop all services
docker-compose -f docker-compose.dev.yml stop

# Stop and remove containers
docker-compose -f docker-compose.dev.yml down

# Nuclear option: Remove everything including data
docker-compose -f docker-compose.dev.yml down -v
```

---

## 🐛 Troubleshooting

### Services Not Starting?
```bash
# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Check specific service
docker logs user-service-dev --tail 100
```

### Port Conflicts?
```bash
# Find what's using port 8080
sudo lsof -i :8080
# Kill it or change the port
```

### Need Fresh Start?
```bash
# Complete reset
docker-compose -f docker-compose.dev.yml down -v
docker system prune -a --volumes
./build.sh
docker-compose -f docker-compose.dev.yml up -d
```

### Services Not Registering with Eureka?
```bash
# Make sure you're on the deploy branch
git branch

# Should show: * deploy

# If not:
git checkout deploy
```

---

## 📚 More Information

- **Detailed Guide**: [README.md](README.md)
- **Deployment Guide**: [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
- **Docker Details**: [DOCKER_README.md](DOCKER_README.md)

---

## ⏱️ Time Estimates

| Task | First Time | Subsequent |
|------|-----------|------------|
| Clone Repository | 1 min | - |
| Build Services | 5-10 min | 3-5 min |
| Start Services | 2-3 min | 1-2 min |
| **Total** | **8-14 min** | **4-7 min** |

---

## 💡 Pro Tips

1. **Always checkout `deploy` branch first**
   ```bash
   git checkout deploy
   ```

2. **Wait for infrastructure before starting services**
   ```bash
   # Start infra first
   docker-compose -f docker-compose.dev.yml up -d postgres redis rabbitmq elasticsearch
   sleep 60
   
   # Then start services
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Monitor logs in real-time**
   ```bash
   docker-compose -f docker-compose.dev.yml logs -f
   ```

4. **Check Eureka dashboard frequently**
   - Open http://localhost:8761
   - All services should be UP and running

5. **Restart unhealthy services**
   ```bash
   docker-compose -f docker-compose.dev.yml restart user-service
   ```

---

## 🎯 Success Criteria

Your deployment is successful when:
- ✅ All 10 containers are running
- ✅ Eureka dashboard shows 5 registered services
- ✅ You can access http://localhost:8761
- ✅ You can register and login a user
- ✅ No error logs in `docker-compose logs`

---

**Ready to deploy? Just run:**
```bash
git clone <repo> && cd libreScholarHub && git checkout deploy && ./build.sh && docker-compose -f docker-compose.dev.yml up -d
```

**Happy Deploying! 🎉**
