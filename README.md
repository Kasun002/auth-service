# Auth Service (Spring Boot Microservice)

A secure authentication microservice for user registration, login, JWT-based authentication, password change, and token refresh. Built with Spring Boot, PostgreSQL, Flyway, and Swagger.

---

## Features
- User registration with role assignment
- Username/password login
- JWT access & refresh tokens
- Token refresh endpoint
- Password change endpoint
- PostgreSQL with Flyway migrations
- Swagger API documentation
- Security best practices (password validation, JWT key size, endpoint protection)

---

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

---

## Step-by-Step Setup

### 1. Clone the Repository
```sh
git clone <your-repo-url>
cd auth-service
```

### 2. Configure Environment
- Edit `src/main/resources/application.yml` if needed (DB credentials, JWT secret, etc).
- Ensure `jwt.secret` is at least 32 characters.

### 3. Start PostgreSQL with Docker Compose
```sh
docker-compose up -d
```
This will start a PostgreSQL instance on port 5432 with the database `shopdb`.

### 4. Run Database Migrations
Flyway will automatically run migrations on application startup.

### 5. Build and Run the Application
```sh
./mvnw clean package
./mvnw spring-boot:run
```
The service will start on port 9090 by default.

### 6. Access Swagger API Docs
Open [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html) or [http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)

---

## API Endpoints

- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive access/refresh tokens
- `POST /api/auth/refresh-token` — Get new tokens using a refresh token
- `POST /api/auth/change-password` — Change password (JWT required)

See Swagger UI for full details and request/response schemas.

---

## Notes
- Default roles seeded: `ADMIN`, `MAKER`, `CHECKER`
- Passwords must be at least 8 chars, with upper/lowercase, digit, and special character
- All sensitive endpoints are JWT-protected
- Update `jwt.secret` in `application.yml` for production

---

## Troubleshooting
- If you see DB connection errors, ensure Docker PostgreSQL is running and credentials match
- If you see JWT errors, check your `jwt.secret` and token format
- For Flyway errors, check migration scripts in `src/main/resources/db/migration/`

---

## License
MIT
