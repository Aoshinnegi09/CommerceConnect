# Training guide

## 1. Start the database

```bash
docker compose up -d postgres
```

## 2. Run the application

```bash
mvn spring-boot:run
```

## 3. Access Swagger

```text
http://localhost:8080/swagger-ui.html
```

## 4. Use the app

- Register a user
- Log in to get a JWT token
- Create a customer profile
- Create a category and product
- Place an order
- Check dashboard summary
