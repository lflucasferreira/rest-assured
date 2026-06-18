package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.utils.ConfigLoader;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.lessThan;

@Epic("Spring Petclinic API")
@Feature("Performance")
@Tag("performance")
class PerformanceSmokeTest extends BaseTest {

    @Test
    @Story("Response SLA")
    @DisplayName("GET /owners should meet response time SLA")
    void ownersListShouldMeetSla() {
        ownersClient.getAllOwners().then()
                .statusCode(200)
                .time(lessThan(ConfigLoader.getMaxResponseTimeMs()));
    }

    @Test
    @Story("Response SLA")
    @DisplayName("GET /pets should meet response time SLA")
    void petsListShouldMeetSla() {
        petsClient.getAllPets().then()
                .statusCode(200)
                .time(lessThan(ConfigLoader.getMaxResponseTimeMs()));
    }

    @Test
    @Story("Response SLA")
    @DisplayName("GET /visits should meet response time SLA when visits endpoint is available")
    void visitsListShouldMeetSla() {
        Response response = visitsClient.getAllVisits();
        org.junit.jupiter.api.Assumptions.assumeTrue(
                response.getStatusCode() == 200,
                "Skipping visits SLA check because visits endpoint returned " + response.getStatusCode()
        );
        response.then().time(lessThan(ConfigLoader.getMaxResponseTimeMs()));
    }
}
