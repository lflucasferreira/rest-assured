package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.Owner;
import io.restassured.filter.Filter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OwnersClient extends ApiClient {

    private static final String OWNERS_PATH = "/owners";

    public OwnersClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public OwnersClient(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public static OwnersClient secured(Filter... additionalFilters) {
        return new OwnersClient(buildSecureRequestSpec(additionalFilters));
    }

    public static OwnersClient withCredentials(String username, String password, Filter... additionalFilters) {
        return new OwnersClient(buildAuthenticatedRequestSpec(
                com.portfolio.petclinic.utils.ConfigLoader.getSecureBaseUri(),
                username,
                password,
                additionalFilters
        ));
    }

    public Response getAllOwners() {
        return given()
                .spec(requestSpec)
                .when()
                .get(OWNERS_PATH);
    }

    public Response getAllOwners(String ifNoneMatchEtag) {
        return given()
                .spec(requestSpec)
                .header("If-None-Match", ifNoneMatchEtag)
                .when()
                .get(OWNERS_PATH);
    }

    public Response getAllOwnersByLastName(String lastName) {
        return given()
                .spec(requestSpec)
                .queryParam("lastName", lastName)
                .when()
                .get(OWNERS_PATH);
    }

    public Response getOwnerById(int ownerId) {
        return given()
                .spec(requestSpec)
                .pathParam("ownerId", ownerId)
                .when()
                .get(OWNERS_PATH + "/{ownerId}");
    }

    public Response createOwner(Owner owner) {
        return given()
                .spec(requestSpec)
                .body(owner)
                .when()
                .post(OWNERS_PATH);
    }

    public Response updateOwner(int ownerId, Owner owner) {
        return given()
                .spec(requestSpec)
                .pathParam("ownerId", ownerId)
                .body(owner)
                .when()
                .put(OWNERS_PATH + "/{ownerId}");
    }

    public Response deleteOwner(int ownerId) {
        return given()
                .spec(requestSpec)
                .pathParam("ownerId", ownerId)
                .when()
                .delete(OWNERS_PATH + "/{ownerId}");
    }

    public Response getOwnerPetById(int ownerId, int petId) {
        return given()
                .spec(requestSpec)
                .pathParam("ownerId", ownerId)
                .pathParam("petId", petId)
                .when()
                .get(OWNERS_PATH + "/{ownerId}/pets/{petId}");
    }
}
