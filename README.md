# Fleet Management System

A REST API application for managing a vehicle fleet, with a simple Angular frontend. The system allows creating, reading, updating and deleting vehicles, as well as tracking HTTP request statistics.

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.3
- Spring Data JPA + Hibernate
- Spring Security (HTTP Basic Auth)
- PostgreSQL 16 + Flyway migrations
- springdoc-openapi (Swagger UI)

**Frontend**
- Angular 17 (standalone components)
- TypeScript, SCSS

**Infrastructure**
- Docker, Docker Compose

---

## Running the Application

### Option 1 — Docker (recommended)

The only requirement is Docker with Docker Compose installed.

```bash
git clone https://github.com/michal-sitek/fleet-management-system.git
cd fleet-management-system
docker compose up --build
```

This will start three containers:
- `fleet-db` — PostgreSQL database
- `fleet-service` — Spring Boot backend
- `fleet-gui` — Angular frontend served by nginx

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8081 |
| Swagger UI | http://localhost:8081/swagger-ui/index.html |

To stop all containers:
```bash
docker compose down
```

To stop and remove all data (database volume):
```bash
docker compose down -v
```

---

### Option 2 — Local Development

**Requirements:** Java 21, Maven, Node.js 20, Docker (for the database only)

**1. Start the database**
```bash
docker compose up fleet-db
```

**2. Start the backend**
```bash
cd fleet-service
./mvnw spring-boot:run
```

The backend starts on http://localhost:8081. Flyway will automatically create the database schema on first run.

**3. Start the frontend**
```bash
cd gui
npm install
ng serve
```

The frontend starts on http://localhost:4200.

---

## Authentication

All API endpoints are secured with HTTP Basic Auth.

| Username | Password |
|---|---|
| admin | admin |

Include the `Authorization` header in every request:
```
Authorization: Basic YWRtaW46YWRtaW4=
```

The frontend handles authentication automatically. Credentials can be changed in the top-right corner of the UI and are stored in `localStorage`.

---

## API Reference

Full interactive documentation is available in Swagger UI:
**http://localhost:8081/swagger-ui/index.html**

### Vehicles

| Method | Endpoint | Description |
|---|---|---|
| GET | /vehicles | List vehicles |
| GET | /vehicles/{id} | Get vehicle by ID |
| GET | /vehicles/by-vin/{vin} | Get vehicle by VIN |
| GET | /vehicles/by-plate/{plateNumber} | Get vehicle by plate number |
| POST | /vehicles | Create a vehicle |
| PUT | /vehicles/{id} | Update a vehicle |
| DELETE | /vehicles/{id} | Delete a vehicle |

**Query parameters for `GET /vehicles`:**

| Parameter | Type | Default | Description |
|---|---|---|---|
| q | string | — | Search across plate number, VIN, brand and model |
| page | int | 0 | Page number (0-indexed) |
| size | int | 20 | Page size |
| sort | string | createdAt,desc | Sort field and direction |

**Vehicle fields:**

| Field | Type | Validation |
|---|---|---|
| plateNumber | string | required, max 20 characters, unique |
| vin | string | required, exactly 17 characters, unique |
| brand | string | required, max 100 characters |
| model | string | required, max 100 characters |
| year | int | required, between 1900 and 2100 |
| status | enum | required, one of: `ACTIVE`, `IN_SERVICE`, `SOLD` |

### Statistics

| Method | Endpoint | Description |
|---|---|---|
| GET | /stats/requests | HTTP request statistics |

Returns the total number of requests handled by the application, broken down by endpoint and HTTP status code.

---

## Tests

**Requirements:** Docker running with the test database

```bash
# Start the test database
docker compose up fleet-test-db

# Run all tests
cd fleet-service
./mvnw test
```

The test suite includes:
- **Unit tests** (`VehicleControllerTest`) — controller layer tested with a mocked service, covers validation errors, 404, 409 conflict and successful responses
- **Integration tests** (`VehicleIntegrationTest`) — full stack tests against a real PostgreSQL database, covers the complete CRUD lifecycle

Coverage report (JaCoCo) is generated at:
```
fleet-service/target/site/jacoco/index.html
```

---

## Environment Variables

Defined in `.env` at the project root:

| Variable | Default | Description |
|---|---|---|
| POSTGRES_USER | fleet_user | Database username |
| POSTGRES_PASSWORD | fleet_pass | Database password |
| FLEET_DB_NAME | fleet_db | Main database name |
| FLEET_DB_PORT | 5433 | Main database port on host |
| FLEET_TEST_DB_NAME | fleet_test_db | Test database name |
| FLEET_TEST_DB_PORT | 5435 | Test database port on host |

---

## Project Structure

```
fleet-management-system/
├── fleet-service/                  # Spring Boot backend
│   ├── src/main/java/.../
│   │   ├── vehicle/                # Vehicle CRUD (controller, service, repository)
│   │   ├── stats/                  # Request statistics
│   │   ├── common/error/           # Global exception handler
│   │   ├── config/                 # Security and OpenAPI configuration
│   └── src/main/resources/
│       ├── application.yaml
│       └── db/migration/           # Flyway SQL migrations
├── gui/                            # Angular frontend
│   └── src/app/
│       ├── vehicles/               # Vehicle list and form components
│       ├── stats/                  # Statistics component
│       ├── models.ts               # TypeScript interfaces
│       └── auth.interceptor.ts     # HTTP Basic Auth interceptor
└── docker-compose.yml
```
