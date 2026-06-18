# Fluxo — Ciclo de vida do pet

**Arquivo fonte:** [`PetLifecycleFlowTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/flows/PetLifecycleFlowTest.java)

---

## Objetivo

Esta classe demonstra **fluxos de negócio multi-etapa** que orquestram vários clients de API:

1. Criar owner → criar pet → adicionar visita → atualizar nome do pet → buscar owner com pets/visitas aninhados
2. Validar sequência de chamadas via `NetworkInspector`
3. Produzir payload audível `OwnerCreatedEvent` para consumidores downstream

---

## Pré-requisitos

| Item | Detalhe |
|------|---------|
| **API Petclinic** | CRUD completo disponível |
| **Captura de rede** | filtro `networkCapture` anexado em `BaseTest` |
| **Execução** | `mvn test -Dtest=PetLifecycleFlowTest` |

---

## Passo a passo — bloco a bloco

### Bloco 1 — Teste de ciclo completo

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

- **Dado:** owner e pet type dinâmicos.
- **Quando:** o fluxo completo owner-pet-visit-update é executado.
- **Então:** estado de domínio aninhado é consistente e URIs HTTP capturadas correspondem à sequência esperada.

---

### Bloco 2 — Payload de evento auditável

```java
@Test
void shouldProduceAuditableOwnerCreatedEventPayload() {
    Owner owner = createOwner();
    OwnerCreatedEvent event = new OwnerCreatedEvent(owner.getId(), owner.getFirstName(), owner.getLastName());
    assertThat(event.asPayload().get("event"), is("OWNER_CREATED"));
    ownersClient.deleteOwner(owner.getId());
}
```

- **Dado:** um owner recém-criado.
- **Quando:** o payload do evento é montado.
- **Então:** o mapa JSON contém `event`, `ownerId` e campos de nome para audit/webhook.

---

## Como executar

```bash
mvn test -Dtest=PetLifecycleFlowTest
```

---

## Referências relacionadas

- Network inspector: [`NetworkInspector.java`](../../../../src/test/java/com/portfolio/petclinic/utils/NetworkInspector.java)
- Fluxo WireMock: [`wiremock-notification.md`](../advanced/wiremock-notification.md)
