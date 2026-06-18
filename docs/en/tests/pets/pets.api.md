# API — Pets (CRUD)

**Source file:** [`PetsApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/PetsApiTest.java)

---

## Purpose

This class validates the **pets resource** end-to-end:

- List and get pets with schema validation
- Create pet under owner (`POST /owners/{ownerId}/pets`)
- Update pet (`PUT /pets/{id}` → 204)
- Delete pet (`DELETE /pets/{id}` → 204, then 404 on GET)

---

## Prerequisites

| Item | Detail |
|------|--------|
| **Petclinic API** | Running with pet types seeded |
| **Pet types** | Loaded in `@BeforeEach` from `GET /pettypes` |
| **Execution** | `mvn test -Dtest=PetsApiTest` |

---

## Rest Assured concepts

| Concept | Usage in this file |
|---------|-------------------|
| **`@BeforeEach` / `@AfterEach`** | Load default pet type; cleanup created pets/owners |
| **`PetsClient`** | `/pets` and nested owner-pet endpoints |
| **`PetFields` vs `Pet`** | Payload vs full entity for create/update |
| **204 No Content** | Update and delete return empty body |

---

## Step-by-step — block by block

### Block 1 — Lifecycle hooks

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

- **Given:** each test needs a valid pet type ID.
- **When:** test completes (pass or fail).
- **Then:** created pets and owners are removed to keep the environment clean.

---

### Block 2 — List & get pets

Tests `shouldReturnAllPetsWithValidStructure` and `shouldReturnPetById` validate collection and single-resource responses against `pet-schema.json`.

---

### Block 3 — Create pet for owner

```java
Owner owner = createOwner();
PetFields petPayload = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());
Response createResponse = petsClient.createPetForOwner(owner.getId(), petPayload);
ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);
```

- **Given:** a new owner and dynamic pet payload.
- **When:** `POST /owners/{ownerId}/pets` is called.
- **Then:** status 201, pet has ID, name, ownerId, and type.

> Petclinic REST exposes pet creation as nested under owners, not `POST /pets`.

---

### Block 4 — Update and delete

Update expects **204**; verification is done via subsequent `GET`. Delete expects **204** followed by **404** on retrieval.

---

## How to run

```bash
mvn test -Dtest=PetsApiTest
```

---

## Related references

- Client: [`PetsClient.java`](../../../../src/test/java/com/portfolio/petclinic/clients/PetsClient.java)
- Negative scenarios: [`PetsNegativeApiTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/negative/PetsNegativeApiTest.java)
