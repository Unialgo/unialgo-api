# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build and Development
- **Build**: `./mvnw clean install`
- **Run application**: `./mvnw spring-boot:run`
- **Run tests**: `./mvnw test`
- **Run single test**: `./mvnw test -Dtest=ClassNameTest`
- **Package**: `./mvnw package`

### Docker Services
- **Start services**: `docker-compose up -d` (MySQL + Keycloak)
- **Stop services**: `docker-compose down`

### Access Points
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/public/swagger.html
- **Keycloak Admin**: http://localhost:8180 (admin/unialgo)

## Architecture Overview

**UniAlgo** is a Spring Boot 3.4.4 + Java 21 programming education platform with 5 core modules following a layered architecture pattern:

### Core Modules
1. **AI** (`/ai/`) - AI-powered question statement generation using OpenAI/DeepSeek
2. **Assignment** (`/assignment/`) - Assignment and question management
3. **Judge0** (`/judge0/`) - Code execution service integration
4. **Question** (`/question/`) - Question and test case management
5. **User** (`/user/`) - Authentication, authorization, and user management

### Architecture Pattern
Each module follows: `Controller → Service → Repository → Entity`
- **Controllers**: HTTP handling, security annotations (`@PreAuthorize`)
- **Services**: Business logic, orchestration
- **Repositories**: Spring Data JPA repositories
- **Entities**: JPA entities with relationships

## Key Technologies & Patterns

### Security & Authentication
- **OAuth2 JWT Resource Server** with Keycloak
- **Roles**: `ROLE_STUDENT`, `ROLE_TEACHER` extracted from JWT `resource_access.unialgo.roles`
- **Public paths**: `/public/**` (no auth required)
- **User sync**: Local database synchronized with Keycloak on login/signup

### Database
- **MySQL** with **Liquibase** migrations
- **Migration pattern**: `/db/changelog/changelogs/YYYY/MM/DD-NN-changelog.xml`
- **Entity relationships**: User→Teacher/Student (1:1), Teacher→Questions/Assignments (1:many), Assignment↔Question (many:many via QuestionAssignment)

### External Services
- **Judge0**: Code execution via custom RestTemplate with timeout configuration
- **AI Service**: OpenAI-compatible API (DeepSeek) for Portuguese problem statements
- **Configuration classes**: `Judge0Config`, `AIConfig`, `SecurityConfig`

## Development Conventions

### Role Strategy Pattern
The user module implements Strategy pattern for role-specific logic:
- `RoleStrategy` interface
- `StudentRoleStrategy` and `TeacherRoleStrategy` implementations  
- `RoleStrategyFactory` for strategy selection

### DTOs and Validation
- Consistent DTO usage for request/response
- Request DTOs: `SaveQuestionRequestDto`, `LoginRequestDto`
- Response DTOs: `GenerateStatementResponseDto`, `Judge0StatusResponseDto`

### Principal-Based Security
- Controllers use `Principal` parameter for authenticated user access
- User ID extracted from JWT subject claim
- Method-level authorization with `@PreAuthorize`

### Configuration Management
Key application properties structure:
```properties
# Database
spring.datasource.* (MySQL)
spring.jpa.hibernate.ddl-auto=none

# Security
spring.security.oauth2.resourceserver.jwt.issuer-uri
keycloak.* (client credentials)

# External APIs  
openai.api.* (AI service)
judge0.api.* (code execution)
```

## Important Notes

- **Dependency Injection**: Constructor injection throughout (no `@Autowired` fields)
- **Exception Handling**: Custom `Judge0Exception`, standard Spring exceptions
- **Lazy Loading**: JPA fetch strategies for performance
- **Timeout Configuration**: External API calls have proper timeout handling
- **Liquibase**: May need manual execution if not auto-initialized
- **Keycloak**: Configuration may need updates if signup issues occur