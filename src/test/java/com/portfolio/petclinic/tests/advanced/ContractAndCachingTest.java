package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.ProblemDetail;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class ContractAndCachingTest extends BaseTest {

    @Test
    @Story("HTTP contract headers")
    @DisplayName("GET /owners should expose consistent security and content headers")
    void shouldExposeConsistentHttpResponseHeaders() {
        // Given/When
        Response response = ownersClient.getAllOwners();

        // Then: transport-level contract is stable
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.getContentType(), containsString("application/json"));
        assertThat(response.getHeader("X-Content-Type-Options"), is("nosniff"));
        assertThat(response.getHeader("Cache-Control"), containsString("no-cache"));
    }

    @Test
    @Story("HTTP caching")
    @DisplayName("GET /owners should support ETag-based conditional requests when ETag is provided")
    void shouldSupportEtagBasedConditionalGetWhenAvailable() {
        // Given: initial owners response
        Response initialResponse = ownersClient.getAllOwners();
        ResponseValidator.assertStatusCode(initialResponse.getStatusCode(), 200);

        String etag = initialResponse.getHeader("ETag");
        Assumptions.assumeTrue(etag != null && !etag.isBlank(),
                "Skipping conditional GET validation because API did not return ETag");

        // When: repeating request with If-None-Match
        Response cachedResponse = ownersClient.getAllOwners(etag);

        // Then
        ResponseValidator.assertStatusCode(cachedResponse.getStatusCode(), 304);
    }

    @Test
    @Story("Error contract")
    @DisplayName("GET /oops should return RFC 7807 ProblemDetail payload on server error")
    void shouldReturnProblemDetailForFailingEndpoint() {
        // Given/When: calling endpoint designed to fail
        Response response = diagnosticsClient.triggerFailingEndpoint();

        // Then: error contract is consistent
        ResponseValidator.assertStatusCode(response.getStatusCode(), 500);
        ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");

        ProblemDetail problem = response.as(ProblemDetail.class);
        assertThat(problem.getStatus(), is(500));
        assertThat(problem.getTitle(), is(not(emptyOrNullString())));
    }
}
