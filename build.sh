#!/bin/bash

# Build script for LibreScholarHub microservices
set -e

echo "🚀 Building LibreScholarHub Microservices..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if Docker Compose is available
if ! command -v docker-compose > /dev/null 2>&1; then
    print_error "Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
fi

# Build mode (dev/prod)
BUILD_MODE=${1:-dev}

print_status "Building in $BUILD_MODE mode..."

# Services to build in dependency order
# userManagementService must be built first as it's a dependency for ResearchPaperService
SERVICES=(
    "userManagementService"
    "Eureka Server"
    "api-gateway"
    "ResearchPaperService"
    "SearchService"
    "NotificationService"
)

# Build each service
for service in "${SERVICES[@]}"; do
    print_status "Building $service..."
    
    if [ -d "$service" ]; then
        cd "$service"
        
        # Clean and package with Maven
        if [ -f "mvnw" ]; then
            print_status "Using Maven wrapper for $service"
            if [ "$service" = "userManagementService" ]; then
                # Install to local repository for other services to use
                ./mvnw clean install -DskipTests
            else
                ./mvnw clean package -DskipTests
            fi
        elif [ -f "pom.xml" ]; then
            print_status "Using system Maven for $service"
            if [ "$service" = "userManagementService" ]; then
                # Install to local repository for other services to use
                mvn clean install -DskipTests
            else
                mvn clean package -DskipTests
            fi
        else
            print_warning "No Maven configuration found for $service, skipping Maven build"
        fi
        
        cd ..
        print_success "Built $service successfully"
    else
        print_error "Service directory $service not found"
        exit 1
    fi
done

# Build Docker images
print_status "Building Docker images..."

if [ "$BUILD_MODE" = "prod" ]; then
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml build --no-cache
    print_success "Production images built successfully"
elif [ "$BUILD_MODE" = "dev" ]; then
    docker-compose -f docker-compose.dev.yml build --no-cache
    print_success "Development images built successfully"
else
    docker-compose build --no-cache
    print_success "Default images built successfully"
fi

print_success "🎉 All services built successfully!"

# Show next steps
echo ""
print_status "Next steps:"
echo "  • For development: docker-compose -f docker-compose.dev.yml up -d"
echo "  • For production: docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d"
echo "  • To view logs: docker-compose logs -f [service-name]"
echo "  • To stop: docker-compose down"
