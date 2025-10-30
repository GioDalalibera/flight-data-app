# Flight Data App

This application provides a REST API for managing flight data, demonstrating a typical CRUD architecture built with Spring Boot and Java 21.

## Tech Stack
- Java 21
- Spring Boot 3.5.7
- Maven
- PostgreSQL + Flyway (for migrations)
- Hibernate
- Springdoc OpenAPI (Swagger UI)
- Testing: JUnit 5
- Dev Tools: Lombok, Spring Boot Actuator, Jakarta Bean Validation, MapStruct
- Docker

## Requirements
In order to properly run this app, I'm assuming the environment is properly configured with: 
- Java Development Kit 21
- Git: Any recent version to clone the repo
- Docker: Latest stable engine. **Only if running PostgreSQL via containers.**
- PostgreSQL: **Only if not using Docker and you want a local DB.**

# Running the app (Windows/Powershell)
Open a terminal in the root of the project and run the following commands, according to desired environment:

## Dev
```
docker compose --env-file .dev.env -f docker-compose-dev.yaml up -d
.\scripts\run-windows.ps1 dev
```

## Prod
```
docker compose --env-file .env -f docker-compose.yaml up -d
.\scripts\run-windows.ps1 prod
```

\* Note, if execution policy blocks the script in Powershell, you may need to run the following once (per session):
```
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

*These instructions assume windows environment, but can be easily adapted to Linux environment*


## Spring Boot Actuator

Actuator is enabled for health/metrics, and wired by profile.

### Endpoints (URLs)

- **Health:** `GET /actuator/health`
- **Info:** `GET /actuator/info`
- **(Dev only)**: `GET /actuator/metrics`, `GET /actuator/env`, `GET /actuator/beans`, etc.

## OpenAPI/API Usage

This app includes OpenAPI, which automatically generates API documentation via Swagger UI.

### Accessing the docs
**Available only when running with Dev profile**!

Once the app is running, open:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

From there, you can explore and test all available endpoints directly in your browser.

### Endpoints

| Method | Endpoint | Description |
|--------|-----------|--------------|
| **GET** | `/api/flights` | List all flights (supports pagination & sorting) |
| **GET** | `/api/flights/{id}` | Get flight details by ID |
| **POST** | `/api/flights` | Create a new flight  |
| **PUT** | `/api/flights/{id}` | Update an existing flight |
| **DELETE** | `/api/flights/{id}` | Delete a flight by ID |

Flights pagination example: Third 3 page (0 indexed pages), 10 flights, sorted by departure airport code<br>[http://127.0.0.1:8080/api/flights?page=2&size=10&sort=departureAirportCode,asc](http://127.0.0.1:8080/api/flights?page=2&size=10&sort=departureAirportCode,asc)

All requests and responses use **JSON**.
