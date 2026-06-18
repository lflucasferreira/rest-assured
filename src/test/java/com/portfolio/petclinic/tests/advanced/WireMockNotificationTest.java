package com.portfolio.petclinic.tests.advanced;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.clients.AuditNotificationClient;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.OwnerCreatedEvent;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class WireMockNotificationTest extends BaseTest {

    private WireMockSupport wireMockSupport;
    private AuditNotificationClient auditNotificationClient;

    @BeforeEach
    void startWireMock() {
        wireMockSupport = new WireMockSupport();
        auditNotificationClient = new AuditNotificationClient(wireMockSupport.baseUrl());

        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"accepted\":true}")));
    }

    @AfterEach
    void stopWireMock() {
        if (wireMockSupport != null) {
            wireMockSupport.close();
        }
    }

    @Test
    @Story("External service mocking")
    @DisplayName("Should stub external webhook with WireMock and verify outbound payload")
    void shouldStubExternalWebhookAndVerifyOutboundPayload() {
        // Given: a newly created owner in Petclinic and a mocked audit webhook
        Owner ownerPayload = TestDataFactory.buildOwner();
        Response createOwnerResponse = ownersClient.createOwner(ownerPayload);
        ResponseValidator.assertStatusCode(createOwnerResponse.getStatusCode(), 201);

        Owner createdOwner = createOwnerResponse.as(Owner.class);
        OwnerCreatedEvent event = new OwnerCreatedEvent(
                createdOwner.getId(),
                createdOwner.getFirstName(),
                createdOwner.getLastName()
        );

        // When: test harness notifies external audit service (mocked)
        Response notificationResponse = auditNotificationClient.notifyOwnerCreated(event);

        // Then: mock received the expected contract and responded with 202
        ResponseValidator.assertStatusCode(notificationResponse.getStatusCode(), 202);
        assertThat(notificationResponse.jsonPath().getBoolean("accepted"), is(true));

        WireMock.verify(postRequestedFor(urlEqualTo("/audit/owner-created"))
                .withRequestBody(equalToJson("""
                        {
                          "event": "OWNER_CREATED",
                          "ownerId": %d,
                          "firstName": "%s",
                          "lastName": "%s"
                        }
                        """.formatted(createdOwner.getId(), createdOwner.getFirstName(), createdOwner.getLastName()),
                        true, true)));

        ownersClient.deleteOwner(createdOwner.getId());
    }

    @Test
    @Story("Fault injection")
    @DisplayName("Should simulate downstream failure using WireMock fault injection")
    void shouldSimulateDownstreamFailureUsingWireMock() {
        // Given: webhook configured to return server error
        wireMockSupport.server().resetAll();
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"service unavailable\"}")));

        OwnerCreatedEvent event = new OwnerCreatedEvent(42, "Jane", "Doe");

        // When/Then: client receives downstream failure status
        Response response = auditNotificationClient.notifyOwnerCreated(event);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 503);
        assertThat(response.getBody().asString(), org.hamcrest.Matchers.containsString("service unavailable"));
    }
}
