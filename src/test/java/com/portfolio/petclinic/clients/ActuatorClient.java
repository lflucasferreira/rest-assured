package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.utils.ConfigLoader;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ActuatorClient {

    private final String rootUri;

    public ActuatorClient() {
        this(ConfigLoader.getPetclinicRootUri());
    }

    public ActuatorClient(String rootUri) {
        this.rootUri = rootUri;
    }

    public Response getHealth() {
        return given()
                .baseUri(rootUri)
                .relaxedHTTPSValidation()
                .accept("application/json")
                .when()
                .get("/actuator/health");
    }

    public Response getInfo() {
        return given()
                .baseUri(rootUri)
                .relaxedHTTPSValidation()
                .accept("application/json")
                .when()
                .get("/actuator/info");
    }
}
