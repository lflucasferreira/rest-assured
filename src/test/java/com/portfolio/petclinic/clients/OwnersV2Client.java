package com.portfolio.petclinic.clients;

import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OwnersV2Client extends ApiClient {

    private static final String OWNERS_V2_PATH = "/v2/owners";

    public OwnersV2Client(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public Response getOwnersPage(Integer page, Integer size, String lastName) {
        var request = given().spec(requestSpec);
        if (page != null) {
            request = request.queryParam("page", page);
        }
        if (size != null) {
            request = request.queryParam("size", size);
        }
        if (lastName != null) {
            request = request.queryParam("lastName", lastName);
        }
        return request.when().get(OWNERS_V2_PATH);
    }
}
