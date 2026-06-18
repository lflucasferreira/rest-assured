# Negativo — Owners

**Arquivo fonte:** [`OwnersNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java)

---

## Objetivo

Cobre **caminhos de erro** do recurso owners:

- `400` para formato inválido de telefone na criação
- `404` para IDs de owner inexistentes (parametrizado)
- `404` ao deletar owner desconhecido

---

## Passo a passo — bloco a bloco

### Bloco 1 — Erro de validação (400)

```java
@Test
void shouldRejectOwnerWithInvalidTelephone() {
    Owner invalidOwner = TestDataFactory.buildInvalidOwnerWithAlphabeticTelephone();
    Response response = ownersClient.createOwner(invalidOwner);
    ResponseValidator.assertStatusCode(response.getStatusCode(), 400);
    ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");
}
```

- **Dado:** owner com telefone alfabético.
- **Quando:** `POST /owners`.
- **Então:** 400 com corpo RFC 7807.

---

### Bloco 2 — Buscas 404 parametrizadas

```java
@ParameterizedTest(name = "GET /owners/{0} should return {1}")
@CsvSource({ "99999, 404", "0, 404" })
void shouldReturnExpectedStatusForInvalidOwnerIds(int ownerId, int expectedStatus) {
    ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(...);
}
```

- **Quando:** IDs inválidos são requisitados.
- **Então:** status esperado; ProblemDetail opcional quando há corpo.

---

### Bloco 3 — Deletar owner desconhecido

```java
Response response = ownersClient.deleteOwner(99999);
ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
```

---

## Como executar

```bash
mvn test -Dtest=OwnersNegativeApiTest
```
