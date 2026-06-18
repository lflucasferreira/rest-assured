package com.portfolio.petclinic.tests.security;

import com.portfolio.petclinic.clients.OwnersClient;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.SecureApiProbe;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Security")
@Tag("security")
class SecurityApiTest {

    private OwnersClient secureOwnersClient;

    @BeforeAll
    static void requireSecureApi() {
        Assumptions.assumeTrue(
                SecureApiProbe.isSecureApiAvailable(),
                "Secure Petclinic API is not available at " + ConfigLoader.getSecureBaseUri()
        );
    }

    @BeforeEach
    void initSecureClient() {
        secureOwnersClient = OwnersClient.secured();
    }

    @Test
    @Story("Authentication required")
    @DisplayName("GET /owners without credentials should return 401 on secured API")
    void shouldRejectUnauthenticatedRequests() {
        // Given/When
        Response response = given()
                .baseUri(ConfigLoader.getSecureBaseUri())
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation()
                .when()
                .get("/owners");

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 401);
    }

    @Test
    @Story("Valid credentials")
    @DisplayName("GET /owners with valid Basic Auth should return 200 on secured API")
    void shouldAllowAuthenticatedRequests() {
        // Given/When
        Response response = secureOwnersClient.getAllOwners();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.as(Owner[].class).length, is(notNullValue()));
    }

    @Test
    @Story("Invalid credentials")
    @DisplayName("GET /owners with invalid credentials should return 401 on secured API")
    void shouldRejectInvalidCredentials() {
        // Given/When
        Response response = given()
                .baseUri(ConfigLoader.getSecureBaseUri())
                .accept(ContentType.JSON)
                .relaxedHTTPSValidation()
                .auth().preemptive().basic(ConfigLoader.getAuthUsername(), "wrong-password")
                .when()
                .get("/owners");

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 401);
    }

    @Test
    @Story("Authenticated write")
    @DisplayName("POST /owners with valid Basic Auth should create owner on secured API")
    void shouldCreateOwnerWithAuthentication() {
        // Given
        Owner ownerPayload = TestDataFactory.buildOwner();

        // When
        Response response = secureOwnersClient.createOwner(ownerPayload);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Owner createdOwner = response.as(Owner.class);
        assertThat(createdOwner.getId(), is(notNullValue()));

        secureOwnersClient.deleteOwner(createdOwner.getId());
    }
}
