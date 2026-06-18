package com.portfolio.petclinic.tests;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.utils.ErrorResponseValidator;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Pet Types")
class PetTypesApiTest extends BaseTest {

    private final List<Integer> createdPetTypeIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        createdPetTypeIds.forEach(petTypesClient::deletePetType);
    }

    @Test
    @Story("List pet types")
    @DisplayName("GET /pettypes should return all pet types")
    void shouldReturnAllPetTypes() {
        // Given/When
        Response response = petTypesClient.getAllPetTypes();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        List<PetType> petTypes = Arrays.asList(response.as(PetType[].class));
        assertThat("Pet types list should not be empty", petTypes, is(not(empty())));
        petTypes.forEach(petType -> {
            assertThat(petType.getId(), is(notNullValue()));
            assertThat(petType.getName(), is(notNullValue()));
        });
    }

    @Test
    @Story("Get pet type by ID")
    @DisplayName("GET /pettypes/{id} should return pet type details")
    void shouldReturnPetTypeById() {
        // Given: an existing pet type from the collection
        Response listResponse = petTypesClient.getAllPetTypes();
        ResponseValidator.assertStatusCode(listResponse.getStatusCode(), 200);

        PetType existingPetType = listResponse.as(PetType[].class)[0];
        int petTypeId = existingPetType.getId();

        // When
        Response response = petTypesClient.getPetTypeById(petTypeId);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        PetType petType = response.as(PetType.class);
        assertThat(petType.getId(), is(petTypeId));
        assertThat(petType.getName(), is(existingPetType.getName()));
    }

    @Test
    @Story("Invalid pet type lookup")
    @DisplayName("GET /pettypes/{id} should return 404 for unknown pet type")
    void shouldReturnNotFoundForUnknownPetType() {
        // Given/When
        Response response = petTypesClient.getPetTypeById(99999);

        // Then
        ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(
                response.getBody().asString(),
                response.getStatusCode(),
                404,
                "schemas/problem-detail-schema.json"
        );
    }

    @Test
    @Story("Create pet type")
    @DisplayName("POST /pettypes should create a new pet type")
    void shouldCreatePetType() {
        PetType payload = TestDataFactory.buildUniquePetType();
        Response response = petTypesClient.createPetType(payload);

        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        PetType created = response.as(PetType.class);
        assertThat(created.getId(), is(notNullValue()));
        assertThat(created.getName(), is(payload.getName()));
        createdPetTypeIds.add(created.getId());
    }

    @Test
    @Story("Update pet type")
    @DisplayName("PUT /pettypes/{id} should update an existing pet type")
    void shouldUpdatePetType() {
        PetType created = createPetType();
        PetType updatePayload = new PetType(created.getId(), created.getName() + "-updated");

        Response updateResponse = petTypesClient.updatePetType(created.getId(), updatePayload);
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = petTypesClient.getPetTypeById(created.getId());
        assertThat(getResponse.as(PetType.class).getName(), is(updatePayload.getName()));
    }

    @Test
    @Story("Delete pet type")
    @DisplayName("DELETE /pettypes/{id} should remove an unused pet type")
    void shouldDeleteUnusedPetType() {
        PetType created = createPetType();
        Response deleteResponse = petTypesClient.deletePetType(created.getId());
        ResponseValidator.assertStatusCode(deleteResponse.getStatusCode(), 204);
        assertThat(petTypesClient.getPetTypeById(created.getId()).getStatusCode(), is(404));
        createdPetTypeIds.remove(created.getId());
    }

    private PetType createPetType() {
        Response response = petTypesClient.createPetType(TestDataFactory.buildUniquePetType());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        PetType petType = response.as(PetType.class);
        createdPetTypeIds.add(petType.getId());
        return petType;
    }
}
