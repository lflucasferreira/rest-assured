package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.models.Vet;
import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class VetsClient extends ApiClient {

    private static final String VETS_PATH = "/vets";

    public VetsClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public VetsClient(io.restassured.specification.RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public static VetsClient withCredentials(String username, String password, Filter... additionalFilters) {
        return new VetsClient(buildAuthenticatedRequestSpec(
                com.portfolio.petclinic.utils.ConfigLoader.getSecureBaseUri(),
                username,
                password,
                additionalFilters
        ));
    }

    public static VetsClient secured(Filter... additionalFilters) {
        return new VetsClient(buildSecureRequestSpec(additionalFilters));
    }

    public Response getAllVets() {
        return given()
                .spec(requestSpec)
                .when()
                .get(VETS_PATH);
    }

    public Response getVetById(int vetId) {
        return given()
                .spec(requestSpec)
                .pathParam("vetId", vetId)
                .when()
                .get(VETS_PATH + "/{vetId}");
    }

    public Response createVet(Vet vet) {
        return given()
                .spec(requestSpec)
                .body(vet)
                .when()
                .post(VETS_PATH);
    }

    public Response updateVet(int vetId, Vet vet) {
        return given()
                .spec(requestSpec)
                .pathParam("vetId", vetId)
                .body(vet)
                .when()
                .put(VETS_PATH + "/{vetId}");
    }

    public Response deleteVet(int vetId) {
        return given()
                .spec(requestSpec)
                .pathParam("vetId", vetId)
                .when()
                .delete(VETS_PATH + "/{vetId}");
    }
}
