package com.portfolio.petclinic.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public final class OpenApiResponseValidator {

    private static final ObjectMapper OBJECT_MAPPER = ResponseValidator.objectMapper();
    private static final Set<String> SUPPORTED_METHODS = Set.of("get", "post", "put", "delete", "patch");

    private OpenApiResponseValidator() {
    }

    public static void assertOperationDocumentsStatusCode(String openApiBody, String path, String method, int statusCode) {
        try {
            JsonNode paths = OBJECT_MAPPER.readTree(openApiBody).path("paths");
            JsonNode operation = paths.path(path).path(method.toLowerCase());
            assertThat("OpenAPI operation not found for " + method.toUpperCase() + " " + path,
                    operation.isMissingNode(), is(false));

            JsonNode responses = operation.path("responses");
            String statusKey = String.valueOf(statusCode);
            assertThat("OpenAPI operation " + path + " should document status " + statusCode,
                    responses.has(statusKey), is(true));
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to validate OpenAPI operation contract", exception);
        }
    }

    public static void assertResponseMatchesDocumentedOperation(
            Response openApiResponse,
            Response apiResponse,
            String path,
            String method
    ) {
        assertThat(openApiResponse.getStatusCode(), is(200));
        assertOperationDocumentsStatusCode(openApiResponse.getBody().asString(), path, method, apiResponse.getStatusCode());
        assertThat(apiResponse.getContentType(), org.hamcrest.Matchers.containsString("application/json"));
    }

    public static boolean hasOperation(String openApiBody, String path, String method) {
        try {
            JsonNode operation = OBJECT_MAPPER.readTree(openApiBody).path("paths").path(path).path(method.toLowerCase());
            return !operation.isMissingNode() && SUPPORTED_METHODS.contains(method.toLowerCase());
        } catch (Exception exception) {
            return false;
        }
    }
}
