package com.portfolio.petclinic.tests.advanced;

import com.github.tomakehurst.wiremock.client.WireMock;
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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class WireMockRetryIntegrationTest extends com.portfolio.petclinic.base.BaseTest {

    private WireMockSupport wireMockSupport;

    @BeforeEach
    void startWireMock() {
        wireMockSupport = new WireMockSupport();
    }

    @AfterEach
    void stopWireMock() {
        if (wireMockSupport != null) {
            wireMockSupport.close();
        }
    }

    @Test
    @Story("Retry integration")
    @DisplayName("Should retry downstream webhook after transient failure and eventually succeed")
    void shouldRetryDownstreamWebhookAfterTransientFailure() {
        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .inScenario("retry")
                .whenScenarioStateIs("Started")
                .willSetStateTo("SecondAttempt")
                .willReturn(aResponse().withStatus(503).withBody("{\"error\":\"temporary\"}")));

        wireMockSupport.server().stubFor(post(urlEqualTo("/audit/owner-created"))
                .inScenario("retry")
                .whenScenarioStateIs("SecondAttempt")
                .willReturn(aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"accepted\":true}")));

        AuditNotificationClient client = new AuditNotificationClient(wireMockSupport.baseUrl());
        Owner owner = ownersClient.createOwner(TestDataFactory.buildOwner()).as(Owner.class);
        OwnerCreatedEvent event = new OwnerCreatedEvent(owner.getId(), owner.getFirstName(), owner.getLastName());

        Response firstAttempt = client.notifyOwnerCreated(event);
        ResponseValidator.assertStatusCode(firstAttempt.getStatusCode(), 503);

        Response secondAttempt = client.notifyOwnerCreated(event);
        ResponseValidator.assertStatusCode(secondAttempt.getStatusCode(), 202);
        assertThat(secondAttempt.jsonPath().getBoolean("accepted"), is(true));

        WireMock.verify(exactly(2), postRequestedFor(urlEqualTo("/audit/owner-created")));
        ownersClient.deleteOwner(owner.getId());
    }
}
