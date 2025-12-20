# Auth Service (Spring Boot Microservice & Vue Frontend)

A secure authentication microservice (Spring Boot) and modern frontend (Vue 3) for user registration, login, JWT-based authentication, password change, and token refresh. Built with Spring Boot, PostgreSQL, Flyway, Swagger, Vue 3, Pinia, and Tailwind CSS.

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
- Modern Vue 3 frontend with component-based architecture

---

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ and npm (for frontend)

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
- Provide secrets via environment variables or a secrets file (see below).

### 3. Start PostgreSQL with Docker Compose
```sh
docker-compose up -d
```
This will start a PostgreSQL instance on port 5432 with the database `shopdb`.

### 4. Run Database Migrations
Flyway will automatically run migrations on application startup.

### 5. Build and Run the Backend Application

#### Option 1: Using Environment Variable (Recommended)
```sh
export JWT_SECRET="<your-jwt-secret>"
./mvnw spring-boot:run
```

#### Option 2: Using a Secrets File
```sh
./mvnw spring-boot:run -Dspring.config.additional-location=classpath:application-secrets.yml
```

The service will start on port 9090 by default.

### 6. Access Swagger API Docs
Open [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html) or [http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)

---

## Frontend (auth-fe)

The frontend is located in the `auth-fe/` directory and is built with Vue 3, Vite, Pinia, and Tailwind CSS.

### Setup & Run

```sh
cd auth-fe
npm install
npm run dev
```

- The frontend expects the backend API to be running and accessible at the URL specified in `VITE_API_BASE_URL` in `auth-fe/.env`.
- All authentication and token management is handled securely using session storage and Axios interceptors.
- See the frontend `auth-fe/README.md` for more details.

---

## API Endpoints (Backend)

- `POST /api/auth/register` — Register a new user
- `POST /api/auth/login` — Login and receive access/refresh tokens
- `POST /api/auth/refresh-token` — Get new tokens using a refresh token
- `POST /api/auth/change-password` — Change password (JWT required)

See Swagger UI for full details and request/response schemas.

---

## Google OAuth2 Login Setup

### 1. Register OAuth2 Credentials in Google Cloud
- Go to [Google Cloud Console](https://console.cloud.google.com/)
- Create a new project (or select an existing one)
- Navigate to **APIs & Services > Credentials**
- Click **Create Credentials > OAuth client ID**
- Set **Application type** to `Web application`
- Add the following redirect URI:
  - `http://localhost:9090/login/oauth2/code/google`
- After creation, note the **Client ID** and **Client Secret**

### 2. Configure Secrets (Do NOT commit secrets to git)
- Copy `src/main/resources/application-secrets.yml.example` to `src/main/resources/application-secrets.yml`
- Fill in your secrets:
  ```yaml
  spring:
    security:
      oauth2:
        client:
          registration:
            google:
              client-id: <your-client-id>
              client-secret: <your-client-secret>
              scope: profile, email
  jwt:
    secret: <your-jwt-secret>
    expiration: 86400000
    refreshExpiration: 604800000
  ```
- Make sure `src/main/resources/application-secrets.yml` is in `.gitignore` (already configured)

### 3. Reference Secrets in `application.yml`
- The main `application.yml` uses environment variable placeholders:
  ```yaml
  client-id: ${GOOGLE_CLIENT_ID}
  client-secret: ${GOOGLE_CLIENT_SECRET}
  ...
  jwt:
    secret: ${JWT_SECRET}
  ```
- You can set these as environment variables or use the secrets file locally.

### 4. Running with Secrets
- To run locally with the secrets file:
  ```sh
  ./mvnw spring-boot:run -Dspring.config.additional-location=classpath:application-secrets.yml
  ```
- Or set the environment variables in your shell/session.

### 5. Testing Google Login
- Start the app and visit:
  - `http://localhost:9090/oauth2/authorization/google`
- Complete Google login. You will be redirected and receive JWT tokens in the response.

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
- If you see errors about missing secrets, ensure you have set the required environment variables or are using the secrets file as described above.

---

## License
MIT
