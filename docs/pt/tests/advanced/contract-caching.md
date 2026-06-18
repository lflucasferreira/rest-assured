# Avançado — Contrato HTTP & cache

**Arquivo fonte:** [`ContractAndCachingTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java)

---

## Objetivo

Valida **contratos em nível de transporte** além de asserções no corpo JSON:

- Headers de segurança e cache em `GET /owners`
- GET condicional com ETag (`304 Not Modified`)
- `ProblemDetail` RFC 7807 em endpoint de falha (`GET /oops`)

---

## Passo a passo — bloco a bloco

### Bloco 1 — Headers de resposta

```java
@Test
void shouldExposeConsistentHttpResponseHeaders() {
    Response response = ownersClient.getAllOwners();
    assertThat(response.getContentType(), containsString("application/json"));
    assertThat(response.getHeader("X-Content-Type-Options"), is("nosniff"));
    assertThat(response.getHeader("Cache-Control"), containsString("no-cache"));
}
```

- **Então:** a API expõe headers de segurança e conteúdo consistentes.

---

### Bloco 2 — GET condicional com ETag

```java
String etag = initialResponse.getHeader("ETag");
Assumptions.assumeTrue(etag != null && !etag.isBlank());
Response cachedResponse = ownersClient.getAllOwners(etag);
ResponseValidator.assertStatusCode(cachedResponse.getStatusCode(), 304);
```

- **Dado:** a API retorna header `ETag` (teste é ignorado se ausente).
- **Quando:** repete a requisição com `If-None-Match`.
- **Então:** status 304.

---

### Bloco 3 — Contrato de erro ProblemDetail

```java
Response response = diagnosticsClient.triggerFailingEndpoint();
ResponseValidator.assertStatusCode(response.getStatusCode(), 500);
ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");
```

- **Quando:** `GET /oops` dispara erro de servidor.
- **Então:** corpo corresponde ao schema RFC 7807 com status e title.

---

## Como executar

```bash
mvn test -Dtest=ContractAndCachingTest
```
