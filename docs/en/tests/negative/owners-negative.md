# Negative — Owners

**Source file:** [`OwnersNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java)

---

## Purpose

Covers **error paths** for the owners resource:

- `400` for invalid telephone format on create
- `404` for non-existent owner IDs (parameterized)
- `404` when deleting unknown owner

---

## Step-by-step — block by block

### Block 1 — Validation error (400)

```java
@Test
void shouldRejectOwnerWithInvalidTelephone() {
    Owner invalidOwner = TestDataFactory.buildInvalidOwnerWithAlphabeticTelephone();
    Response response = ownersClient.createOwner(invalidOwner);
    ResponseValidator.assertStatusCode(response.getStatusCode(), 400);
    ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");
}
```

- **Given:** owner with alphabetic telephone.
- **When:** `POST /owners`.
- **Then:** 400 with RFC 7807 body.

---

### Block 2 — Parameterized 404 lookups

```java
@ParameterizedTest(name = "GET /owners/{0} should return {1}")
@CsvSource({ "99999, 404", "0, 404" })
void shouldReturnExpectedStatusForInvalidOwnerIds(int ownerId, int expectedStatus) {
    ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(...);
}
```

- **When:** invalid IDs are requested.
- **Then:** expected status; optional ProblemDetail when body is present.

---

### Block 3 — Delete unknown owner

```java
Response response = ownersClient.deleteOwner(99999);
ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
```

---

## How to run

```bash
mvn test -Dtest=OwnersNegativeApiTest
```
