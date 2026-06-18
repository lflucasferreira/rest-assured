package com.portfolio.petclinic.utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class SecureApiProbe {

    private SecureApiProbe() {
    }

    public static boolean isSecureApiAvailable() {
        try {
            Response response = given()
                    .baseUri(ConfigLoader.getSecureBaseUri())
                    .relaxedHTTPSValidation()
                    .when()
                    .get("/owners");

            return response.getStatusCode() == 401 || response.getStatusCode() == 403;
        } catch (Exception exception) {
            return false;
        }
    }
}
