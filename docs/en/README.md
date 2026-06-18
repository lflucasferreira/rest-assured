# Rest Assured Automation Suite — Training Documentation

Instructional material that explains **block by block** each test class in the project. The current suite has **98 tests** covering owners, pets, visits, vets, specialties, security, OpenAPI, and advanced scenarios.

**Language:** English · [Português](../pt/README.md)

---

## How to use this material

```bash
docker compose up --abort-on-container-exit --exit-code-from tests
mvn clean test
mvn test -Dtest=VisitsApiTest
mvn test -Dtest.groups=smoke
mvn allure:serve
```

---

## Index by suite

### Core API — Owners & Pets

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| Owners CRUD, filter & PUT | [owners.api.md](tests/owners/owners.api.md) | [`OwnersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java) |
| Pets CRUD | [pets.api.md](tests/pets/pets.api.md) | [`PetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java) |
| Nested pet route | — | [`PetsNestedApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsNestedApiTest.java) |
| Owners v2 pagination | — | [`OwnersV2ApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersV2ApiTest.java) |

### Expanded domain

| Suite | Test file |
|-------|-----------|
| Visits CRUD | [`VisitsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/VisitsApiTest.java) |
| Vets CRUD | [`VetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/VetsApiTest.java) |
| Specialties CRUD | [`SpecialtiesApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/SpecialtiesApiTest.java) |
| Pet types CRUD | [`PetTypesApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetTypesApiTest.java) |
| Actuator smoke | [`ActuatorSmokeTest.java`](../../src/test/java/com/portfolio/petclinic/tests/smoke/ActuatorSmokeTest.java) |

### Security

| Suite | Test file |
|-------|-----------|
| Basic Auth | [`SecurityApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/SecurityApiTest.java) |
| RBAC | [`SecurityAuthorizationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/SecurityAuthorizationTest.java) |
| User provisioning | [`UsersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/UsersApiTest.java) |

### Advanced QA

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| HTTP contract & caching | [contract-caching.md](tests/advanced/contract-caching.md) | [`ContractAndCachingTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java) |
| Network inspection | [network-inspection.md](tests/advanced/network-inspection.md) | [`NetworkInspectionTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java) |
| WireMock | [wiremock-notification.md](tests/advanced/wiremock-notification.md) | [`WireMockNotificationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java) |
| WireMock resilience | — | [`WireMockResilienceTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockResilienceTest.java) |
| OpenAPI | — | [`OpenApiContractTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/OpenApiContractTest.java) |

### Negative scenarios

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| Owners | [owners-negative.md](tests/negative/owners-negative.md) | [`OwnersNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java) |
| Pets | [pets-negative.md](tests/negative/pets-negative.md) | [`PetsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java) |
| Visits | — | [`VisitsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/VisitsNegativeApiTest.java) |

---

## Other materials

| Resource | Description |
|----------|-------------|
| [`slides/`](../slides/) | Reveal.js deck + PDF export |
| [`complete-guide.html`](../complete-guide.html) | Single-page English guide |
| [`guia-completo.html`](../guia-completo.html) | Guia em português |
| [`api-testing-strategy.md`](../api-testing-strategy.md) | Clients, schemas, tags, endpoint map |
