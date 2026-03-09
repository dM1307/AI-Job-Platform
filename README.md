# AI Job Platform

AI Job Platform is a Spring Boot project that parses resumes, extracts skills with AI, and matches candidates to jobs.

## Why this project is resume-ready
- Clear layered architecture (controller/service/repository).
- Centralized exception handling with structured API errors.
- Automated CI pipeline via GitHub Actions.
- Unit and integration-leaning tests included.
- Extensible service layer so new AI providers and modules can be added later.

## Core features
- Create and manage resumes.
- Upload resume files and parse content via Apache Tika.
- Create jobs and match jobs to resumes.
- Skill extraction and vector-ready embedding flow.
- Semantic skill search endpoint.

## API quality improvements
- Structured error response includes timestamp, HTTP status, request path, and optional details.
- Domain-specific exceptions for:
  - bad requests,
  - parsing errors,
  - unavailable external AI services,
  - missing resources.

## Testing
Run tests locally:

```bash
./mvnw test
```

Tests include:
- global exception handler behavior,
- resume parsing service behavior,
- Spring application context boot with test profile.

## CI pipeline
A GitHub Actions pipeline runs on every push and pull request:
- Java 17 setup,
- Maven dependency caching,
- `./mvnw -B clean verify`.

Workflow file:
- `.github/workflows/ci.yml`

## Extensibility notes
To keep the project future-proof:
- Add new AI providers by introducing additional services and wiring through Spring DI.
- Add new exception types and register handlers in `GlobalExceptionHandler`.
- Add profile-based configs (`application-<profile>.properties`) for local/dev/prod behavior.
- Add feature modules as separate packages (`controller`, `service`, `repository`, `dto`) to keep boundaries clean.
