package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.Specialty;
import io.restassured.filter.Filter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class SpecialtiesClient extends ApiClient {

    private static final String SPECIALTIES_PATH = "/specialties";

    public SpecialtiesClient(Filter... additionalFilters) {
        super(additionalFilters);
    }

    public SpecialtiesClient(io.restassured.specification.RequestSpecification requestSpecification) {
        super(requestSpecification);
    }

    public static SpecialtiesClient withCredentials(String username, String password, Filter... additionalFilters) {
        return new SpecialtiesClient(buildAuthenticatedRequestSpec(
                com.portfolio.petclinic.utils.ConfigLoader.getSecureBaseUri(),
                username,
                password,
                additionalFilters
        ));
    }

    public static SpecialtiesClient secured(Filter... additionalFilters) {
        return new SpecialtiesClient(buildSecureRequestSpec(additionalFilters));
    }

    public Response getAllSpecialties() {
        return given()
                .spec(requestSpec)
                .when()
                .get(SPECIALTIES_PATH);
    }

    public Response getSpecialtyById(int specialtyId) {
        return given()
                .spec(requestSpec)
                .pathParam("specialtyId", specialtyId)
                .when()
                .get(SPECIALTIES_PATH + "/{specialtyId}");
    }

    public Response createSpecialty(Specialty specialty) {
        return given()
                .spec(requestSpec)
                .body(specialty)
                .when()
                .post(SPECIALTIES_PATH);
    }

    public Response updateSpecialty(int specialtyId, Specialty specialty) {
        return given()
                .spec(requestSpec)
                .pathParam("specialtyId", specialtyId)
                .body(specialty)
                .when()
                .put(SPECIALTIES_PATH + "/{specialtyId}");
    }

    public Response deleteSpecialty(int specialtyId) {
        return given()
                .spec(requestSpec)
                .pathParam("specialtyId", specialtyId)
                .when()
                .delete(SPECIALTIES_PATH + "/{specialtyId}");
    }
}
