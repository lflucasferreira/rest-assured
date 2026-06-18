package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.PetType;
import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetTypesClient extends ApiClient {

    private static final String PET_TYPES_PATH = "/pettypes";

    public PetTypesClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public Response getAllPetTypes() {
        return given()
                .spec(requestSpec)
                .when()
                .get(PET_TYPES_PATH);
    }

    public Response getPetTypeById(int petTypeId) {
        return given()
                .spec(requestSpec)
                .pathParam("petTypeId", petTypeId)
                .when()
                .get(PET_TYPES_PATH + "/{petTypeId}");
    }

    public Response createPetType(PetType petType) {
        return given()
                .spec(requestSpec)
                .body(petType)
                .when()
                .post(PET_TYPES_PATH);
    }

    public Response updatePetType(int petTypeId, PetType petType) {
        return given()
                .spec(requestSpec)
                .pathParam("petTypeId", petTypeId)
                .body(petType)
                .when()
                .put(PET_TYPES_PATH + "/{petTypeId}");
    }

    public Response deletePetType(int petTypeId) {
        return given()
                .spec(requestSpec)
                .pathParam("petTypeId", petTypeId)
                .when()
                .delete(PET_TYPES_PATH + "/{petTypeId}");
    }
}
