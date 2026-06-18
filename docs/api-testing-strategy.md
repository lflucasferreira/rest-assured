# Rest Assured Testing Strategy — Spring Petclinic REST

This Rest Assured project validates the [Spring Petclinic REST](https://github.com/spring-petclinic/spring-petclinic-rest) API exclusively through **HTTP clients**, **POJO models**, and **JSON schema contracts**. There are no browser or UI tests in this repository.

**Current suite size:** 98 tests (7 optional skips for environment-dependent scenarios).

## Client layer

All HTTP calls go through dedicated client classes in [`src/test/java/com/portfolio/petclinic/clients/`](../src/test/java/com/portfolio/petclinic/clients/):

```java
// clients/OwnersClient.java
public Response getAllOwners() {
    return given()
        .spec(requestSpec)
        .when()
        .get("/owners");
}
```

Test classes should **not** call `RestAssured.given()` directly — use clients to keep endpoints and headers centralized.

## Base test setup

[`BaseTest`](../src/test/java/com/portfolio/petclinic/base/BaseTest.java) wires shared clients, request specification, and the network capture filter:

```java
@BeforeEach
void initClients() {
    networkCapture = new NetworkCaptureFilter();
    ownersClient = new OwnersClient(networkCapture);
    petsClient = new PetsClient(networkCapture);
    // ...
}
```

## Assertion patterns

| Layer | Class | When to use |
|-------|-------|-------------|
| Status code | `ResponseValidator.assertStatusCode` | Every test |
| JSON schema | `ResponseValidator.assertMatchesSchema` | Contract validation on success/error bodies |
| POJO mapping | `response.as(Owner.class)` | Field-level assertions with Hamcrest |
| JsonPath | `response.jsonPath().getString("[0].lastName")` | Targeted extraction without full mapping |
| Error bodies | `ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail` | 4xx/5xx with optional RFC 7807 body |
| SLA | `response.then().time(lessThan(ms))` | Performance guardrails |
| OpenAPI | `OpenApiResponseValidator.assertResponseMatchesDocumentedOperation` | Per-operation contract checks |

## JSON schemas

Schemas live in [`src/test/resources/schemas/`](../src/test/resources/schemas/):

| Schema | Used for |
|--------|----------|
| `owner-schema.json` | Owner responses |
| `pet-schema.json` | Pet responses |
| `visit-schema.json` | Visit responses |
| `vet-schema.json` | Veterinarian responses |
| `specialty-schema.json` | Specialty responses |
| `owners-page-schema.json` | Paginated `/v2/owners` |
| `openapi-docs-schema.json` | OpenAPI document smoke |
| `problem-detail-schema.json` | RFC 7807 error payloads |

## Test data

[`TestDataFactory`](../src/test/java/com/portfolio/petclinic/utils/TestDataFactory.java) generates dynamic data with JavaFaker. Prefer factories over hardcoded fixtures to avoid collisions in parallel runs.

## Environment configuration

[`ConfigLoader`](../src/test/java/com/portfolio/petclinic/utils/ConfigLoader.java) reads `src/test/resources/config/{env}.properties`:

```bash
mvn clean test -Denv=dev \
  -Dapi.base.uri=http://localhost:9966/petclinic/api \
  -Dapi.secure.base.uri=http://localhost:9967/petclinic/api
```

| Property | Purpose |
|----------|---------|
| `api.base.uri` | Default API (no auth) |
| `api.secure.base.uri` | Secure API with Basic Auth |
| `api.petclinic.root.uri` | Actuator & OpenAPI root |
| `webhook.timeout.ms` | WireMock client timeout |

## JUnit tags

Run subsets via Maven Surefire groups (`pom.xml` → `test.groups`):

```bash
mvn test -Dtest.groups=smoke
mvn test -Dtest.groups=security
mvn test -Dtest.groups=performance
mvn test -Dtest.groups=contract
```

## Network inspection

A custom Rest Assured `Filter` (`NetworkCaptureFilter`) records the last HTTP exchange. [`NetworkInspector`](../src/test/java/com/portfolio/petclinic/utils/NetworkInspector.java) attaches traces to Allure and asserts URI sequences in flow tests.

## External service mocking

[`WireMockSupport`](../src/test/java/com/portfolio/petclinic/utils/WireMockSupport.java) starts an embedded WireMock server for outbound webhook simulations. Suites:

- `WireMockNotificationTest` — happy path + 503 fault
- `WireMockResilienceTest` — timeout, malformed JSON, connection reset
- `WireMockRetryIntegrationTest` — retry after transient failure

## Security testing

Docker Compose and CI run two API instances:

- **9966** — `petclinic-api` (no auth)
- **9967** — `petclinic-api-secure` (`PETCLINIC_SECURITY_ENABLE=true`)

Use `OwnersClient.secured()`, `OwnersClient.withCredentials(user, pass)`, and `@Tag("security")` tests. RBAC tests provision users via `UsersClient` when the image supports login for `POST /users`.

## What this repo does not use

- **Browser automation** — API-only scope.
- **Shared production data** — tests create and clean up their own owners/pets when possible.
- **Hardcoded IDs** — parameterized negative tests use sentinel IDs (e.g. `99999`).

## Client / endpoint map

| Client | Base path | Test classes |
|--------|-----------|--------------|
| `ActuatorClient` | `/actuator` | `ActuatorSmokeTest` |
| `OwnersClient` | `/owners` | `OwnersApiTest`, `OwnersNegativeApiTest`, flows, security |
| `OwnersV2Client` | `/v2/owners` | `OwnersV2ApiTest`, `OwnersV2NegativeApiTest` |
| `PetsClient` | `/pets`, `/owners/{id}/pets` | `PetsApiTest`, `PetsNestedApiTest`, `PetsNegativeApiTest`, flows |
| `VisitsClient` | `/visits`, nested visits | `VisitsApiTest`, `VisitsNegativeApiTest`, flows |
| `PetTypesClient` | `/pettypes` | `PetTypesApiTest`, `PetsApiTest`, flows |
| `VetsClient` | `/vets` | `VetsApiTest`, security |
| `SpecialtiesClient` | `/specialties` | `SpecialtiesApiTest`, security |
| `OpenApiClient` | `/v3/api-docs` | `OpenApiContractTest`, `OpenApiOperationContractTest` |
| `UsersClient` | `/users` | `UsersApiTest`, security |
| `DiagnosticsClient` | `/oops` | `ContractAndCachingTest` |
| `AuditNotificationClient` | WireMock `/audit/owner-created` | WireMock suites |

When the API adds a new resource, add a client method rather than inlining Rest Assured calls in test classes.
