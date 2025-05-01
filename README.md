# Lifetree App API

A domain-driven design (DDD) REST API for managing requirements and results tracking, built with Ktor and Kotlin.

## Project Overview

Lifetree is a requirements and results management system that demonstrates clean architecture principles:

- **Domain Layer**: Contains core business entities, repositories, and services
- **Application Layer**: Provides use case implementations and DTOs
- **Infrastructure Layer**: Implements persistence, security, and external integrations
- **Presentation Layer**: Handles HTTP requests with Ktor controllers and routes

## Technology Stack

- **Kotlin 2.1.10** - Modern JVM language with concise syntax and null safety
- **Ktor 3.1.2** - Lightweight asynchronous web framework
- **Exposed 0.41.1** - Type-safe SQL framework for Kotlin
- **PostgreSQL** - Relational database for persistence
- **Koin 3.4.3** - Lightweight dependency injection framework
- **JWT Authentication** - Secure API access with token-based authentication
- **Docker & Docker Compose** - Containerization for local development

## Getting Started

### Prerequisites

- JDK 17 or higher
- Docker and Docker Compose
- PostgreSQL client (optional)

### Setup & Running

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/lifetree-app-api.git
cd lifetree-app-api
```

2. **Start the PostgreSQL database with Docker**

```bash
docker-compose up -d
```

3. **Build and run the application**

```bash
./gradlew run
```

The server will start on port 8081. You can access the API at `http://localhost:8081/api`.

### Configuration

Application settings are located in `src/main/resources/application.conf`. Environment variables can override these settings:

- `PORT` - Server port (default: 8081)
- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USER` - Database username
- `DATABASE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT token generation

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate and receive JWT token

### Requirements
- `GET /api/requirements` - List all requirements
- `GET /api/requirements/{id}` - Get a specific requirement
- `POST /api/requirements` - Create a new requirement
- `PUT /api/requirements/{id}` - Update a requirement
- `DELETE /api/requirements/{id}` - Delete a requirement

### Results
- `GET /api/results` - List all results
- `GET /api/results/{id}` - Get a specific result
- `GET /api/results/requirement/{requirementId}` - Get results for a requirement
- `POST /api/results` - Create a new result
- `PUT /api/results/{id}` - Update a result
- `DELETE /api/results/{id}` - Delete a result

### Users
- `GET /api/users/me` - Get current user details
- `PUT /api/users/me` - Update current user profile
- `GET /api/admin/users` - List all users (admin only)

## Testing

API endpoints can be tested using the HTTP request files in the project root:

- `health-misc-api-tests.http` - Basic health check and system info
- `requirements-api-tests.http` - Requirements API tests
- `results-api-tests.http` - Results API tests
- `users-api-tests.http` - User authentication and profile tests

You can run these tests using IntelliJ IDEA's HTTP Client plugin.

## Building for Production

Build an executable JAR file:

```bash
./gradlew buildFatJar
```

Run the JAR file:

```bash
java -jar build/libs/lifetree-app-api-0.0.1-all.jar
```

## Database Schema

The database schema is defined in `schema.sql` and includes tables for:

- Users
- Requirements
- Results
- Tags (for categorization)
- Audit logs (for change tracking)

## Project Structure

The codebase follows a clean architecture approach:

- `domain` - Business entities and logic
  - `model` - Aggregates, entities and value objects
  - `repository` - Repository interfaces
  - `service` - Domain services
- `application` - Application services and DTOs
  - `dto` - Data transfer objects
  - `mapper` - Object mapping utilities
  - `service` - Application services
- `infrastructure` - Technical implementations
  - `config` - Application configuration
  - `persistence` - Database access
  - `security` - Authentication and authorization
- `presentation` - API controllers and routes

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.