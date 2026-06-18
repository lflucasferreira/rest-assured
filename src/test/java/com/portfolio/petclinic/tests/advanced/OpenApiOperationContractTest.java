package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.clients.OpenApiClient;
import com.portfolio.petclinic.utils.OpenApiResponseValidator;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Epic("Spring Petclinic API")
@Feature("OpenAPI Contract")
@Tag("contract")
class OpenApiOperationContractTest extends BaseTest {

    private OpenApiClient openApiClient;
    private String openApiDocument;

    @BeforeEach
    void loadOpenApiDocument() {
        openApiClient = new OpenApiClient();
        Response response = openApiClient.getApiDocs();
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        openApiDocument = response.getBody().asString();
    }

    @Test
    @Story("Operation contract")
    @DisplayName("GET /owners response should match documented OpenAPI operation")
    void ownersListShouldMatchDocumentedOperation() {
        Response response = ownersClient.getAllOwners();
        OpenApiResponseValidator.assertResponseMatchesDocumentedOperation(
                openApiClient.getApiDocs(),
                response,
                "/api/owners",
                "get"
        );
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    }

    @Test
    @Story("Operation contract")
    @DisplayName("GET /vets response should match documented OpenAPI operation")
    void vetsListShouldMatchDocumentedOperation() {
        Response response = new com.portfolio.petclinic.clients.VetsClient(networkCapture).getAllVets();
        OpenApiResponseValidator.assertResponseMatchesDocumentedOperation(
                openApiClient.getApiDocs(),
                response,
                "/api/vets",
                "get"
        );
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    }

    @Test
    @Story("Operation contract")
    @DisplayName("GET /visits response should match documented OpenAPI operation when visits exist")
    void visitsListShouldMatchDocumentedOperation() {
        Response response = visitsClient.getAllVisits();
        if (response.getStatusCode() == 404) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                    "Skipping visits OpenAPI contract because no visits are available in the API dataset");
        }
        OpenApiResponseValidator.assertResponseMatchesDocumentedOperation(
                openApiClient.getApiDocs(),
                response,
                "/api/visits",
                "get"
        );
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    }

    @Test
    @Story("Operation contract")
    @DisplayName("OpenAPI document should define core owner write operation")
    void openApiShouldDefineOwnerCreateOperation() {
        org.hamcrest.MatcherAssert.assertThat(
                OpenApiResponseValidator.hasOperation(openApiDocument, "/api/owners", "post"),
                org.hamcrest.Matchers.is(true)
        );
    }
}
