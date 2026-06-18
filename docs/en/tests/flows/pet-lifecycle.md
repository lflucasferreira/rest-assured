# Flow — Pet lifecycle

**Source file:** [`PetLifecycleFlowTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/flows/PetLifecycleFlowTest.java)

---

## Purpose

This class demonstrates **multi-step business flows** that orchestrate several API clients:

1. Create owner → create pet → add visit → update pet name → fetch owner with nested pets/visits
2. Validate network call sequence via `NetworkInspector`
3. Produce auditable `OwnerCreatedEvent` payload for downstream consumers

---

## Prerequisites

| Item | Detail |
|------|--------|
| **Petclinic API** | Full CRUD available |
| **Network capture** | `networkCapture` filter attached in `BaseTest` |
| **Execution** | `mvn test -Dtest=PetLifecycleFlowTest` |

---

## Step-by-step — block by block

### Block 1 — Full lifecycle test

```java
@Test
void shouldCompleteOwnerPetVisitLifecycle() {
    networkCapture.reset();
    Owner owner = createOwner();
    Pet pet = createPet(owner.getId(), petType);
    addVisit(owner.getId(), pet.getId());
    updatePetName(pet, pet.getName() + "-VIP");
    Owner ownerWithPets = fetchOwnerWithPets(owner.getId());

    assertThat(ownerWithPets.getPets(), hasSize(1));
    NetworkInspector.assertResponseSequenceContains(
        networkCapture, "/owners", "/owners/", "/owners/", "/pets/", "/owners/");
    cleanup(owner.getId(), pet.getId());
}
```

- **Given:** dynamic owner and pet type.
- **When:** the full owner-pet-visit-update flow runs.
- **Then:** nested domain state is consistent and captured HTTP URIs match expected sequence.

---

### Block 2 — Auditable event payload

```java
@Test
void shouldProduceAuditableOwnerCreatedEventPayload() {
    Owner owner = createOwner();
    OwnerCreatedEvent event = new OwnerCreatedEvent(owner.getId(), owner.getFirstName(), owner.getLastName());
    assertThat(event.asPayload().get("event"), is("OWNER_CREATED"));
    ownersClient.deleteOwner(owner.getId());
}
```

- **Given:** a newly created owner.
- **When:** event payload is built.
- **Then:** JSON map contains `event`, `ownerId`, and name fields for audit/webhook consumers.

---

## How to run

```bash
mvn test -Dtest=PetLifecycleFlowTest
```

---

## Related references

- Network inspector: [`NetworkInspector.java`](../../../../src/test/java/com/portfolio/petclinic/utils/NetworkInspector.java)
- WireMock flow: [`wiremock-notification.md`](../advanced/wiremock-notification.md)
