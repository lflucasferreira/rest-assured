# API — Owners (CRUD & filtro)

**Arquivo fonte:** [`OwnersApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java)

---

## Objetivo

Esta classe cobre **testes de contrato no caminho feliz** do recurso owners:

- Listar todos os owners com validação de schema
- Criar um novo owner e verificar campos persistidos
- Buscar owner por ID
- Filtrar owners pelo parâmetro `lastName`

---

## Pré-requisitos

| Item | Detalhe |
|------|---------|
| **API Petclinic** | Rodando em `http://localhost:9966/petclinic/api` (Docker ou local) |
| **Dados seed** | Pelo menos um owner deve existir para testes de leitura/filtro |
| **Execução** | `mvn test -Dtest=OwnersApiTest` |

---

## Anotações Allure

| Anotação | Valor |
|----------|-------|
| `@Epic` | Spring Petclinic API |
| `@Feature` | Owners |

---

## Conceitos Rest Assured

| Conceito | Uso neste arquivo |
|----------|-------------------|
| **`OwnersClient`** | Encapsula chamadas HTTP em `/owners` |
| **`ResponseValidator`** | Asserções de status e JSON schema |
| **`TestDataFactory.buildOwner()`** | Payload dinâmico para POST |
| **Mapeamento POJO** | `response.as(Owner.class)` e `Owner[].class` |
| **Hamcrest** | `assertThat` para checagens de campo |
| **Cleanup** | `deleteOwner` após teste de criação |

---

## Passo a passo — bloco a bloco

### Bloco 1 — Listar owners

```java
@Test
@Story("List owners")
@DisplayName("GET /owners should return all owners with valid structure")
void shouldReturnAllOwnersWithValidStructure() {
    Response response = ownersClient.getAllOwners();
    ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    List<Owner> owners = Arrays.asList(response.as(Owner[].class));
    assertThat(owners, is(not(empty())));
    ResponseValidator.assertFirstArrayItemMatchesSchema(
        response.getBody().asString(), "schemas/owner-schema.json");
}
```

- **Dado:** a API Petclinic está disponível.
- **Quando:** `GET /owners` é chamado.
- **Então:** status 200, lista não vazia, primeiro item corresponde a `owner-schema.json`.

---

### Bloco 2 — Criar owner

```java
@Test
@Story("Create owner")
void shouldCreateOwnerSuccessfully() {
    Owner ownerPayload = TestDataFactory.buildOwner();
    Response createResponse = ownersClient.createOwner(ownerPayload);
    ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);
    Owner createdOwner = createResponse.as(Owner.class);
    assertThat(createdOwner.getFirstName(), is(ownerPayload.getFirstName()));
    ownersClient.deleteOwner(createdOwner.getId());
}
```

- **Dado:** dados de owner gerados dinamicamente.
- **Quando:** `POST /owners` é enviado.
- **Então:** status 201, resposta corresponde ao schema, campos batem com o payload; owner é removido no cleanup.

---

### Bloco 3 — Buscar por ID

```java
@Test
@Story("Get owner by ID")
void shouldReturnOwnerById() {
    Owner existingOwner = ownersClient.getAllOwners().as(Owner[].class)[0];
    Response response = ownersClient.getOwnerById(existingOwner.getId());
    ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    assertThat(response.as(Owner.class).getId(), is(existingOwner.getId()));
}
```

- **Dado:** um owner existente na coleção.
- **Quando:** `GET /owners/{id}` é chamado.
- **Então:** status 200 e ID corresponde.

---

### Bloco 4 — Filtrar por sobrenome

```java
@Test
@Story("Filter owners")
void shouldFilterOwnersByLastName() {
    String lastName = ownersClient.getAllOwners().as(Owner[].class)[0].getLastName();
    List<Owner> filtered = Arrays.asList(
        ownersClient.getAllOwnersByLastName(lastName).as(Owner[].class));
    filtered.forEach(owner -> assertThat(owner.getLastName(), is(lastName)));
}
```

- **Dado:** owners existem no sistema.
- **Quando:** `GET /owners?lastName=` é chamado.
- **Então:** todos os owners retornados compartilham o sobrenome solicitado.

---

## Como executar

```bash
mvn test -Dtest=OwnersApiTest
docker compose up --abort-on-container-exit --exit-code-from tests
```

---

## Referências relacionadas

- Client: [`OwnersClient.java`](../../../../src/test/java/com/portfolio/petclinic/clients/OwnersClient.java)
- Cenários negativos: [`OwnersNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java)
- Schema: [`owner-schema.json`](../../../../src/test/resources/schemas/owner-schema.json)
