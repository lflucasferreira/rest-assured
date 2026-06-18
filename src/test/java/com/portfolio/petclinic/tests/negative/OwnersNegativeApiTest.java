package com.portfolio.petclinic.tests.negative;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.ProblemDetail;
import com.portfolio.petclinic.utils.ErrorResponseValidator;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Negative Scenarios")
class OwnersNegativeApiTest extends BaseTest {

    @Test
    @Story("Validation error")
    @DisplayName("POST /owners should return 400 for invalid telephone format")
    void shouldRejectOwnerWithInvalidTelephone() {
        // Given: owner payload with alphabetic telephone
        Owner invalidOwner = TestDataFactory.buildInvalidOwnerWithAlphabeticTelephone();

        // When
        Response response = ownersClient.createOwner(invalidOwner);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 400);
        ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/problem-detail-schema.json");

        ProblemDetail problem = response.as(ProblemDetail.class);
        assertThat(problem.getStatus(), is(400));
    }

    @ParameterizedTest(name = "GET /owners/{0} should return {1}")
    @CsvSource({
            "99999, 404",
            "0, 404"
    })
    @Story("Invalid owner lookup")
    @DisplayName("Should return expected status for non-existent owner IDs")
    void shouldReturnExpectedStatusForInvalidOwnerIds(int ownerId, int expectedStatus) {
        // Given/When
        Response response = ownersClient.getOwnerById(ownerId);

        // Then
        ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(
                response.getBody().asString(),
                response.getStatusCode(),
                expectedStatus,
                "schemas/problem-detail-schema.json"
        );

        if (!response.getBody().asString().isBlank()) {
            assertThat(response.as(ProblemDetail.class).getStatus(), is(expectedStatus));
        }
    }

    @Test
    @Story("Invalid delete")
    @DisplayName("DELETE /owners/{id} should return 404 for unknown owner")
    void shouldReturnNotFoundWhenDeletingUnknownOwner() {
        // Given/When
        Response response = ownersClient.deleteOwner(99999);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }

    @Test
    @Story("Validation error")
    @DisplayName("POST /owners should return 400 for empty required fields")
    void shouldRejectOwnerWithEmptyRequiredFields() {
        Response response = ownersClient.createOwner(TestDataFactory.buildEmptyOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 400);
    }

    @Test
    @Story("Invalid update")
    @DisplayName("PUT /owners/{id} should return 404 for unknown owner")
    void shouldReturnNotFoundWhenUpdatingUnknownOwner() {
        Owner payload = TestDataFactory.buildOwner();
        payload.setId(99999);
        Response response = ownersClient.updateOwner(99999, payload);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }

    @Test
    @Story("Invalid update")
    @DisplayName("PUT /owners/{id} should return 400 for invalid telephone")
    void shouldRejectOwnerUpdateWithInvalidTelephone() {
        Response createResponse = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);
        Owner owner = createResponse.as(Owner.class);

        owner.setTelephone("INVALID");
        Response response = ownersClient.updateOwner(owner.getId(), owner);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 400);

        ownersClient.deleteOwner(owner.getId());
    }
}
