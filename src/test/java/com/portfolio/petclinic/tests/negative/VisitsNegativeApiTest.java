package com.portfolio.petclinic.tests.negative;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.models.VisitFields;
import com.portfolio.petclinic.utils.ErrorResponseValidator;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Negative Scenarios")
class VisitsNegativeApiTest extends BaseTest {

    private final List<Integer> createdOwnerIds = new ArrayList<>();
    private final List<Integer> createdPetIds = new ArrayList<>();
    private PetType defaultPetType;

    @BeforeEach
    void loadPetType() {
        Response response = petTypesClient.getAllPetTypes();
        defaultPetType = response.as(PetType[].class)[0];
    }

    @AfterEach
    void cleanup() {
        createdPetIds.forEach(petId -> petsClient.deletePet(petId));
        createdOwnerIds.forEach(ownerId -> ownersClient.deleteOwner(ownerId));
    }

    @Test
    @Story("Invalid visit lookup")
    @DisplayName("GET /visits/{id} should return 404 for unknown visit")
    void shouldReturnNotFoundForUnknownVisit() {
        Response response = visitsClient.getVisitById(99999);
        ErrorResponseValidator.assertErrorStatusAndOptionalProblemDetail(
                response.getBody().asString(),
                response.getStatusCode(),
                404,
                "schemas/problem-detail-schema.json"
        );
    }

    @Test
    @Story("Invalid visit creation")
    @DisplayName("POST visit should return 400 for empty description")
    void shouldRejectVisitWithEmptyDescription() {
        Owner owner = createOwner();
        PetFields petFields = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());
        Response petResponse = petsClient.createPetForOwner(owner.getId(), petFields);
        int petId = petResponse.as(com.portfolio.petclinic.models.Pet.class).getId();
        createdPetIds.add(petId);

        Response response = visitsClient.addVisit(
                owner.getId(),
                petId,
                TestDataFactory.buildInvalidVisitWithEmptyDescription()
        );

        ResponseValidator.assertStatusCode(response.getStatusCode(), 400);
    }

    @Test
    @Story("Invalid nested visit")
    @DisplayName("POST visit should return 404 for non-existent pet")
    void shouldRejectVisitForNonExistentPet() {
        Owner owner = createOwner();
        Response response = visitsClient.addVisit(owner.getId(), 99999, TestDataFactory.buildVisitFields());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }

    @Test
    @Story("Invalid nested pet lookup")
    @DisplayName("GET /owners/{ownerId}/pets/{petId} should return 404 for mismatched owner and pet")
    void shouldReturnNotFoundForMismatchedOwnerAndPet() {
        Response response = ownersClient.getOwnerPetById(1, 99999);
        assertThat(response.getStatusCode(), is(404));
    }

    private Owner createOwner() {
        Response response = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Owner owner = response.as(Owner.class);
        createdOwnerIds.add(owner.getId());
        return owner;
    }
}
