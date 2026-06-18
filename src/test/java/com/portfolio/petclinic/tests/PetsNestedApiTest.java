package com.portfolio.petclinic.tests;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Pets")
class PetsNestedApiTest extends BaseTest {

    private final List<Integer> createdOwnerIds = new ArrayList<>();
    private final List<Integer> createdPetIds = new ArrayList<>();
    private PetType defaultPetType;

    @BeforeEach
    void loadPetType() {
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        ResponseValidator.assertStatusCode(petTypesResponse.getStatusCode(), 200);
        defaultPetType = petTypesResponse.as(PetType[].class)[0];
    }

    @AfterEach
    void cleanup() {
        createdPetIds.forEach(petId -> petsClient.deletePet(petId));
        createdOwnerIds.forEach(ownerId -> ownersClient.deleteOwner(ownerId));
    }

    @Test
    @Story("Nested pet lookup")
    @DisplayName("GET /owners/{ownerId}/pets/{petId} should return pet details consistent with GET /pets/{petId}")
    void shouldReturnOwnerPetConsistentWithGlobalPetEndpoint() {
        // Given: owner with a pet
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());

        // When: requesting pet via nested route
        Response nestedResponse = ownersClient.getOwnerPetById(owner.getId(), pet.getId());
        Response globalResponse = petsClient.getPetById(pet.getId());

        // Then: both routes return equivalent pet data
        ResponseValidator.assertStatusCode(nestedResponse.getStatusCode(), 200);
        ResponseValidator.assertStatusCode(globalResponse.getStatusCode(), 200);

        Pet nestedPet = nestedResponse.as(Pet.class);
        Pet globalPet = globalResponse.as(Pet.class);

        assertThat(nestedPet.getId(), is(globalPet.getId()));
        assertThat(nestedPet.getName(), is(globalPet.getName()));
        assertThat(nestedPet.getOwnerId(), is(owner.getId()));
    }

    private Owner createOwner() {
        Response response = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Owner owner = response.as(Owner.class);
        createdOwnerIds.add(owner.getId());
        return owner;
    }

    private Pet createPet(int ownerId) {
        PetFields petFields = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());
        Response response = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Pet pet = response.as(Pet.class);
        createdPetIds.add(pet.getId());
        return pet;
    }
}
