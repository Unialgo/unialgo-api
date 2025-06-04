# Judge0 Integration Guide

This document provides a comprehensive overview of the Judge0 integration implementation in the UniAlgo API.

## Overview

The Judge0 integration allows students to submit code solutions that are automatically executed and evaluated against test cases. The implementation follows SOLID principles and integrates seamlessly with the existing architecture.

## Architecture Components

### 1. Core Entities

#### Submission Entity
- **Location**: `src/main/java/com/ua/unialgo/submission/entity/Submission.java`
- **Purpose**: Represents a code submission with execution results
- **Key Fields**:
  - `user`: Reference to the submitting user
  - `question`: Reference to the question being solved
  - `sourceCode`: The submitted code
  - `languageId`: Judge0 language identifier
  - `status`: Current submission status
  - `score`: Calculated score based on test case results
  - `judge0Token`: Token for tracking in Judge0

#### SubmissionStatus Enum
- **Location**: `src/main/java/com/ua/unialgo/submission/entity/SubmissionStatus.java`
- **Purpose**: Maps Judge0 status codes to meaningful enum values
- **Key Statuses**: PENDING, IN_QUEUE, PROCESSING, ACCEPTED, WRONG_ANSWER, etc.

### 2. Service Layer

#### SubmissionService
- **Location**: `src/main/java/com/ua/unialgo/submission/service/SubmissionService.java`
- **Purpose**: Manages submission lifecycle and business logic
- **Key Methods**:
  - `submitCode()`: Creates and processes new submissions
  - `evaluateSubmission()`: Runs code against test cases
  - `updateSubmissionStatus()`: Updates status from Judge0

#### SubmissionPollingService
- **Location**: `src/main/java/com/ua/unialgo/submission/service/SubmissionPollingService.java`
- **Purpose**: Scheduled service for updating submission statuses
- **Schedule**: Runs every 10 seconds to check for updates

### 3. Controller Layer

#### SubmissionController
- **Location**: `src/main/java/com/ua/unialgo/submission/controller/SubmissionController.java`
- **Purpose**: REST endpoints for submission management
- **Key Endpoints**:
  - `POST /submissions`: Submit code via JSON
  - `POST /submissions/upload`: Submit code via file upload
  - `GET /submissions/{id}`: Get submission details
  - `GET /submissions/user`: Get user submissions
  - `PUT /submissions/{id}/status`: Force status update

### 4. Data Transfer Objects

#### SubmitCodeRequestDto
- **Location**: `src/main/java/com/ua/unialgo/submission/dto/SubmitCodeRequestDto.java`
- **Purpose**: Request DTO for code submissions
- **Supports**: Both direct code input and file uploads

#### SubmissionResponseDto
- **Location**: `src/main/java/com/ua/unialgo/submission/dto/SubmissionResponseDto.java`
- **Purpose**: Response DTO with submission details for frontend

## Integration Flow

### 1. Code Submission
```
Student → SubmissionController → SubmissionService → Judge0Service
```

1. Student submits code via REST API
2. SubmissionService creates submission entity
3. Code is sent to Judge0 for each test case
4. Submission status is updated asynchronously

### 2. Status Updates
```
SubmissionPollingService → SubmissionService → Judge0Service
```

1. Scheduled task checks for pending submissions
2. Queries Judge0 for status updates
3. Updates submission records in database

### 3. Test Case Evaluation
```
SubmissionService → Judge0Service (for each test case)
```

1. For each test case, code is submitted to Judge0
2. Results are collected and compared
3. Score is calculated based on passed test cases

## Database Schema

### New Tables

#### submissions
- Stores all code submissions and results
- Foreign keys to `user` and `question` tables
- Indexes on user_id, question_id, status, and submission_date

#### question_allowed_languages
- Maps questions to allowed programming languages
- Supports restricting languages per question

### Updated Tables

#### question
- Added `allowedLanguages`, `timeLimit`, `memoryLimit`
- OneToMany relationship with test_cases

#### test_case
- Added `isHidden` flag for private test cases
- Added `weight` for weighted scoring

