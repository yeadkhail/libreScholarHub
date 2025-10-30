#!/bin/bash

# Deployment script for LibreScholarHub microservices
set -e

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

# Show usage
show_usage() {
    echo "Usage: $0 [COMMAND] [ENVIRONMENT]"
    echo ""
    echo "Commands:"
    echo "  up      - Start all services"
    echo "  down    - Stop all services"
    echo "  restart - Restart all services"
    echo "  logs    - Show logs for all services"
    echo "  status  - Show status of all services"
    echo "  build   - Build and start services"
    echo ""
    echo "Environments:"
    echo "  dev     - Development environment (default)"
    echo "  prod    - Production environment"
    echo ""
    echo "Examples:"
    echo "  $0 up dev       # Start development environment"
    echo "  $0 up prod      # Start production environment"
    echo "  $0 logs         # Show logs for all services"
    echo "  $0 build prod   # Build and start production environment"
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Check if Docker Compose is available
check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        print_error "Docker Compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
}

# Get compose files based on environment
get_compose_files() {
    local env=$1
    if [ "$env" = "prod" ]; then
        echo "-f docker-compose.yml -f docker-compose.prod.yml"
    elif [ "$env" = "dev" ]; then
        echo "-f docker-compose.dev.yml"
    else
        echo "-f docker-compose.yml"
    fi
}

# Main execution
COMMAND=${1:-up}
ENVIRONMENT=${2:-dev}

case $COMMAND in
    up)
        check_docker
        check_docker_compose
        print_status "Starting LibreScholarHub in $ENVIRONMENT environment..."
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        docker-compose $COMPOSE_FILES up -d
        
        print_success "Services started successfully!"
        print_status "Waiting for services to be healthy..."
        sleep 10
        
        print_status "Service status:"
        docker-compose $COMPOSE_FILES ps
        ;;
    
    down)
        check_docker
        check_docker_compose
        print_status "Stopping LibreScholarHub services..."
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        docker-compose $COMPOSE_FILES down
        
        print_success "Services stopped successfully!"
        ;;
    
    restart)
        check_docker
        check_docker_compose
        print_status "Restarting LibreScholarHub in $ENVIRONMENT environment..."
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        docker-compose $COMPOSE_FILES down
        docker-compose $COMPOSE_FILES up -d
        
        print_success "Services restarted successfully!"
        ;;
    
    logs)
        check_docker
        check_docker_compose
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        docker-compose $COMPOSE_FILES logs -f
        ;;
    
    status)
        check_docker
        check_docker_compose
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        print_status "Service status:"
        docker-compose $COMPOSE_FILES ps
        
        print_status "Resource usage:"
        docker stats --no-stream
        ;;
    
    build)
        check_docker
        check_docker_compose
        
        print_status "Building and starting LibreScholarHub in $ENVIRONMENT environment..."
        
        # Run build script first
        if [ -x "./build.sh" ]; then
            ./build.sh $ENVIRONMENT
        else
            print_warning "Build script not found or not executable"
        fi
        
        COMPOSE_FILES=$(get_compose_files $ENVIRONMENT)
        docker-compose $COMPOSE_FILES up -d
        
        print_success "Services built and started successfully!"
        ;;
    
    *)
        print_error "Unknown command: $COMMAND"
        show_usage
        exit 1
        ;;
esac

# Show helpful information
if [ "$COMMAND" = "up" ] || [ "$COMMAND" = "restart" ] || [ "$COMMAND" = "build" ]; then
    echo ""
    print_status "Services are now running. Access points:"
    echo "  • Eureka Server:        http://localhost:8761"
    echo "  • API Gateway:          http://localhost:8080"
    echo "  • User Service:         http://localhost:8090"
    echo "  • Research Paper Service: http://localhost:8091"
    echo "  • Search Service:       http://localhost:8097"
    echo "  • Notification Service: http://localhost:8082"
    echo "  • RabbitMQ Management:  http://localhost:15672 (guest/guest)"
    echo "  • PostgreSQL:           localhost:5432"
    echo "  • Redis:                localhost:6379"
    echo ""
    print_status "Useful commands:"
    echo "  • View logs:    $0 logs $ENVIRONMENT"
    echo "  • Check status: $0 status $ENVIRONMENT"
    echo "  • Stop services: $0 down $ENVIRONMENT"
fi
