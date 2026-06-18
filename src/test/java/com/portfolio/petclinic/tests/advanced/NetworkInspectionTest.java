package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.NetworkInspector;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class NetworkInspectionTest extends BaseTest {

    @Test
    @Story("Network interception")
    @DisplayName("Should capture and inspect HTTP exchange metadata via custom filter")
    void shouldCaptureAndInspectLastHttpExchange() {
        // Given: network capture filter is attached to API clients
        // When: listing pets
        Response response = petsClient.getAllPets();

        // Then: captured traffic can be inspected programmatically
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        NetworkInspector.attachLastExchangeToAllure(networkCapture);

        NetworkInspector.assertLastRequestMethod(networkCapture, "GET");
        NetworkInspector.assertLastRequestUriContains(networkCapture, "/pets");
        NetworkInspector.assertLastResponseStatus(networkCapture, 200);
        NetworkInspector.assertLastResponseTimeUnder(networkCapture, ConfigLoader.getMaxResponseTimeMs());
        NetworkInspector.assertLastResponseBodyContains(networkCapture, "\"name\"");
    }

    @Test
    @Story("JsonPath extraction")
    @DisplayName("Should extract nested JSON values with JsonPath for targeted assertions")
    void shouldExtractNestedValuesUsingJsonPath() {
        // Given: owners exist in the system
        Response response = ownersClient.getAllOwners();
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        // When: extracting nested fields without full POJO mapping
        String firstOwnerLastName = response.jsonPath().getString("[0].lastName");
        int firstOwnerPetCount = response.jsonPath().getList("[0].pets").size();

        // Then: JsonPath values support focused assertions
        assertThat(firstOwnerLastName, is(not(emptyOrNullString())));
        assertThat(firstOwnerPetCount, is(not(0)));
        assertThat(response.jsonPath().getString("[0].pets[0].type.name"), is(not(emptyOrNullString())));
    }

    @Test
    @Story("Response SLA")
    @DisplayName("Should enforce response time SLA using Rest Assured time matcher")
    void shouldMeetResponseTimeServiceLevelAgreement() {
        // Given/When: requesting pet types
        Response response = petTypesClient.getAllPetTypes();

        // Then: response is fast enough and valid
        response.then()
                .statusCode(200)
                .time(org.hamcrest.Matchers.lessThan(ConfigLoader.getMaxResponseTimeMs()));

        assertThat(response.getContentType(), containsString("application/json"));
    }
}
