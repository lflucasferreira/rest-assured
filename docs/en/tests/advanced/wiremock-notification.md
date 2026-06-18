# Advanced — WireMock notifications

**Source file:** [`WireMockNotificationTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/WireMockNotificationTest.java)

---

## Purpose

Simulates **outbound webhook calls** to an external audit service:

- WireMock stub returns `202 Accepted`
- Verify exact JSON payload with `WireMock.verify`
- Fault injection returns `503` for resilience testing

---

## Prerequisites

| Item | Detail |
|------|--------|
| **WireMock** | Embedded server via `WireMockSupport` |
| **Audit client** | `AuditNotificationClient` points to WireMock base URL |

---

## Step-by-step — block by block

### Block 1 — Setup and teardown

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

### Block 2 — Stub and verify webhook

```java
Owner createdOwner = ownersClient.createOwner(ownerPayload).as(Owner.class);
OwnerCreatedEvent event = new OwnerCreatedEvent(createdOwner.getId(), ...);
Response notificationResponse = auditNotificationClient.notifyOwnerCreated(event);

WireMock.verify(postRequestedFor(urlEqualTo("/audit/owner-created"))
    .withRequestBody(equalToJson("{ \"event\": \"OWNER_CREATED\", ... }", true, true)));
```

- **Given:** owner created in Petclinic and mock webhook ready.
- **When:** test harness notifies audit service.
- **Then:** mock received expected payload; response is 202 with `accepted: true`.

---

### Block 3 — Fault injection

```java
wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
    .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"service unavailable\"}")));
Response response = auditNotificationClient.notifyOwnerCreated(event);
ResponseValidator.assertStatusCode(response.getStatusCode(), 503);
```

- **When:** downstream returns 503.
- **Then:** client surfaces failure without crashing the test harness.

---

## How to run

```bash
mvn test -Dtest=WireMockNotificationTest
```
