# API — Pets (CRUD)

**Arquivo fonte:** [`PetsApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java)

---

## Objetivo

Esta classe valida o **recurso pets** de ponta a ponta:

- Listar e buscar pets com validação de schema
- Criar pet sob owner (`POST /owners/{ownerId}/pets`)
- Atualizar pet (`PUT /pets/{id}` → 204)
- Remover pet (`DELETE /pets/{id}` → 204, depois 404 no GET)

---

## Pré-requisitos

| Item | Detalhe |
|------|---------|
| **API Petclinic** | Rodando com pet types seedados |
| **Pet types** | Carregados no `@BeforeEach` via `GET /pettypes` |
| **Execução** | `mvn test -Dtest=PetsApiTest` |

---

## Conceitos Rest Assured

| Conceito | Uso neste arquivo |
|----------|-------------------|
| **`@BeforeEach` / `@AfterEach`** | Carrega pet type padrão; limpa pets/owners criados |
| **`PetsClient`** | Endpoints `/pets` e aninhados owner-pet |
| **`PetFields` vs `Pet`** | Payload vs entidade completa para create/update |
| **204 No Content** | Update e delete retornam corpo vazio |

---

## Passo a passo — bloco a bloco

### Bloco 1 — Hooks de ciclo de vida

```java
@BeforeEach
void loadPetType() {
    PetType[] petTypes = petTypesClient.getAllPetTypes().as(PetType[].class);
    defaultPetType = petTypes[0];
}

@AfterEach
void cleanupCreatedResources() {
    createdPetIds.forEach(petId -> petsClient.deletePet(petId));
    createdOwnerIds.forEach(ownerId -> ownersClient.deleteOwner(ownerId));
}
```

- **Dado:** cada teste precisa de um pet type ID válido.
- **Quando:** o teste termina (pass ou fail).
- **Então:** pets e owners criados são removidos para manter o ambiente limpo.

---

### Bloco 2 — Listar e buscar pets

Os testes `shouldReturnAllPetsWithValidStructure` e `shouldReturnPetById` validam respostas de coleção e recurso único contra `pet-schema.json`.

---

### Bloco 3 — Criar pet para owner

```java
Owner owner = createOwner();
PetFields petPayload = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());
Response createResponse = petsClient.createPetForOwner(owner.getId(), petPayload);
ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);
```

- **Dado:** um novo owner e payload dinâmico de pet.
- **Quando:** `POST /owners/{ownerId}/pets` é chamado.
- **Então:** status 201, pet tem ID, nome, ownerId e type.

> O Petclinic REST expõe criação de pet aninhada sob owners, não `POST /pets`.

---

### Bloco 4 — Atualizar e remover

Update espera **204**; a verificação é feita via `GET` subsequente. Delete espera **204** seguido de **404** na leitura.

---

## Como executar

```bash
mvn test -Dtest=PetsApiTest
```

---

## Referências relacionadas

- Client: [`PetsClient.java`](../../../../src/test/java/com/portfolio/petclinic/clients/PetsClient.java)
- Cenários negativos: [`PetsNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java)
