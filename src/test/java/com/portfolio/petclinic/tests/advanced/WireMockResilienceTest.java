package com.portfolio.petclinic.tests.advanced;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.portfolio.petclinic.clients.AuditNotificationClient;
import com.portfolio.petclinic.models.OwnerCreatedEvent;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.WireMockSupport;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.http.Fault.CONNECTION_RESET_BY_PEER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class WireMockResilienceTest {

    private WireMockSupport wireMockSupport;
    private AuditNotificationClient auditNotificationClient;

    @BeforeEach
    void startWireMock() {
        wireMockSupport = new WireMockSupport();
        auditNotificationClient = new AuditNotificationClient(wireMockSupport.baseUrl());
    }

    @AfterEach
    void stopWireMock() {
        if (wireMockSupport != null) {
            wireMockSupport.close();
        }
    }

    @Test
    @Story("Downstream timeout")
    @DisplayName("Should fail when downstream webhook exceeds client timeout")
    void shouldFailWhenDownstreamWebhookExceedsTimeout() {
        // Given: webhook responds slower than client timeout
        int clientTimeoutMs = ConfigLoader.getWebhookTimeoutMs();
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withFixedDelay(clientTimeoutMs + 2000)
                        .withBody("{\"accepted\":true}")));

        AuditNotificationClient timeoutClient = new AuditNotificationClient(
                wireMockSupport.baseUrl(),
                clientTimeoutMs
        );
        OwnerCreatedEvent event = new OwnerCreatedEvent(7, "Timeout", "Case");

        // When/Then
        assertThrows(Exception.class, () -> timeoutClient.notifyOwnerCreated(event));
    }

    @Test
    @Story("Invalid downstream payload")
    @DisplayName("Should surface malformed JSON from downstream webhook")
    void shouldSurfaceMalformedJsonFromDownstreamWebhook() {
        // Given: webhook returns invalid JSON body
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{invalid-json")));

        OwnerCreatedEvent event = new OwnerCreatedEvent(8, "Invalid", "Payload");

        // When
        Response response = auditNotificationClient.notifyOwnerCreated(event);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.getBody().asString(), containsString("invalid-json"));
        assertThat(response.getBody().asString(), not(containsString("\"accepted\"")));
    }

    @Test
    @Story("Connection fault")
    @DisplayName("Should handle connection reset fault from downstream webhook")
    void shouldHandleConnectionResetFaultFromDownstreamWebhook() {
        // Given: webhook simulates connection reset
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse().withFault(CONNECTION_RESET_BY_PEER)));

        OwnerCreatedEvent event = new OwnerCreatedEvent(9, "Connection", "Fault");

        // When/Then
        assertThrows(Exception.class, () -> auditNotificationClient.notifyOwnerCreated(event));

        WireMock.verify(postRequestedFor(urlEqualTo("/audit/owner-created")));
    }

    @Test
    @Story("Delayed response")
    @DisplayName("Should accept delayed but within-timeout downstream response")
    void shouldAcceptDelayedButWithinTimeoutDownstreamResponse() {
        // Given: webhook responds within client timeout
        int clientTimeoutMs = ConfigLoader.getWebhookTimeoutMs();
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withFixedDelay(Math.max(500, clientTimeoutMs / 2))
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"accepted\":true}")));

        AuditNotificationClient resilientClient = new AuditNotificationClient(
                wireMockSupport.baseUrl(),
                clientTimeoutMs
        );
        OwnerCreatedEvent event = new OwnerCreatedEvent(10, "Delayed", "Success");

        // When
        Response response = resilientClient.notifyOwnerCreated(event);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 202);
        assertThat(response.jsonPath().getBoolean("accepted"), org.hamcrest.Matchers.is(true));
    }
}
