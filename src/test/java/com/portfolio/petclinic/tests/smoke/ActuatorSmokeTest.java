package com.portfolio.petclinic.tests.smoke;

import com.portfolio.petclinic.clients.ActuatorClient;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Actuator Smoke")
@Tag("smoke")
class ActuatorSmokeTest {

    private ActuatorClient actuatorClient;

    @BeforeEach
    void initClient() {
        actuatorClient = new ActuatorClient();
    }

    @Test
    @Story("Health check")
    @DisplayName("GET /actuator/health should report UP status")
    void shouldReportHealthyStatus() {
        // Given/When
        Response response = actuatorClient.getHealth();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.jsonPath().getString("status"), is("UP"));
    }

    @Test
    @Story("Application info")
    @DisplayName("GET /actuator/info should return application metadata when exposed")
    void shouldReturnApplicationInfoWhenExposed() {
        // Given/When
        Response response = actuatorClient.getInfo();

        // Then
        Assumptions.assumeTrue(
                response.getStatusCode() == 200,
                "Skipping /actuator/info validation because endpoint is not exposed by the API image"
        );
        assertThat(response.getContentType(), containsString("application/json"));
    }
}
