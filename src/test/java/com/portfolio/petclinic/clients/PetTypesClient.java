package com.portfolio.petclinic.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetTypesClient extends ApiClient {

    private static final String PET_TYPES_PATH = "/pettypes";

    public Response getAllPetTypes() {
        return given()
            .spec(requestSpec)
            .when()
            .get(PET_TYPES_PATH);
    }
}
