package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.User;
import io.restassured.filter.Filter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UsersClient extends ApiClient {

    private static final String USERS_PATH = "/users";

    public UsersClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public UsersClient(RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public static UsersClient secured(Filter... additionalFilters) {
        return new UsersClient(buildSecureRequestSpec(additionalFilters));
    }

    public static UsersClient withCredentials(String username, String password, Filter... additionalFilters) {
        return new UsersClient(buildAuthenticatedRequestSpec(
                com.portfolio.petclinic.utils.ConfigLoader.getSecureBaseUri(),
                username,
                password,
                additionalFilters
        ));
    }

    public Response createUser(User user) {
        return given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post(USERS_PATH);
    }
}
