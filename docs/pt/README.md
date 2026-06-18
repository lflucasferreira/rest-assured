# Rest Assured Automation Suite — Documentação de Treinamento

Material didático que explica **bloco a bloco** cada classe de teste do projeto. Ideal para novos alunos que estão aprendendo Rest Assured, JUnit 5 e o Rest Assured Automation Suite em Java.

Cada documento aponta para o arquivo de teste correspondente com um link relativo.

**Idioma:** Português · [English](../en/README.md)

---

## Como usar este material

1. Leia o doc da suite que você vai executar ou manter.
2. Abra o [arquivo de teste](..) linkado no topo do documento.
3. Siga a explicação seção por seção enquanto lê o código.
4. Execute a suite localmente:

```bash
docker compose up --abort-on-container-exit --exit-code-from tests   # API + testes no Docker
mvn clean test                                                       # Maven local (API deve estar up)
mvn test -Dtest=OwnersApiTest                                        # classe única
mvn allure:serve                                                     # relatório Allure
```

---

## Índice por suite

### API principal — Owners & Pets

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Owners CRUD & filtro | [owners.api.md](tests/owners/owners.api.md) | [`OwnersApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java) |
| Pets CRUD | [pets.api.md](tests/pets/pets.api.md) | [`PetsApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java) |

### Fluxos end-to-end

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Ciclo de vida do pet | [pet-lifecycle.md](tests/flows/pet-lifecycle.md) | [`PetLifecycleFlowTest.java`](../../src/test/java/com/portfolio/petclinic/tests/flows/PetLifecycleFlowTest.java) |

### Técnicas avançadas de QA

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Contrato HTTP & cache | [contract-caching.md](tests/advanced/contract-caching.md) | [`ContractAndCachingTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java) |
| Inspeção de rede | [network-inspection.md](tests/advanced/network-inspection.md) | [`NetworkInspectionTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java) |
| WireMock notificações | [wiremock-notification.md](tests/advanced/wiremock-notification.md) | [`WireMockNotificationTest.java`](../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java) |

### Cenários negativos

| Suite | Documentação | Arquivo de teste |
|-------|--------------|------------------|
| Owners — validação & 404 | [owners-negative.md](tests/negative/owners-negative.md) | [`OwnersNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java) |
| Pets — validação & 404 | [pets-negative.md](tests/negative/pets-negative.md) | [`PetsNegativeApiTest.java`](../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java) |

---

## Conceitos transversais

Os documentos cobrem, entre outros:

- **Rest Assured:** `given()`/`when()`/`then()`, `Response`, JsonPath, matchers Hamcrest, SLA com `time()`
- **JUnit 5:** `@Test`, `@BeforeEach`/`@AfterEach`, `@ParameterizedTest`, `@DisplayName`, `@Epic`/`@Feature`/`@Story` (Allure)
- **Camada de clients:** classes em [`clients/`](../../src/test/java/com/portfolio/petclinic/clients/)
- **POJOs:** modelos de request/response em [`models/`](../../src/test/java/com/portfolio/petclinic/models/)
- **Dados de teste:** `TestDataFactory` com JavaFaker em [`utils/`](../../src/test/java/com/portfolio/petclinic/utils/)
- **Validação de schema:** JSON schemas em [`resources/schemas/`](../../src/test/resources/schemas/)
- **Captura de rede:** `Filter` customizado via `NetworkCaptureFilter` e `NetworkInspector`
- **Mocks externos:** stubs e verify com WireMock em [`WireMockSupport`](../../src/test/java/com/portfolio/petclinic/utils/WireMockSupport.java)
- **Configuração:** perfis de ambiente via [`ConfigLoader`](../../src/test/java/com/portfolio/petclinic/utils/ConfigLoader.java) — veja [`api-testing-strategy.md`](../api-testing-strategy.md)

---

## Outros materiais em `docs/`

| Recurso | Descrição |
|---------|-----------|
| [`slides/`](../slides/) | Apresentação introdutória Rest Assured (HTML/PDF) |
| [`guia-completo.html`](../guia-completo.html) | Guia passo a passo em português (página única) |
| [`complete-guide.html`](../complete-guide.html) | Step-by-step guide in English (single page) |
| [`api-testing-strategy.md`](../api-testing-strategy.md) | Camada de clients, schemas e padrões de asserção |
| [`rest-assured-technical-interview-questions.md`](../rest-assured-technical-interview-questions.md) | Banco de perguntas técnicas para entrevistas (Português) |

---

## Estrutura de pastas

```
docs/
├── README.md                          ← seletor de idioma
├── guia-completo.html                 ← guia completo (PT)
├── complete-guide.html                ← complete guide (EN)
├── rest-assured-technical-interview-questions.md
├── api-testing-strategy.md
├── pt/
│   ├── README.md                      ← índice (Português)
│   └── tests/                         ← walkthroughs por classe
├── en/
│   ├── README.md                      ← index (English)
│   └── tests/
└── slides/                            ← apresentação Reveal.js
```

Cada `.md` em `docs/pt/tests/` espelha a classe de teste homônima em `src/test/java/`.
