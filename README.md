# SweetStory Backend

REST API for the SweetStory sweets shop: user accounts (JWT in a cookie), a product catalogue and orders, with an admin area for managing products and orders.

## Tech stack

Java 21, Spring Boot 4.1.2 (Web MVC, Data JPA, Security, Validation, Actuator), PostgreSQL, JJWT, Lombok, Maven (wrapper included).

## Prerequisites

- JDK 21
- A running PostgreSQL database (default: `localhost:5432/cake_db`)

## Configuration

Everything is set through environment variables; the defaults live in `src/main/resources/application.yaml`.

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `8080` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/cake_db` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | `postgres` / `postgres` | Database credentials |
| `DDL_AUTO` | `update` | Hibernate schema strategy |
| `JWT_SECRET` | dev-only value | JWT signing key - **always override in production** |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Token lifetime |
| `JWT_COOKIE_SECURE` | `false` | Set `true` behind HTTPS |
| `JWT_COOKIE_SAME_SITE` | `Lax` | Token cookie SameSite policy |
| `CORS_ALLOWED_ORIGINS` | localhost:3000, localhost:5173, the deployed frontend | Comma-separated allowed origins |

## Run

```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
./mvnw test                 # run the tests
./mvnw package              # build target/*.jar
```

### Docker

```bash
docker build -t sweetstory-backend .
docker run -p 8080:8080 -e DB_URL=jdbc:postgresql://host:5432/cake_db \
  -e DB_USERNAME=... -e DB_PASSWORD=... -e JWT_SECRET=... sweetstory-backend
```

The image is a multi-stage build and runs as a non-root user.

## API overview

| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login`, `/api/auth/logout` | public |
| PATCH / DELETE | `/api/users/me` | authenticated |
| GET | `/api/products`, `/api/products/{id}` | public |
| POST | `/api/admin/products` | admin |
| PUT / DELETE | `/api/admin/products/{id}` | admin |
| POST | `/api/orders` | authenticated |
| GET | `/api/orders/my` | authenticated |
| GET | `/api/admin/orders` | admin |
| PATCH | `/api/admin/orders/{id}/status` | admin |
| DELETE | `/api/admin/orders/{id}` | admin |

All `/api/admin/**` routes require the `ADMIN` role. Actuator endpoints are provided by Spring Boot Actuator.

## Project structure

`src/main/java/com/shop/SweetStory/`: `auth` (register/login, users), `catalog` (products), `order` (orders), `common` (security and shared config).
