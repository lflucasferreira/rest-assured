# Avançado — WireMock notificações

**Arquivo fonte:** [`WireMockNotificationTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java)

---

## Objetivo

Simula **chamadas de webhook outbound** para um serviço externo de auditoria:

- Stub WireMock retorna `202 Accepted`
- Verifica payload JSON exato com `WireMock.verify`
- Injeção de falha retorna `503` para testes de resiliência

---

## Pré-requisitos

| Item | Detalhe |
|------|---------|
| **WireMock** | Servidor embutido via `WireMockSupport` |
| **Client de audit** | `AuditNotificationClient` aponta para base URL do WireMock |

---

## Passo a passo — bloco a bloco

### Bloco 1 — Setup e teardown

```java
@BeforeEach
void startWireMock() {
    wireMockSupport = new WireMockSupport();
    auditNotificationClient = new AuditNotificationClient(wireMockSupport.baseUrl());
    wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
        .willReturn(aResponse().withStatus(202).withBody("{\"accepted\":true}")));
}

@AfterEach
void stopWireMock() {
    wireMockSupport.close();
}
```

---

### Bloco 2 — Stub e verify do webhook

```java
Owner createdOwner = ownersClient.createOwner(ownerPayload).as(Owner.class);
OwnerCreatedEvent event = new OwnerCreatedEvent(createdOwner.getId(), ...);
Response notificationResponse = auditNotificationClient.notifyOwnerCreated(event);

WireMock.verify(postRequestedFor(urlEqualTo("/audit/owner-created"))
    .withRequestBody(equalToJson("{ \"event\": \"OWNER_CREATED\", ... }", true, true)));
```

- **Dado:** owner criado no Petclinic e webhook mockado pronto.
- **Quando:** o harness de teste notifica o serviço de audit.
- **Então:** o mock recebeu o payload esperado; resposta é 202 com `accepted: true`.

---

### Bloco 3 — Injeção de falha

```java
wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
    .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"service unavailable\"}")));
Response response = auditNotificationClient.notifyOwnerCreated(event);
ResponseValidator.assertStatusCode(response.getStatusCode(), 503);
```

- **Quando:** downstream retorna 503.
- **Então:** o client expõe a falha sem quebrar o harness de teste.

---

## Como executar

```bash
mvn test -Dtest=WireMockNotificationTest
```
