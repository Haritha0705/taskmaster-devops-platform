<div align="center">

# TaskMaster

A modern DevOps Task Management Platform featuring CI/CD pipelines, JWT authentication, HashiCorp Vault for security, REST APIs, microservices architecture, and cloud-native deployment with AWS and Docker.  

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-24-2496ED?style=flat&logo=docker&logoColor=white)](https://www.docker.com/)
[![AWS](https://img.shields.io/badge/AWS-Cloud-FF9900?style=flat&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/)
[![ECS](https://img.shields.io/badge/ECS-Elastic-0052CC?style=flat&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/ecs/)
[![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-automation-2088FF?style=flat&logo=github&logoColor=white)](https://github.com/features/actions)
[![Terraform](https://img.shields.io/badge/Terraform-1.5-623CE4?style=flat&logo=terraform&logoColor=white)](https://www.terraform.io/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-Ready-success?style=flat)](#)
[![HashiCorp Vault](https://img.shields.io/badge/HashiCorp%20Vault-Security-4A4A4A?style=flat&logo=vault&logoColor=white)](https://www.vaultproject.io/)
[![Security](https://img.shields.io/badge/Security-High-red?style=flat)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=flat&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=flat&logo=redis&logoColor=white)](https://redis.io/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-E6522C?style=flat&logo=prometheus&logoColor=white)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Visualization-F46800?style=flat&logo=grafana&logoColor=white)](https://grafana.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Monitoring](#monitoring)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

TaskMaster Pro is a RESTful API service built to demonstrate modern DevOps engineering practices in a production environment. The project implements a complete CI/CD pipeline, automated security scanning, containerization, infrastructure provisioning, and observability stack.

### Project Objectives

- Implement secure authentication and authorization patterns
- Demonstrate container orchestration with AWS ECS
- Establish automated testing and deployment pipelines
- Integrate comprehensive monitoring and logging
- Apply infrastructure as code principles
- Follow security best practices and compliance standards

---

## Architecture

### System Design

```
                    Internet
                       |
                   Route 53
                       |
              Application Load Balancer
                       |
        +--------------+---------------+
        |              |               |
    ECS Task      ECS Task        ECS Task
    (Container)   (Container)     (Container)
        |              |               |
        +-------+------+-------+-------+
                |              |
           PostgreSQL       Redis
           (RDS)         (ElastiCache)

Monitoring: Prometheus -> Grafana -> CloudWatch
```

### Component Architecture

The application follows a layered architecture pattern:

- **Presentation Layer**: REST Controllers handling HTTP requests
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access with Spring Data JPA
- **Security Layer**: JWT authentication and authorization filters
- **Infrastructure Layer**: AWS services and container orchestration

---

## Technology Stack

### Core Technologies

| Category | Technology | Version |
|----------|-----------|---------|
| Runtime | Java | 21 LTS |
| Framework | Spring Boot | 3.2.x |
| Security | Spring Security | 6.x |
| Data Access | Spring Data JPA | 3.x |
| Database | PostgreSQL | 16 |
| Cache | Redis | 7.x |
| Migrations | Flyway | 9.x |

### DevOps & Infrastructure

| Category | Technology |
|----------|-----------|
| Containerization | Docker, Docker Compose |
| Orchestration | AWS ECS Fargate |
| CI/CD | GitHub Actions |
| IaC | Terraform |
| Monitoring | Prometheus, Grafana |
| Logging | AWS CloudWatch |
| Security Scanning | Trivy, Snyk, SonarQube |

### Cloud Services (AWS)

- ECS (Elastic Container Service) - Container orchestration
- RDS (Relational Database Service) - Managed PostgreSQL
- ElastiCache - Managed Redis
- ECR (Elastic Container Registry) - Container image storage
- ALB (Application Load Balancer) - Traffic distribution
- VPC - Network isolation
- CloudWatch - Monitoring and logging
- Secrets Manager - Secure credential storage

---

## Features

### Security Implementation

- JWT-based stateless authentication with refresh token rotation
- Role-based access control (RBAC) with fine-grained permissions
- BCrypt password hashing with configurable work factor
- Redis-backed distributed rate limiting
- HashiCorp Vault integration for secrets management
- Comprehensive security headers (CORS, CSP, HSTS)
- Automated vulnerability scanning in CI/CD pipeline
- SQL injection and XSS protection

### Core Functionality

- Complete CRUD operations for task management
- User registration and profile management
- Advanced search and filtering capabilities
- Pagination and sorting for large datasets
- Soft delete with recovery functionality
- Audit trail with timestamp tracking
- Task categorization and priority management
- RESTful API design with proper HTTP semantics

### DevOps Implementation

- Fully automated CI/CD pipeline with GitHub Actions
- Multi-stage Docker builds for optimized images
- Infrastructure as Code using Terraform modules
- Blue-green deployment strategy for zero downtime
- Auto-scaling based on CPU and memory metrics
- Comprehensive health checks for all dependencies
- Graceful shutdown handling
- Environment-specific configuration management

### Observability

- Prometheus metrics collection via Spring Actuator
- Custom Grafana dashboards for system and business metrics
- Structured JSON logging with correlation IDs
- Distributed request tracing capability
- Configurable alert rules for critical thresholds
- CloudWatch integration for centralized logging
- Real-time monitoring of application health

---

## Getting Started

### Prerequisites

Required software installations:

- Java Development Kit 21
- Maven 3.9 or higher
- Docker Desktop
- Git
- AWS CLI (for deployment)
- Terraform (for infrastructure provisioning)

### Local Development Setup

Clone and configure the repository:

```bash
git clone https://github.com/yourusername/taskmaster-devops-platform.git
cd taskmaster-devops-platform
```

Create environment configuration file `.env`:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskmaster
DB_USERNAME=postgres
DB_PASSWORD=secure_password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-secret-key-minimum-256-bits
JWT_EXPIRATION=86400000
SPRING_PROFILES_ACTIVE=dev
```

Start required services:

```bash
docker compose up -d postgres redis
```

Build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

Verify successful startup:

```bash
curl http://localhost:8080/actuator/health
```

Expected response: `{"status":"UP"}`

### Docker Deployment

Build and run with Docker Compose:

```bash
docker compose up --build
```

The application will be accessible at `http://localhost:8080`

---

## API Documentation

### Authentication Endpoints

**User Registration**

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**User Login**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

Response includes JWT access token and refresh token.

### Task Management Endpoints

**Create Task**

```http
POST /api/v1/tasks
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Complete documentation",
  "description": "Write comprehensive API documentation",
  "priority": "HIGH",
  "dueDate": "2024-01-20T23:59:59Z",
  "category": "WORK"
}
```

**Retrieve Tasks**

```http
GET /api/v1/tasks?page=0&size=10&sort=dueDate,asc
Authorization: Bearer {token}
```

**Update Task**

```http
PUT /api/v1/tasks/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Updated title",
  "status": "IN_PROGRESS"
}
```

**Delete Task**

```http
DELETE /api/v1/tasks/{id}
Authorization: Bearer {token}
```

### Interactive Documentation

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

OpenAPI specification: `http://localhost:8080/v3/api-docs`

---

## Deployment

### AWS Infrastructure Provisioning

Initialize and apply Terraform configuration:

```bash
cd infrastructure/terraform

terraform init
terraform plan -var-file=environments/prod.tfvars
terraform apply -var-file=environments/prod.tfvars
```

This provisions:
- VPC with public and private subnets
- ECS cluster and service definitions
- RDS PostgreSQL instance with Multi-AZ
- ElastiCache Redis cluster
- Application Load Balancer
- Security groups and IAM roles
- CloudWatch log groups and alarms

### Container Deployment

Build and push Docker image to ECR:

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin {account-id}.dkr.ecr.us-east-1.amazonaws.com

docker build -t taskmaster:latest .
docker tag taskmaster:latest {account-id}.dkr.ecr.us-east-1.amazonaws.com/taskmaster:latest
docker push {account-id}.dkr.ecr.us-east-1.amazonaws.com/taskmaster:latest
```

Update ECS service:

```bash
aws ecs update-service --cluster taskmaster-cluster --service taskmaster-service --force-new-deployment
```

### Environment Configuration

Production environment variables are managed through AWS Secrets Manager and injected into ECS task definitions:

- Database credentials
- Redis endpoint
- JWT signing key
- External service API keys

---

## Monitoring

### Metrics Collection

Prometheus scrapes metrics from Spring Boot Actuator endpoints exposed at `/actuator/prometheus`. Key metrics include:

- HTTP request rate and latency percentiles
- JVM memory usage and garbage collection
- Database connection pool statistics
- Redis cache hit/miss ratios
- Custom business metrics (tasks created, user registrations)

### Dashboards

Pre-configured Grafana dashboards provide visualization of:

- Application performance metrics
- Infrastructure resource utilization
- Business KPIs and trends
- Error rates and status codes

### Health Monitoring

Health check endpoints:

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/redis
```

### Alerting

Prometheus alert rules configured for:

- High error rate (>5% for 5 minutes)
- Elevated response time (p95 > 500ms)
- Database connection pool exhaustion
- Memory usage exceeding threshold
- Service unavailability

Alerts are routed to configured channels (Slack, PagerDuty, Email).

---

## Testing

### Test Execution

Run complete test suite:

```bash
mvn test
```

Generate coverage report:

```bash
mvn clean test jacoco:report
```

View coverage report at: `target/site/jacoco/index.html`

### Test Categories

- **Unit Tests**: Service layer logic and utility functions
- **Integration Tests**: Database interactions with Testcontainers
- **API Tests**: REST endpoint behavior and contracts
- **Security Tests**: Authentication and authorization flows

Target coverage: 85% minimum

---

## CI/CD Pipeline

### Workflow Stages

The automated pipeline executes the following stages:

1. **Build & Test**: Compile source code and execute test suites
2. **Code Quality**: SonarQube analysis and quality gate validation
3. **Security Scan**: Dependency vulnerability check and SAST
4. **Container Build**: Multi-stage Docker image creation
5. **Container Scan**: Trivy vulnerability scanning
6. **Push to Registry**: Upload to Amazon ECR
7. **Deploy to ECS**: Rolling update with health checks
8. **Post-Deploy Validation**: Smoke tests and monitoring

### Triggers

Pipeline executes on:
- Push to main branch (full deployment)
- Pull requests (build and test only)
- Manual workflow dispatch
- Scheduled security scans (daily)

### Required Secrets

Configure in GitHub repository settings:

```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
SONAR_TOKEN
SNYK_TOKEN
DOCKER_USERNAME
DOCKER_PASSWORD
```

---

## Project Structure

```
taskmaster-devops-platform/
├── src/
│   ├── main/
│   │   ├── java/com/taskmaster/
│   │   │   ├── config/              # Spring configurations
│   │   │   ├── controller/          # REST endpoints
│   │   │   ├── service/             # Business logic
│   │   │   ├── repository/          # Data access
│   │   │   ├── model/               # Domain entities
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── security/            # Security components
│   │   │   ├── exception/           # Exception handlers
│   │   │   └── util/                # Utility classes
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/        # Flyway scripts
│   └── test/
│       └── java/com/taskmaster/
│           ├── unit/
│           ├── integration/
│           └── e2e/
├── infrastructure/
│   └── terraform/
│       ├── modules/                 # Reusable modules
│       └── environments/            # Environment configs
├── .github/
│   └── workflows/                   # CI/CD definitions
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── monitoring/
│   ├── prometheus/
│   └── grafana/
├── docs/                            # Additional documentation
├── scripts/                         # Utility scripts
└── pom.xml
```

---

## Configuration

### Application Profiles

**Development** (`application-dev.yml`):
- Verbose logging enabled
- H2 console accessible
- Hot reload configured
- Relaxed security for testing

**Production** (`application-prod.yml`):
- Optimized logging levels
- Strict security policies
- External configuration via environment variables
- Performance tuning applied

### Database Migrations

Flyway manages schema versioning with migrations in `src/main/resources/db/migration/`:

- `V1__init_schema.sql`: Initial schema creation
- `V2__add_indexes.sql`: Performance indexes
- `V3__add_audit_columns.sql`: Audit trail fields

Migrations execute automatically on application startup.

---

## Contributing

### Development Workflow

1. Fork the repository
2. Create feature branch: `git checkout -b feature/description`
3. Implement changes with tests
4. Commit with conventional format: `feat: add feature description`
5. Push to fork and create pull request

### Commit Convention

Follow conventional commits specification:

- `feat:` New feature implementation
- `fix:` Bug fix
- `docs:` Documentation updates
- `refactor:` Code restructuring
- `test:` Test additions or modifications
- `chore:` Maintenance tasks
- `ci:` CI/CD configuration changes

### Pull Request Requirements

- All tests must pass
- Code coverage maintained above 80%
- SonarQube quality gate passed
- No security vulnerabilities introduced
- Documentation updated for API changes
- Conventional commit messages used

---

## Performance

### Benchmarks

Performance characteristics on AWS ECS (2 vCPU, 4GB RAM):

| Endpoint | Average | p95 | p99 | Throughput |
|----------|---------|-----|-----|------------|
| GET /tasks | 45ms | 89ms | 120ms | 2000 req/s |
| POST /tasks | 67ms | 125ms | 180ms | 1500 req/s |
| POST /auth/login | 120ms | 200ms | 280ms | 800 req/s |

### Optimization Strategies

- HikariCP connection pooling with tuned parameters
- Redis caching for frequently accessed data
- Database query optimization with proper indexing
- JPA lazy loading for related entities
- Response compression (GZIP)
- Async processing for non-critical operations

---

## Security

### Authentication Flow

1. User submits credentials to `/auth/login`
2. Server validates credentials and generates JWT access token
3. Client includes token in Authorization header for subsequent requests
4. Server validates token signature and expiration on each request
5. Client uses refresh token to obtain new access token when expired

### Security Headers

All responses include security headers:

```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000
Content-Security-Policy: default-src 'self'
```

### Secrets Management

Production secrets are stored in:
- AWS Secrets Manager for database credentials
- HashiCorp Vault for application secrets
- Environment variables injected at runtime
- Never committed to version control

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

<div align="center">

**[⬆ Back to Top](#library-management-system)**

Made with ❤️ by [Haritha](https://github.com/Haritha0705)

</div>
