# Negativo — Pets

**Arquivo fonte:** [`PetsNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java)

---

## Objetivo

Cobre **caminhos de erro** do recurso pets:

- `404` para IDs de pet inexistentes (parametrizado)
- `404` ao atualizar pet inexistente
- `404` ao criar pet para owner inexistente (parametrizado)

---

## Passo a passo — bloco a bloco

### Bloco 1 — GET 404 parametrizado

```java
@ParameterizedTest(name = "GET /pets/{0} should return {1}")
@CsvSource({ "99999, 404", "0, 404" })
void shouldReturnExpectedStatusForInvalidPetIds(int petId, int expectedStatus) {
    ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(...);
}
```

---

### Bloco 2 — Atualizar pet inexistente

```java
@Test
void shouldReturnNotFoundWhenUpdatingMissingPet() {
    var petPayload = new Pet();
    petPayload.setId(99999);
    Response response = petsClient.updatePet(99999, petPayload);
    ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
}
```

---

### Bloco 3 — Criar pet para owner inexistente

```java
@ParameterizedTest(name = "POST pet to missing owner {0} should return 404")
@ValueSource(ints = {99999, 50000})
void shouldRejectPetCreationForMissingOwner(int ownerId) {
    Response response = petsClient.createPetForOwner(ownerId, petFields);
    ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
}
```

---

## Como executar

```bash
mvn test -Dtest=PetsNegativeApiTest
```
