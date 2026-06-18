# Spring Petclinic API Tests — Training Documentation

Instructional material that explains **block by block** each test class in the project. Ideal for new students learning Rest Assured, JUnit 5, and API test automation in Java.

Each document links to the corresponding test file with a relative path.

**Language:** English · [Português](../pt/README.md)

---

## How to use this material

1. Read the doc for the suite you will run or maintain.
2. Open the [test file](..) linked at the top of the document.
3. Follow the explanation section by section while reading the code.
4. Run the suite locally:

```bash
docker compose up --abort-on-container-exit --exit-code-from tests   # API + tests in Docker
mvn clean test                                                       # local Maven (API must be up)
mvn test -Dtest=OwnersApiTest                                        # single class
mvn allure:serve                                                     # Allure report
```

---

## Index by suite

### Core API — Owners & Pets

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| Owners CRUD & filter | [owners.api.md](tests/owners/owners.api.md) | [`OwnersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java) |
| Pets CRUD | [pets.api.md](tests/pets/pets.api.md) | [`PetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java) |

### End-to-end flows

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| Pet lifecycle | [pet-lifecycle.md](tests/flows/pet-lifecycle.md) | [`PetLifecycleFlowTest.java`](../../src/test/java/com/portfolio/petclinic/tests/flows/PetLifecycleFlowTest.java) |

### Advanced QA techniques

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| HTTP contract & caching | [contract-caching.md](tests/advanced/contract-caching.md) | [`ContractAndCachingTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java) |
| Network inspection | [network-inspection.md](tests/advanced/network-inspection.md) | [`NetworkInspectionTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java) |
| WireMock notifications | [wiremock-notification.md](tests/advanced/wiremock-notification.md) | [`WireMockNotificationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java) |

### Negative scenarios

| Suite | Documentation | Test file |
|-------|---------------|-----------|
| Owners — validation & 404 | [owners-negative.md](tests/negative/owners-negative.md) | [`OwnersNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java) |
| Pets — validation & 404 | [pets-negative.md](tests/negative/pets-negative.md) | [`PetsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java) |

---

## Cross-cutting concepts

The docs cover, among other topics:

- **Rest Assured:** `given()`/`when()`/`then()`, `Response`, JsonPath, Hamcrest matchers, `time()` SLA
- **JUnit 5:** `@Test`, `@BeforeEach`/`@AfterEach`, `@ParameterizedTest`, `@DisplayName`, `@Epic`/`@Feature`/`@Story` (Allure)
- **API Client layer:** classes in [`clients/`](../../src/test/java/com/portfolio/petclinic/clients/)
- **POJOs:** request/response models in [`models/`](../../src/test/java/com/portfolio/petclinic/models/)
- **Test data:** `TestDataFactory` with JavaFaker in [`utils/`](../../src/test/java/com/portfolio/petclinic/utils/)
- **Schema validation:** JSON schemas in [`resources/schemas/`](../../src/test/resources/schemas/)
- **Network capture:** custom `Filter` via `NetworkCaptureFilter` and `NetworkInspector`
- **External mocks:** WireMock stubs and verify in [`WireMockSupport`](../../src/test/java/com/portfolio/petclinic/utils/WireMockSupport.java)
- **Configuration:** environment profiles via [`ConfigLoader`](../../src/test/java/com/portfolio/petclinic/utils/ConfigLoader.java) — see [`api-testing-strategy.md`](../api-testing-strategy.md)

---

## Other materials in `docs/`

| Resource | Description |
|----------|-------------|
| [`slides/`](../slides/) | Introductory Rest Assured presentation (HTML/PDF) |
| [`guia-completo.html`](../guia-completo.html) | Step-by-step guide in Portuguese (single page) |
| [`complete-guide.html`](../complete-guide.html) | Step-by-step guide in English (single page) |
| [`api-testing-strategy.md`](../api-testing-strategy.md) | Client layer, schemas, and assertion patterns |
| [`rest-assured-technical-interview-questions.md`](../rest-assured-technical-interview-questions.md) | Technical interview question bank (Portuguese) |

---

## Folder structure

```
docs/
├── README.md                          ← language selector
├── guia-completo.html                 ← complete guide (PT)
├── complete-guide.html                ← complete guide (EN)
├── rest-assured-technical-interview-questions.md
├── api-testing-strategy.md
├── en/
│   ├── README.md                      ← this index (English)
│   └── tests/                         ← walkthroughs per test class
├── pt/
│   ├── README.md                      ← índice (Português)
│   └── tests/
└── slides/                            ← Reveal.js presentation only
```

Each `.md` in `docs/en/tests/` mirrors the homonymous test class under `src/test/java/`.
