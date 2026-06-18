package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.utils.ConfigLoader;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OpenApiClient {

    public Response getApiDocs() {
        return given()
                .baseUri(ConfigLoader.getPetclinicRootUri())
                .relaxedHTTPSValidation()
                .accept("application/json")
                .when()
                .get(ConfigLoader.getOpenApiDocsPath());
    }
}
