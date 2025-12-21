# CloudCart Microservices Platform

A production-ready e-commerce microservices platform built with Spring Boot 3.2, Spring Cloud, and Docker.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (:8080)                       │
│         (Routing, Auth, Rate Limiting, Circuit Breaker)          │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  User Service │    │Product Service│    │ Order Service │
│    (:8081)    │    │   (:8082)     │    │   (:8083)     │
└───────┬───────┘    └───────┬───────┘    └───────┬───────┘
        │                    │                    │
        ▼                    ▼                    ▼
   [PostgreSQL]        [PostgreSQL]         [PostgreSQL]
                                                  
┌─────────────────────────────────────────────────────────────────┐
│                   Service Registry (:8761)                       │
│                        (Eureka Server)                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    Config Server (:8888)                         │
│                 (Centralized Configuration)                      │
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.2.1, Spring Cloud 2023.0.0 |
| **Language** | Java 17 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Configuration** | Spring Cloud Config |
| **Database** | PostgreSQL 15 |
| **Caching** | Redis 7 |
| **Security** | Spring Security, JWT |
| **Resilience** | Resilience4j (Circuit Breaker, Retry, Rate Limiter) |
| **Observability** | Micrometer, Prometheus, Zipkin |
| **Documentation** | SpringDoc OpenAPI 3 |
| **Containerization** | Docker, Docker Compose |
| **Build Tool** | Maven |

## 📁 Project Structure

```
cloudcart-microservices-platform/
├── pom.xml                     # Parent POM
├── docker-compose.yml          # Docker Compose configuration
├── service-registry/           # Eureka Server
├── config-server/              # Centralized Configuration
├── api-gateway/                # API Gateway
├── user-service/               # User Management & Authentication
├── product-service/            # Product Catalog (scaffold)
├── order-service/              # Order Management (scaffold)
└── notification-service/       # Notifications (scaffold)
```

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Option 1: Run with Docker Compose (Recommended)

```bash
# Build all services
mvn clean package -DskipTests

# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Option 2: Run Locally (Development)

1. **Start Infrastructure**
```bash
# Start PostgreSQL and Redis
docker-compose up -d postgres redis
```

2. **Start Services (in order)**
```bash
# Terminal 1: Service Registry
cd service-registry && mvn spring-boot:run

# Terminal 2: Config Server
cd config-server && mvn spring-boot:run

# Terminal 3: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4: User Service
cd user-service && mvn spring-boot:run
```

## 🔌 Service Endpoints

| Service | Port | URL |
|---------|------|-----|
| **Service Registry** | 8761 | http://localhost:8761 |
| **Config Server** | 8888 | http://localhost:8888 |
| **API Gateway** | 8080 | http://localhost:8080 |
| **User Service** | 8081 | http://localhost:8081 |
| **PostgreSQL** | 5432 | localhost:5432 |
| **Redis** | 6379 | localhost:6379 |
| **Zipkin** | 9411 | http://localhost:9411 |

## 📚 API Documentation

Once services are running, access OpenAPI documentation:
- **User Service**: http://localhost:8080/api/users/swagger-ui.html
- **Direct Access**: http://localhost:8081/swagger-ui.html

## 🔐 Authentication

### Register a New User
```bash
curl -X POST http://localhost:8080/api/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "johndoe",
    "password": "SecurePass123!"
  }'
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run tests for specific service
cd user-service && mvn test

# Run with coverage
mvn test jacoco:report
```

## 📊 Monitoring & Observability

### Health Checks
```bash
# Gateway health
curl http://localhost:8080/actuator/health

# User Service health
curl http://localhost:8081/actuator/health
```

### Metrics (Prometheus)
```bash
curl http://localhost:8081/actuator/prometheus
```

### Distributed Tracing
Access Zipkin UI: http://localhost:9411

## ⚙️ Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `default` | Active Spring profile |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka` | Eureka server URL |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config server URL |
| `POSTGRES_HOST` | `localhost` | PostgreSQL host |
| `POSTGRES_PORT` | `5432` | PostgreSQL port |
| `POSTGRES_DB` | `cloudcart_users` | Database name |
| `POSTGRES_USER` | `cloudcart` | Database user |
| `POSTGRES_PASSWORD` | `cloudcart123` | Database password |
| `REDIS_HOST` | `localhost` | Redis host |
| `JWT_SECRET` | (configured) | JWT signing secret |

### Service Credentials

| Service | Username | Password |
|---------|----------|----------|
| **Eureka** | admin | admin123 |
| **Config Server** | config | config123 |
| **PostgreSQL** | cloudcart | cloudcart123 |

## 🔧 Development

### Building Individual Services
```bash
cd user-service
mvn clean package
```

### Building Docker Images
```bash
# Build all images
docker-compose build

# Build specific service
docker-compose build user-service
```

### Code Quality
```bash
# Check style
mvn checkstyle:check

# Run static analysis
mvn spotbugs:check
```

## 🏭 Production Considerations

1. **Security**: Replace default secrets with strong, unique values
2. **Database**: Use managed PostgreSQL (AWS RDS, Azure Database, etc.)
3. **Caching**: Use managed Redis (ElastiCache, Azure Cache, etc.)
4. **Secrets Management**: Use Vault or cloud secret managers
5. **Scaling**: Deploy to Kubernetes with HPA
6. **Monitoring**: Set up alerting with Prometheus + Grafana

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Open a Pull Request

---

Built with ❤️ for DevOps portfolio demonstration
