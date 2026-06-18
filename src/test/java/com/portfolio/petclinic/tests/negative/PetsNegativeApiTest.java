package com.portfolio.petclinic.tests.negative;

import com.portfolio.petclinic.base.BaseTest;
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
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Negative Scenarios")
class PetsNegativeApiTest extends BaseTest {

    @ParameterizedTest(name = "GET /pets/{0} should return {1}")
    @CsvSource({
            "99999, 404",
            "0, 404"
    })
    @Story("Invalid pet lookup")
    @DisplayName("Should return expected status for non-existent pet IDs")
    void shouldReturnExpectedStatusForInvalidPetIds(int petId, int expectedStatus) {
        // Given/When: requesting an invalid pet ID
        Response response = petsClient.getPetById(petId);

        // Then: API returns expected error status and problem contract when body is present
        ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(
                response.getBody().asString(),
                response.getStatusCode(),
                expectedStatus,
                "schemas/problem-detail-schema.json"
        );

        if (!response.getBody().asString().isBlank()) {
            ProblemDetail problem = response.as(ProblemDetail.class);
            assertThat(problem.getStatus(), is(expectedStatus));
        }
    }

    @Test
    @Story("Invalid update")
    @DisplayName("PUT /pets/{id} should return 404 when pet does not exist")
    void shouldReturnNotFoundWhenUpdatingMissingPet() {
        // Given: update payload for a non-existent pet
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        int petTypeId = petTypesResponse.jsonPath().getInt("[0].id");
        String petTypeName = petTypesResponse.jsonPath().getString("[0].name");

        var petFields = TestDataFactory.buildPetFields(petTypeId, petTypeName);
        var petPayload = new com.portfolio.petclinic.models.Pet();
        petPayload.setId(99999);
        petPayload.setName(petFields.getName());
        petPayload.setBirthDate(petFields.getBirthDate());
        petPayload.setType(petFields.getType());

        // When: updating missing pet
        Response response = petsClient.updatePet(99999, petPayload);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }

    @ParameterizedTest(name = "POST pet to missing owner {0} should return 404")
    @ValueSource(ints = {99999, 50000})
    @Story("Invalid pet creation")
    @DisplayName("Should reject pet creation for non-existent owner")
    void shouldRejectPetCreationForMissingOwner(int ownerId) {
        // Given: valid pet payload but invalid owner
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        var petFields = TestDataFactory.buildPetFields(
                petTypesResponse.jsonPath().getInt("[0].id"),
                petTypesResponse.jsonPath().getString("[0].name")
        );

        // When
        Response response = petsClient.createPetForOwner(ownerId, petFields);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }

    @Test
    @Story("Validation error")
    @DisplayName("POST pet should return 400 for future birth date")
    void shouldRejectPetWithFutureBirthDate() {
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        Response createOwnerResponse = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(createOwnerResponse.getStatusCode(), 201);
        int ownerId = createOwnerResponse.as(com.portfolio.petclinic.models.Owner.class).getId();

        var petFields = TestDataFactory.buildPetFieldsWithFutureBirthDate(
                petTypesResponse.jsonPath().getInt("[0].id"),
                petTypesResponse.jsonPath().getString("[0].name")
        );

        Response response = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 400);

        ownersClient.deleteOwner(ownerId);
    }

    @Test
    @Story("Validation error")
    @DisplayName("POST pet should return server error for empty pet name")
    void shouldRejectPetWithEmptyName() {
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        Response createOwnerResponse = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(createOwnerResponse.getStatusCode(), 201);
        int ownerId = createOwnerResponse.as(com.portfolio.petclinic.models.Owner.class).getId();

        var petFields = TestDataFactory.buildPetFieldsWithEmptyName(
                petTypesResponse.jsonPath().getInt("[0].id"),
                petTypesResponse.jsonPath().getString("[0].name")
        );

        Response response = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 500);

        ownersClient.deleteOwner(ownerId);
    }
}
