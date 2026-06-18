# API — Owners (CRUD & filter)

**Source file:** [`OwnersApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/OwnersApiTest.java)

---

## Purpose

This class covers **happy-path contract tests** for the owners resource:

- List all owners with schema validation
- Create a new owner and verify persisted fields
- Get owner by ID
- Filter owners by `lastName` query parameter

---

## Prerequisites

| Item | Detail |
|------|--------|
| **Petclinic API** | Running at `http://localhost:9966/petclinic/api` (Docker or local) |
| **Seed data** | At least one owner must exist for read/filter tests |
| **Execution** | `mvn test -Dtest=OwnersApiTest` |

---

## Allure annotations

| Annotation | Value |
|------------|-------|
| `@Epic` | Spring Petclinic API |
| `@Feature` | Owners |

---

## Rest Assured concepts

| Concept | Usage in this file |
|---------|-------------------|
| **`OwnersClient`** | Encapsulates `/owners` HTTP calls |
| **`ResponseValidator`** | Status code and JSON schema assertions |
| **`TestDataFactory.buildOwner()`** | Dynamic owner payload for POST |
| **POJO mapping** | `response.as(Owner.class)` and `Owner[].class` |
| **Hamcrest** | `assertThat` for field-level checks |
| **Cleanup** | `deleteOwner` after create test |

---

## Step-by-step — block by block

### Block 1 — List owners

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

- **Given:** the Petclinic API is available.
- **When:** `GET /owners` is called.
- **Then:** status 200, non-empty list, first item matches `owner-schema.json`.

---

### Block 2 — Create owner

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

- **Given:** dynamically generated owner data.
- **When:** `POST /owners` is sent.
- **Then:** status 201, response matches schema, fields match payload; owner is deleted in cleanup.

---

### Block 3 — Get by ID

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

- **Given:** an existing owner from the collection.
- **When:** `GET /owners/{id}` is called.
- **Then:** status 200 and ID matches.

---

### Block 4 — Filter by last name

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

- **Given:** owners exist in the system.
- **When:** `GET /owners?lastName=` is called.
- **Then:** all returned owners share the requested last name.

---

## How to run

```bash
mvn test -Dtest=OwnersApiTest
docker compose up --abort-on-container-exit --exit-code-from tests
```

---

## Related references

- Client: [`OwnersClient.java`](../../../../src/test/java/com/portfolio/petclinic/clients/OwnersClient.java)
- Negative scenarios: [`OwnersNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/OwnersNegativeApiTest.java)
- Schema: [`owner-schema.json`](../../../../src/test/resources/schemas/owner-schema.json)
