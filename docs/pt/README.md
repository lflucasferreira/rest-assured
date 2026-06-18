# Rest Assured Automation Suite — Documentação de Treinamento

Material didático que explica **bloco a bloco** cada classe de teste do projeto. A suite atual possui **98 testes** cobrindo owners, pets, visits, vets, specialties, segurança, OpenAPI e cenários avançados.

**Idioma:** Português · [English](../en/README.md)

---

## Como usar este material

1. Leia o doc da suite que você vai executar ou manter.
2. Abra o [arquivo de teste](..) linkado no topo do documento.
3. Siga a explicação seção por seção enquanto lê o código.
4. Execute a suite localmente:

```bash
docker compose up --abort-on-container-exit --exit-code-from tests   # API + API segura + testes
mvn clean test                                                       # Maven local (APIs devem estar up)
mvn test -Dtest=VisitsApiTest                                        # classe única
mvn test -Dtest.groups=smoke                                         # subset por tag
mvn allure:serve                                                     # relatório Allure
```

---

## Índice por suite

### API principal — Owners & Pets

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Owners CRUD, filtro & PUT | [owners.api.md](tests/owners/owners.api.md) | [`OwnersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java) |
| Pets CRUD | [pets.api.md](tests/pets/pets.api.md) | [`PetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java) |
| Pet nested route | — | [`PetsNestedApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsNestedApiTest.java) |
| Owners paginação v2 | — | [`OwnersV2ApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersV2ApiTest.java) |

### Domínio expandido

| Suite | Arquivo de teste |
|-------|------------------|
| Visits CRUD | [`VisitsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/VisitsApiTest.java) |
| Vets CRUD | [`VetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/VetsApiTest.java) |
| Specialties CRUD | [`SpecialtiesApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/SpecialtiesApiTest.java) |
| Pet types CRUD | [`PetTypesApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetTypesApiTest.java) |
| Actuator smoke | [`ActuatorSmokeTest.java`](../../src/test/java/com/portfolio/petclinic/tests/smoke/ActuatorSmokeTest.java) |

### Fluxos end-to-end

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Ciclo de vida do pet | [pet-lifecycle.md](tests/flows/pet-lifecycle.md) | [`PetLifecycleFlowTest.java`](../../src/test/java/com/portfolio/petclinic/tests/flows/PetLifecycleFlowTest.java) |

### Segurança

| Suite | Arquivo de teste |
|-------|------------------|
| Basic Auth (401/200) | [`SecurityApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/SecurityApiTest.java) |
| RBAC por role | [`SecurityAuthorizationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/SecurityAuthorizationTest.java) |
| POST /users | [`UsersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/security/UsersApiTest.java) |

### Técnicas avançadas de QA

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Contrato HTTP & cache | [contract-caching.md](tests/advanced/contract-caching.md) | [`ContractAndCachingTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java) |
| Inspeção de rede | [network-inspection.md](tests/advanced/network-inspection.md) | [`NetworkInspectionTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java) |
| WireMock notificações | [wiremock-notification.md](tests/advanced/wiremock-notification.md) | [`WireMockNotificationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java) |
| WireMock resiliência | — | [`WireMockResilienceTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockResilienceTest.java) |
| OpenAPI | — | [`OpenApiContractTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/OpenApiContractTest.java) |
| Performance SLA | — | [`PerformanceSmokeTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/PerformanceSmokeTest.java) |

### Cenários negativos

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Owners — validação & 404 | [owners-negative.md](tests/negative/owners-negative.md) | [`OwnersNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java) |
| Pets — validação & 404 | [pets-negative.md](tests/negative/pets-negative.md) | [`PetsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java) |
| Visits negativos | — | [`VisitsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/VisitsNegativeApiTest.java) |
| Paginação v2 inválida | — | [`OwnersV2NegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersV2NegativeApiTest.java) |

---

## Conceitos transversais

Os documentos cobrem, entre outros:

- **Rest Assured:** `given()`/`when()`/`then()`, `Response`, JsonPath, matchers Hamcrest, SLA com `time()`
- **JUnit 5:** `@Test`, `@BeforeEach`/`@AfterEach`, `@ParameterizedTest`, `@Tag`, `@DisplayName`, Allure
- **Camada de clients:** `ActuatorClient`, `OwnersClient`, `VisitsClient`, `VetsClient`, `UsersClient`, …
- **Segurança:** API dual (9966/9967), `OwnersClient.secured()`, RBAC
- **OpenAPI:** `OpenApiClient`, `OpenApiResponseValidator`
- **Captura de rede:** `NetworkCaptureFilter` e `NetworkInspector`
- **Mocks externos:** WireMock (notificação, resiliência, retry)
- **Configuração:** [`ConfigLoader`](../../src/test/java/com/portfolio/petclinic/utils/ConfigLoader.java) — veja [`api-testing-strategy.md`](../api-testing-strategy.md)

---

## Outros materiais em `docs/`

| Recurso | Descrição |
|---------|-----------|
| [`slides/`](../slides/) | Apresentação Reveal.js + PDF |
| [`guia-completo.html`](../guia-completo.html) | Guia passo a passo em português |
| [`complete-guide.html`](../complete-guide.html) | Step-by-step guide in English |
| [`api-testing-strategy.md`](../api-testing-strategy.md) | Clients, schemas, tags e mapa de endpoints |
| [`rest-assured-technical-interview-questions.md`](../rest-assured-technical-interview-questions.md) | Perguntas técnicas para entrevistas |

---

## Estrutura de pastas

```
docs/
├── index.html                         ← hub
├── guia-completo.html                 ← guia completo (PT)
├── complete-guide.html                ← complete guide (EN)
├── api-testing-strategy.md
├── pt/README.md                       ← este índice
├── en/README.md
└── slides/
    ├── index.html                     ← apresentação
    └── rest-assured-intro-slides.pdf  ← export PDF
```

Cada `.md` em `docs/pt/tests/` espelha a classe de teste homônima em `src/test/java/`.
