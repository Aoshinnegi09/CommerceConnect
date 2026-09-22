# CommerceConnect

CommerceConnect is a Java and Spring Boot portfolio project built for a Deloitte-aligned engineering role. It demonstrates enterprise commerce workflow, database modeling, secure API design, inventory handling, marketing promotions, campaign management, and operational auditability.

## Business problem

A modern commerce and marketing platform helps teams manage customer profiles, catalog products, maintain inventory, process orders, run promotions, and launch campaigns. The platform is designed as a modular monolith so it is easy to understand, test locally, and evolve into a distributed service architecture later.

## Tech stack

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Flyway migrations
- Springdoc OpenAPI
- Maven
- Docker + Docker Compose
- JUnit 5 + Mockito

## Core features

- User registration and login
- JWT authentication and role-based authorization
- Customer profile management
- Category and product management
- Product search and listing with filters
- Inventory tracking and stock validation
- Order creation and payment status tracking
- Promotion engine with percentage and fixed discount logic
- Campaign management
- Notification service
- Audit logs
- Dashboard summary endpoint

## Project structure

```text
commerce-connect/
├── src/main/java/com/commerceconnect
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   ├── security
│   ├── service
│   └── CommerceConnectApplication.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
├── src/test/java/com/commerceconnect
├── docs/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .env.example
├── .gitignore
└── README.md
```

## Local setup

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker (optional, for containerized run)
- PostgreSQL (if not using Docker)

### Run with Docker Compose

```bash
docker compose up --build
```

The application will run on:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432

### Run locally without Docker

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/commerceconnect
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
mvn spring-boot:run
```

## Environment variables

See `.env.example`.

## API examples

### Register user

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"John",
    "lastName":"Smith",
    "email":"john@example.com",
    "password":"secret123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john@example.com",
    "password":"secret123"
  }'
```

### Create product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name":"Wireless Mouse",
    "description":"Ergonomic mouse",
    "sku":"WM-001",
    "price":49.99,
    "categoryId":1,
    "active":true
  }'
```

### Place order

```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "customerId":1,
    "items":[{"productId":1,"quantity":2}]
  }'
```

## Testing

```bash
mvn test
```

## Interview talking points

- This project shows strong backend fundamentals in Java and Spring Boot.
- It includes secure authentication, persistent relational data, and business workflows.
- Order placement includes transactional consistency and inventory checks.
- The architecture is easy to explain in interviews and future-ready for service splitting.

## Documentation

- docs/REQUIREMENTS.md
- docs/SYSTEM_DESIGN.md
- docs/TEST_PLAN.md
- docs/TRAINING_GUIDE.md
