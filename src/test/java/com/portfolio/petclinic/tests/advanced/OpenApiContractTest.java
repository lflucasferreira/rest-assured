package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.clients.OpenApiClient;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("OpenAPI Contract")
class OpenApiContractTest {

    private OpenApiClient openApiClient;

    @BeforeEach
    void initClient() {
        openApiClient = new OpenApiClient();
    }

    @Test
    @Story("OpenAPI document")
    @DisplayName("GET /v3/api-docs should return a valid OpenAPI specification")
    void shouldReturnValidOpenApiSpecification() {
        // Given/When
        Response response = openApiClient.getApiDocs();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.getContentType(), containsString("application/json"));
        ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/openapi-docs-schema.json");

        assertThat(response.jsonPath().getString("info.title"), is(notNullValue()));
        assertThat(response.jsonPath().getMap("paths"), hasKey("/api/owners"));
        assertThat(response.jsonPath().getMap("paths"), hasKey("/api/vets"));
        assertThat(response.jsonPath().getMap("paths"), hasKey("/api/pettypes"));
    }
}
