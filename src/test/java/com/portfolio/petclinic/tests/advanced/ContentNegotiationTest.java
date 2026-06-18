package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("HTTP Contract")
class ContentNegotiationTest extends BaseTest {

    @Test
    @Story("Accept header")
    @DisplayName("GET /owners with Accept application/xml should not return success JSON contract")
    void shouldRejectOrFailXmlAcceptHeader() {
        Response response = given()
                .baseUri(ConfigLoader.getBaseUri())
                .accept("application/xml")
                .relaxedHTTPSValidation()
                .when()
                .get("/owners");

        assertThat(response.getStatusCode(), is(500));
    }

    @Test
    @Story("Content-Type header")
    @DisplayName("POST /owners without Content-Type should fail")
    void shouldFailWhenContentTypeHeaderIsMissing() {
        Owner owner = TestDataFactory.buildOwner();

        Response response = given()
                .baseUri(ConfigLoader.getBaseUri())
                .relaxedHTTPSValidation()
                .body(owner)
                .when()
                .post("/owners");

        assertThat(response.getStatusCode(), is(500));
    }

    @Test
    @Story("Accept header")
    @DisplayName("GET /owners with Accept application/json should succeed")
    void shouldAcceptJsonResponses() {
        Response response = given()
                .baseUri(ConfigLoader.getBaseUri())
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation()
                .when()
                .get("/owners");

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getContentType(), org.hamcrest.Matchers.containsString("application/json"));
    }
}
