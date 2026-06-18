package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.Owner;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OwnersClient extends ApiClient {

    private static final String OWNERS_PATH = "/owners";

    public Response getAllOwners() {
        return given()
            .spec(requestSpec)
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

    public Response deleteOwner(int ownerId) {
        return given()
            .spec(requestSpec)
            .pathParam("ownerId", ownerId)
            .when()
            .delete(OWNERS_PATH + "/{ownerId}");
    }
}
