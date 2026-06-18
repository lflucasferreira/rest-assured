package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Data Integrity")
class ReferentialIntegrityTest extends BaseTest {

    private Integer createdOwnerId;
    private Integer createdPetId;
    private Integer createdPetTypeId;

    @AfterEach
    void cleanup() {
        if (createdPetId != null) {
            petsClient.deletePet(createdPetId);
        }
        if (createdOwnerId != null) {
            ownersClient.deleteOwner(createdOwnerId);
        }
        if (createdPetTypeId != null) {
            petTypesClient.deletePetType(createdPetTypeId);
        }
    }

    @Test
    @Story("Cascade delete")
    @DisplayName("DELETE /owners/{id} should remove owner even when pets exist")
    void shouldDeleteOwnerWithPets() {
        PetType petType = createDedicatedPetType();
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId(), petType);

        Response deleteOwnerResponse = ownersClient.deleteOwner(owner.getId());
        ResponseValidator.assertStatusCode(deleteOwnerResponse.getStatusCode(), 204);

        assertThat(ownersClient.getOwnerById(owner.getId()).getStatusCode(), is(404));
        assertThat(petsClient.getPetById(pet.getId()).getStatusCode(), is(404));

        createdOwnerId = null;
        createdPetId = null;
    }

    @Test
    @Story("Idempotent delete")
    @DisplayName("DELETE /owners/{id} twice should return 404 on second attempt")
    void secondDeleteShouldReturnNotFound() {
        Response createResponse = ownersClient.createOwner(TestDataFactory.buildOwner());
        Owner owner = createResponse.as(Owner.class);
        createdOwnerId = owner.getId();

        ResponseValidator.assertStatusCode(ownersClient.deleteOwner(owner.getId()).getStatusCode(), 204);
        ResponseValidator.assertStatusCode(ownersClient.deleteOwner(owner.getId()).getStatusCode(), 404);
        createdOwnerId = null;
    }

    @Test
    @Story("Idempotent delete")
    @DisplayName("DELETE /pets/{id} twice should return 404 on second attempt")
    void secondPetDeleteShouldReturnNotFound() {
        PetType petType = createDedicatedPetType();
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId(), petType);

        ResponseValidator.assertStatusCode(petsClient.deletePet(pet.getId()).getStatusCode(), 204);
        ResponseValidator.assertStatusCode(petsClient.deletePet(pet.getId()).getStatusCode(), 404);

        createdPetId = null;
    }

    private PetType createDedicatedPetType() {
        Response response = petTypesClient.createPetType(TestDataFactory.buildUniquePetType());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        createdPetTypeId = response.as(PetType.class).getId();
        return response.as(PetType.class);
    }

    private Owner createOwner() {
        Response response = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        createdOwnerId = response.as(Owner.class).getId();
        return response.as(Owner.class);
    }

    private Pet createPet(int ownerId, PetType petType) {
        PetFields petFields = TestDataFactory.buildPetFields(petType.getId(), petType.getName());
        Response response = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        createdPetId = response.as(Pet.class).getId();
        return response.as(Pet.class);
    }
}