#### user
- Updated to use auto-increment Long ID
- Added `keycloakId` and `email` fields
- Maintains Keycloak integration

## Configuration

### Application Properties
```properties
# Judge0 Configuration
judge0.api.url=http://judge0-server:2358
judge0.api.key=
judge0.default-language-id=62
judge0.api.timeout=30
judge0.api.connect-timeout=5
judge0.api.read-timeout=30
```

### Docker Compose
The Judge0 services are already configured in `compose.yml`:
- `judge0-server`: Main API server (port 2358)
- `judge0-worker`: Background worker for execution
- `judge0-db`: PostgreSQL database for Judge0
- `judge0-redis`: Redis for queueing

## Security

### Authentication
- All submission endpoints require authentication
- Students can only access their own submissions
- Teachers can access all submissions

### Authorization
- `@PreAuthorize("hasRole('STUDENT')")` for submission endpoints
- `@PreAuthorize("hasRole('TEACHER')")` for administrative endpoints

### Input Validation
- Source code length limits
- Language ID validation
- Test case limits to prevent abuse

## Error Handling

### Judge0 Errors
- Connection timeouts handled gracefully
- 4xx/5xx responses mapped to appropriate exceptions
- Retry logic for transient failures

### Business Logic Errors
- Invalid language for question
- Missing test cases
- User permission violations

## Performance Considerations

### Asynchronous Processing
- Submissions are processed asynchronously
- Polling mechanism prevents blocking operations
- Database updates are batched where possible

### Caching
- Language mappings cached in memory
- Test case results cached per submission

### Resource Limits
- CPU time limits enforced by Judge0
- Memory limits enforced by Judge0
- Submission rate limiting (future enhancement)

## Testing

### Unit Tests
- `SubmissionServiceTest`: Tests service layer logic
- Mocked dependencies for isolated testing
- Edge cases and error conditions covered

### Integration Tests
- End-to-end submission workflow
- Judge0 API integration tests
- Database persistence tests

## Frontend Integration

### File Upload Support
- Multipart file upload endpoint
- MIME type validation
- File size limits

### Monaco Editor Support
- Direct code submission via JSON
- Real-time syntax validation (frontend)
- Code formatting and highlighting (frontend)

### Progress Tracking
- Polling endpoint for status updates
- WebSocket support (future enhancement)
- Real-time score updates

## Monitoring and Logging

### Logging
- All submissions logged with user and question IDs
- Judge0 API calls logged with response times
- Error conditions logged with stack traces

### Metrics
- Submission success rates
- Average execution times
- Popular programming languages

## Future Enhancements

### Planned Features
1. **Plagiarism Detection**: Compare submissions for similarity
2. **Code Quality Analysis**: Static analysis integration
3. **Real-time Updates**: WebSocket for live status updates
4. **Advanced Scoring**: Weighted test cases and partial credit
5. **Resource Monitoring**: Track CPU/memory usage per submission

### Scalability Improvements
1. **Queue System**: Message queue for submission processing
2. **Load Balancing**: Multiple Judge0 instances
3. **Caching Layer**: Redis for frequently accessed data
4. **Database Optimization**: Read replicas and indexing

## Troubleshooting

### Common Issues

#### Judge0 Service Not Available
```bash
# Check service status
docker-compose ps judge0-server

# Check logs
docker-compose logs judge0-server
```

#### Database Migration Failures
```bash
# Run migrations manually
./mvnw liquibase:update

# Check migration status
./mvnw liquibase:status
```

#### Submission Stuck in Processing
- Check Judge0 service health
- Verify polling service is running
- Manual status update via API endpoint

### Debug Endpoints
- `GET /submissions/{id}/status`: Force status refresh
- Judge0 health check at `/about` endpoint
- Application health at `/actuator/health` (if enabled)

## API Documentation

Complete API documentation is available via Swagger UI at:
`http://localhost:8080/public/swagger.html`

Key endpoints include:
- Submission management
- File upload support
- Status tracking
- User and teacher views

This integration provides a robust, scalable foundation for automated code evaluation in the UniAlgo platform.