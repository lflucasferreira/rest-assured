# Spring Petclinic API Test Automation

Production-quality API test automation framework for the [Spring Petclinic REST](https://github.com/spring-petclinic/spring-petclinic-rest) application, built with **Java 17**, **Rest Assured**, **JUnit 5**, and **Maven**.

## Description

This project demonstrates a scalable API testing architecture suitable for QA automation portfolios. It validates core Petclinic endpoints (owners and pets), uses reusable API clients, dynamic test data, JSON schema validation, Allure reporting, and Docker-based execution for local and CI environments.

## Folder Structure

```
.
├── docker-compose.yml
├── .gitlab-ci.yml
├── pom.xml
└── src/test/
    ├── java/com/portfolio/petclinic/
    │   ├── base/           # Shared test setup
    │   ├── clients/        # API client layer
    │   ├── models/         # Request/response POJOs
    │   ├── tests/          # JUnit test suites
    │   │   ├── advanced/   # Network capture, WireMock, contracts
    │   │   ├── flows/      # End-to-end business flows
    │   │   └── negative/   # Parameterized negative scenarios
    │   └── utils/          # Config, data factory, validators
    └── resources/
        ├── config/         # Environment-specific properties
        ├── schemas/        # JSON schemas for response validation
        ├── allure.properties
        └── logback-test.xml
```

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker and Docker Compose (for containerized runs)

## How to Run

### Option 1: Docker Compose (recommended)

Starts the Petclinic API and runs the full test suite in isolated containers:

```bash
docker compose up --abort-on-container-exit --exit-code-from tests
```

### Option 2: Local API + local Maven

1. Start the API:

```bash
docker run -p 9966:9966 springcommunity/spring-petclinic-rest:latest
```

2. Run tests against `dev` environment (default):

```bash
mvn clean test
```

3. Override base URI if needed:

```bash
mvn clean test -Denv=dev -Dapi.base.uri=http://localhost:9966/petclinic/api
```

## How to Test

```bash
# Run all tests
mvn clean test

# Run a specific test class
mvn test -Dtest=OwnersApiTest

# Run with Docker environment profile
mvn clean test -Denv=docker -Dapi.base.uri=http://localhost:9966/petclinic/api
```

### Allure Report

```bash
mvn clean test
mvn allure:serve
```

Published on GitHub Pages after each push to `main`:

- Hub: https://lflucasferreira.github.io/rest-assured/
- Report: https://lflucasferreira.github.io/rest-assured/report/

Local Pages build (same as CI):

```bash
npm ci
mvn clean test
npm run allure:merge-results   # optional if using target/allure-results only
npm run pages:prepare-allure
npm run pages:build
```

## API Coverage

| Method | Endpoint | Test Class |
|--------|----------|------------|
| GET | `/owners` | `OwnersApiTest` |
| GET | `/owners?lastName=` | `OwnersApiTest` |
| GET | `/owners/{id}` | `OwnersApiTest` |
| POST | `/owners` | `OwnersApiTest` |
| GET | `/pets` | `PetsApiTest` |
| GET | `/pets/{id}` | `PetsApiTest` |
| POST | `/owners/{ownerId}/pets` | `PetsApiTest` |
| PUT | `/pets/{id}` | `PetsApiTest` |
| DELETE | `/pets/{id}` | `PetsApiTest` |

### Advanced QA coverage

| Area | Test Class | Techniques |
|------|------------|------------|
| Network interception | `NetworkInspectionTest` | Custom `Filter`, JsonPath, SLA (`time()`) |
| External mocks | `WireMockNotificationTest` | WireMock stubs, verify, fault injection |
| HTTP contracts | `ContractAndCachingTest` | Security headers, RFC 7807, conditional GET |
| Negative paths | `*NegativeApiTest` | `@ParameterizedTest`, optional error schema |
| E2E flows | `PetLifecycleFlowTest` | Multi-step orchestration + call-chain validation |

> Note: Pet creation in Petclinic REST is exposed as `POST /owners/{ownerId}/pets` (not `POST /pets`).

## Technologies Used

- Java 17
- Maven
- Rest Assured 5
- JUnit 5
- Hamcrest
- Jackson
- JavaFaker
- WireMock
- Allure Report
- Docker / Docker Compose
- GitLab CI

## Contribution Guidelines

1. Follow the existing package structure (`clients`, `models`, `tests`, `utils`).
2. Add API calls only through client classes.
3. Use dynamic test data via `TestDataFactory`.
4. Include status code, body, and schema assertions.
5. Keep tests independent and clean up created data when applicable.

## License

This project is provided for educational and portfolio purposes.
