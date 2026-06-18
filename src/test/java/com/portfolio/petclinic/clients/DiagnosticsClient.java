package com.portfolio.petclinic.clients;

import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DiagnosticsClient extends ApiClient {

    public DiagnosticsClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public Response triggerFailingEndpoint() {
        return given()
                .spec(requestSpec)
                .when()
                .get("/oops");
    }
}
