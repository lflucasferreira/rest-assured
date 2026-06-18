# Advanced — HTTP contract & caching

**Source file:** [`ContractAndCachingTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/ContractAndCachingTest.java)

---

## Purpose

Validates **transport-level contracts** beyond JSON body assertions:

- Security and cache headers on `GET /owners`
- ETag-based conditional GET (`304 Not Modified`)
- RFC 7807 `ProblemDetail` on failing endpoint (`GET /oops`)

---

## Step-by-step — block by block

### Block 1 — Response headers

```java
@Test
void shouldExposeConsistentHttpResponseHeaders() {
    Response response = ownersClient.getAllOwners();
    assertThat(response.getContentType(), containsString("application/json"));
    assertThat(response.getHeader("X-Content-Type-Options"), is("nosniff"));
    assertThat(response.getHeader("Cache-Control"), containsString("no-cache"));
}
```

- **Then:** API exposes consistent security and content headers.

---

### Block 2 — Conditional GET with ETag

```java
String etag = initialResponse.getHeader("ETag");
Assumptions.assumeTrue(etag != null && !etag.isBlank());
Response cachedResponse = ownersClient.getAllOwners(etag);
ResponseValidator.assertStatusCode(cachedResponse.getStatusCode(), 304);
```

- **Given:** API returns `ETag` header (test skips if absent).
- **When:** repeat request with `If-None-Match`.
- **Then:** status 304.

---

### Block 3 — ProblemDetail error contract

```java
Response response = diagnosticsClient.triggerFailingEndpoint();
ResponseValidator.assertStatusCode(response.getStatusCode(), 500);
ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");
```

- **When:** `GET /oops` triggers server error.
- **Then:** body matches RFC 7807 schema with status and title.

---

## How to run

```bash
mvn test -Dtest=ContractAndCachingTest
```
