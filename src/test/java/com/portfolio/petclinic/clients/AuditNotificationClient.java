package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.OwnerCreatedEvent;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuditNotificationClient {

    private final String webhookBaseUrl;

    public AuditNotificationClient(String webhookBaseUrl) {
        this.webhookBaseUrl = webhookBaseUrl;
    }

    public Response notifyOwnerCreated(OwnerCreatedEvent event) {
        return given()
                .baseUri(webhookBaseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(event.asPayload())
                .when()
                .post("/audit/owner-created");
    }
}
