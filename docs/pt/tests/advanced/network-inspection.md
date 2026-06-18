# Avançado — Inspeção de rede

**Arquivo fonte:** [`NetworkInspectionTest.java`](../../../../src/test/java/com/portfolio/petclinic/tests/advanced/NetworkInspectionTest.java)

---

## Objetivo

Demonstra **inspeção programática de tráfego HTTP** sem browser:

- `Filter` customizado do Rest Assured captura última request/response
- Extração JsonPath para asserções pontuais
- SLA de tempo de resposta via matcher `time()` do Rest Assured

---

## Passo a passo — bloco a bloco

### Bloco 1 — Capturar última troca

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

- **Dado:** filtro de captura de rede nos clients de API.
- **Quando:** `GET /pets` é executado.
- **Então:** método, URI, status, SLA e fragmento do corpo são assertáveis; trace anexado ao Allure.

---

### Bloco 2 — Extração JsonPath

```java
String firstOwnerLastName = response.jsonPath().getString("[0].lastName");
int firstOwnerPetCount = response.jsonPath().getList("[0].pets").size();
assertThat(response.jsonPath().getString("[0].pets[0].type.name"), is(not(emptyOrNullString())));
```

- **Quando:** JSON aninhado é consultado sem mapeamento POJO completo.
- **Então:** asserções focadas em campos aninhados.

---

### Bloco 3 — Enforcement de SLA

```java
response.then()
    .statusCode(200)
    .time(lessThan(ConfigLoader.getMaxResponseTimeMs()));
```

- **Então:** endpoint de pet types responde dentro do limiar configurado.

---

## Como executar

```bash
mvn test -Dtest=NetworkInspectionTest
```
