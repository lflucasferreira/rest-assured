package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.VisitFields;
import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class VisitsClient extends ApiClient {

    public VisitsClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public Response addVisit(int ownerId, int petId, VisitFields visitFields) {
        return given()
                .spec(requestSpec)
                .pathParam("ownerId", ownerId)
                .pathParam("petId", petId)
                .body(visitFields)
                .when()
                .post("/owners/{ownerId}/pets/{petId}/visits");
    }
}
