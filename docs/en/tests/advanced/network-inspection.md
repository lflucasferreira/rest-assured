# Advanced — Network inspection

**Source file:** [`NetworkInspectionTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java)

---

## Purpose

Demonstrates **programmatic HTTP traffic inspection** without a browser:

- Custom Rest Assured `Filter` captures last request/response
- JsonPath extraction for targeted assertions
- Response time SLA via Rest Assured `time()` matcher

---

## Step-by-step — block by block

### Block 1 — Capture last exchange

```java
@Test
void shouldCaptureAndInspectLastHttpExchange() {
    Response response = petsClient.getAllPets();
    NetworkInspector.attachLastExchangeToAllure(networkCapture);
    NetworkInspector.assertLastRequestMethod(networkCapture, "GET");
    NetworkInspector.assertLastRequestUriContains(networkCapture, "/pets");
    NetworkInspector.assertLastResponseTimeUnder(networkCapture, ConfigLoader.getMaxResponseTimeMs());
}
```

- **Given:** network capture filter on API clients.
- **When:** `GET /pets` runs.
- **Then:** method, URI, status, SLA, and body fragment are assertable; trace attached to Allure.

---

### Block 2 — JsonPath extraction

```java
String firstOwnerLastName = response.jsonPath().getString("[0].lastName");
int firstOwnerPetCount = response.jsonPath().getList("[0].pets").size();
assertThat(response.jsonPath().getString("[0].pets[0].type.name"), is(not(emptyOrNullString())));
```

- **When:** nested JSON is queried without full POJO mapping.
- **Then:** focused assertions on nested fields.

---

### Block 3 — SLA enforcement

```java
response.then()
    .statusCode(200)
    .time(lessThan(ConfigLoader.getMaxResponseTimeMs()));
```

- **Then:** pet types endpoint responds within configured threshold.

---

## How to run

```bash
mvn test -Dtest=NetworkInspectionTest
```
