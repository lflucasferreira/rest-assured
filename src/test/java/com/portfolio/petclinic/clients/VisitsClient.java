package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.Visit;
import com.portfolio.petclinic.models.VisitFields;
import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class VisitsClient extends ApiClient {

    private static final String VISITS_PATH = "/visits";

    public VisitsClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public Response getAllVisits() {
        return given()
                .spec(requestSpec)
                .when()
                .get(VISITS_PATH);
    }

    public Response getVisitById(int visitId) {
        return given()
                .spec(requestSpec)
                .pathParam("visitId", visitId)
                .when()
                .get(VISITS_PATH + "/{visitId}");
    }

    public Response createVisit(VisitFields visitFields) {
        return given()
                .spec(requestSpec)
                .body(visitFields)
                .when()
                .post(VISITS_PATH);
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

    public Response updateVisit(int visitId, Visit visit) {
        return given()
                .spec(requestSpec)
                .pathParam("visitId", visitId)
                .body(visit)
                .when()
                .put(VISITS_PATH + "/{visitId}");
    }

    public Response deleteVisit(int visitId) {
        return given()
                .spec(requestSpec)
                .pathParam("visitId", visitId)
                .when()
                .delete(VISITS_PATH + "/{visitId}");
    }
}
