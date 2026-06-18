# Negative — Pets

**Source file:** [`PetsNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java)

---

## Purpose

Covers **error paths** for the pets resource:

- `404` for non-existent pet IDs (parameterized)
- `404` when updating missing pet
- `404` when creating pet for non-existent owner (parameterized)

---

## Step-by-step — block by block

### Block 1 — Parameterized GET 404

```java
@ParameterizedTest(name = "GET /pets/{0} should return {1}")
@CsvSource({ "99999, 404", "0, 404" })
void shouldReturnExpectedStatusForInvalidPetIds(int petId, int expectedStatus) {
    ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(...);
}
```

---

### Block 2 — Update missing pet

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

### Block 3 — Create pet for missing owner

```java
@ParameterizedTest(name = "POST pet to missing owner {0} should return 404")
@ValueSource(ints = {99999, 50000})
void shouldRejectPetCreationForMissingOwner(int ownerId) {
    Response response = petsClient.createPetForOwner(ownerId, petFields);
    ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
}
```

---

## How to run

```bash
mvn test -Dtest=PetsNegativeApiTest
```
