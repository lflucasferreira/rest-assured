package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.OwnerCreatedEvent;
import com.portfolio.petclinic.utils.ConfigLoader;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuditNotificationClient {

    private final String webhookBaseUrl;
    private final int timeoutMs;

    public AuditNotificationClient(String webhookBaseUrl) {
        this(webhookBaseUrl, ConfigLoader.getWebhookTimeoutMs());
    }

    public AuditNotificationClient(String webhookBaseUrl, int timeoutMs) {
        this.webhookBaseUrl = webhookBaseUrl;
        this.timeoutMs = timeoutMs;
    }

    public Response notifyOwnerCreated(OwnerCreatedEvent event) {
        return given()
                .config(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMs)
                        .setParam("http.socket.timeout", timeoutMs)))
                .baseUri(webhookBaseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(event.asPayload())
                .when()
                .post("/audit/owner-created");
    }
}
