# Gas Station API

REST backend for a multi-station gas station chain. It serves public station information (location, fuel prices, fuel levels) to customers and provides a back-office API for corporate admins and station managers.

The full product specification is in [`requirement.md`](requirement.md).

## Tech stack

- Java 21, Spring Boot 4
- Spring Web, Spring Data JPA, Spring Security, Bean Validation
- JWT authentication (jjwt 0.12)
- PostgreSQL (runtime), H2 (tests)
- Lombok
- springdoc-openapi (Swagger UI)
- Gradle

## Project structure

```
src/main/java/com/station/gas/
├── GasApplication.java      # Spring Boot entry point
├── bean/                    # Auth request/response DTOs
├── controller/              # REST controllers (public, auth, manager, admin)
├── model/                   # JPA entities
├── repository/              # Spring Data repositories
└── security/                # JWT filter, JWT utils, security config
src/main/resources/
├── application.properties   # Runtime config (Postgres, JWT, port)
└── import.sql               # Sample data
```

## Domain model

| Entity        | Table           | Description                                                        |
|---------------|-----------------|--------------------------------------------------------------------|
| `User`        | `users`         | Back-office user with role `CORPORATE_ADMIN` or `STATION_MANAGER`  |
| `Station`     | `stations`      | A gas station (name, slug, address, coordinates, contact info)     |
| `StationUser` | `station_users` | Assigns a manager to a station                                     |
| `FuelType`    | `fuel_types`    | Fuel sold at a station, with tank capacity and low-level threshold |
| `FuelPrice`   | `fuel_prices`   | Price history per fuel type; source `API` or `MANUAL`              |
| `FuelLevel`   | `fuel_levels`   | Tank volume history per fuel type; source `API` or `MANUAL`        |

The current price and level of a fuel type are the most recent `FuelPrice` / `FuelLevel` rows by `recordedAt`.

## Getting started

### Prerequisites

- JDK 21
- PostgreSQL running on `localhost:5432`

### Database

Create the database (default credentials are `postgres` / `postgres`):

```bash
createdb -U postgres gas_station
```

Hibernate creates and updates the tables on startup (`spring.jpa.hibernate.ddl-auto=update`).

### Run

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080`.

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Test

```bash
./gradlew test
```

Tests use the in-memory H2 database configured in `src/test/resources/application-test.properties`.

### Build

```bash
./gradlew build
java -jar build/libs/gas-0.0.1-SNAPSHOT.jar
```

## Configuration

Settings live in `src/main/resources/application.properties`:

| Property                     | Default                                         | Description                          |
|------------------------------|-------------------------------------------------|--------------------------------------|
| `spring.datasource.url`      | `jdbc:postgresql://localhost:5432/gas_station`  | Database URL                         |
| `spring.datasource.username` | `postgres`                                      | Database user                        |
| `spring.datasource.password` | `postgres`                                      | Database password                    |
| `jwt.secret`                 | (development key)                               | Base64-encoded HMAC key, ≥ 256 bits  |
| `jwt.expiration`             | `3600000`                                       | Token lifetime in milliseconds (1 h) |
| `server.port`                | `8080`                                          | HTTP port                            |

Any property can be overridden with an environment variable, for example `SPRING_DATASOURCE_URL` or `JWT_SECRET`. **Replace `jwt.secret` and the database password before deploying anywhere other than a local machine.**

CORS allows `http://localhost:5173` and `http://localhost:5174` (Vite dev servers). Change this in `security/SecurityConfig.java`.

## Sample data

`src/main/resources/import.sql` contains two users, two stations (Bangkok and Chiang Mai), fuel types, prices, and levels. Both users have the password `password`:

| Email                 | Role              |
|-----------------------|-------------------|
| `admin@station.com`   | `CORPORATE_ADMIN` |
| `manager@station.com` | `STATION_MANAGER` (assigned to Bangkok Central) |

Hibernate runs `import.sql` automatically only when `ddl-auto` is `create` or `create-drop`. With the default `update`, load it manually, e.g. `psql -U postgres -d gas_station -f src/main/resources/import.sql`.

## Authentication

Log in to get a JWT, then send it as a bearer token:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@station.com","password":"password"}'
# => {"token":"<jwt>"}

curl http://localhost:8080/api/v1/admin/stations \
  -H 'Authorization: Bearer <jwt>'
```

### Access rules

| Path                 | Access                                   |
|----------------------|------------------------------------------|
| `/api/v1/auth/**`    | Public                                   |
| `/api/v1/public/**`  | Public                                   |
| `/api/v1/manager/**` | `STATION_MANAGER` or `CORPORATE_ADMIN`   |
| `/api/v1/admin/**`   | `CORPORATE_ADMIN`                        |

Station managers can only access stations they are assigned to; corporate admins can access every station.

## API endpoints

### Auth

| Method | Path                  | Description              |
|--------|-----------------------|--------------------------|
| POST   | `/api/v1/auth/login`  | Log in, returns a JWT    |

### Public

| Method | Path                              | Description                                         |
|--------|-----------------------------------|-----------------------------------------------------|
| GET    | `/api/v1/public/stations`         | List stations (summary with coordinates)            |
| GET    | `/api/v1/public/stations/{slug}`  | Station details with current fuel prices and levels |

### Manager

| Method | Path                                                         | Description                                         |
|--------|--------------------------------------------------------------|-----------------------------------------------------|
| GET    | `/api/v1/manager/stations`                                   | Stations assigned to the current user               |
| GET    | `/api/v1/manager/stations/{stationId}`                       | Station details                                     |
| PUT    | `/api/v1/manager/stations/{stationId}`                       | Update station profile                              |
| GET    | `/api/v1/manager/stations/{stationId}/fuel-board`            | Current price and level for every active fuel type  |
| POST   | `/api/v1/manager/stations/{stationId}/prices`                | Manually set a price (`{fuelTypeId, price}`)        |
| POST   | `/api/v1/manager/stations/{stationId}/fuel-levels`           | Manually set a level (`{fuelTypeId, volumeLitres}`) |
| GET    | `/api/v1/manager/stations/{stationId}/fuel-types`            | List fuel types                                     |
| POST   | `/api/v1/manager/stations/{stationId}/fuel-types`            | Create a fuel type                                  |
| PUT    | `/api/v1/manager/stations/{stationId}/fuel-types/{fuelTypeId}` | Update a fuel type                                |

### Admin

| Method | Path                          | Description                       |
|--------|-------------------------------|-----------------------------------|
| GET    | `/api/v1/admin/stations`      | List all stations                 |
| POST   | `/api/v1/admin/stations`      | Create a station                  |
| GET    | `/api/v1/admin/stations/{id}` | Get a station                     |
| PUT    | `/api/v1/admin/stations/{id}` | Update a station                  |
| GET    | `/api/v1/admin/users`         | List users                        |
| POST   | `/api/v1/admin/users`         | Create a user                     |
| POST   | `/api/v1/admin/users/assign`  | Assign a manager to a station (`{userId, stationId}`) |

See Swagger UI for full request and response schemas.
