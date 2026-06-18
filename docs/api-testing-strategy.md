# API Testing Strategy — Spring Petclinic REST

This Rest Assured project validates the [Spring Petclinic REST](https://github.com/spring-petclinic/spring-petclinic-rest) API exclusively through **HTTP clients**, **POJO models**, and **JSON schema contracts**. There are no browser or UI tests in this repository.

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
void setUp() {
    RestAssured.requestSpecification = requestSpec;
    networkCapture.reset();
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

## JSON schemas

Schemas live in [`src/test/resources/schemas/`](../src/test/resources/schemas/):

| Schema | Used for |
|--------|----------|
| `owner-schema.json` | Owner responses |
| `pet-schema.json` | Pet responses |
| `problem-detail-schema.json` | RFC 7807 error payloads |

## Test data

[`TestDataFactory`](../src/test/java/com/portfolio/petclinic/utils/TestDataFactory.java) generates dynamic data with JavaFaker. Prefer factories over hardcoded fixtures to avoid collisions in parallel runs.

## Environment configuration

[`ConfigLoader`](../src/test/java/com/portfolio/petclinic/utils/ConfigLoader.java) reads `src/test/resources/config/{env}.properties`:

```bash
mvn clean test -Denv=dev -Dapi.base.uri=http://localhost:9966/petclinic/api
```

## Network inspection

A custom Rest Assured `Filter` (`NetworkCaptureFilter`) records the last HTTP exchange. [`NetworkInspector`](../src/test/java/com/portfolio/petclinic/utils/NetworkInspector.java) attaches traces to Allure and asserts URI sequences in flow tests.

## External service mocking

[`WireMockSupport`](../src/test/java/com/portfolio/petclinic/utils/WireMockSupport.java) starts an embedded WireMock server for outbound webhook simulations. Use `WireMock.verify()` to assert request payloads without hitting real downstream systems.

## What this repo does not use

- **Browser automation** — API-only scope.
- **Shared production data** — tests create and clean up their own owners/pets when possible.
- **Hardcoded IDs** — parameterized negative tests use sentinel IDs (e.g. `99999`).

## Client / endpoint map

| Client | Base path | Test classes |
|--------|-----------|--------------|
| `OwnersClient` | `/owners` | `OwnersApiTest`, `OwnersNegativeApiTest`, flows |
| `PetsClient` | `/pets`, `/owners/{id}/pets` | `PetsApiTest`, `PetsNegativeApiTest`, flows |
| `PetTypesClient` | `/pettypes` | `PetsApiTest`, flows |
| `VisitsClient` | `/owners/{id}/pets/{id}/visits` | `PetLifecycleFlowTest` |
| `DiagnosticsClient` | `/oops` | `ContractAndCachingTest` |
| `AuditNotificationClient` | WireMock `/audit/owner-created` | `WireMockNotificationTest` |

When the API adds a new resource, add a client method rather than inlining Rest Assured calls in test classes.
