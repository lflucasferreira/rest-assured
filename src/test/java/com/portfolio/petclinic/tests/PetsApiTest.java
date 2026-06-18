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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
@Epic("Spring Petclinic API")
@Feature("Pets")
class PetsApiTest extends BaseTest {

    private final List<Integer> createdOwnerIds = new ArrayList<>();
    private final List<Integer> createdPetIds = new ArrayList<>();

    private PetType defaultPetType;

    @BeforeEach
    void loadPetType() {
        Response petTypesResponse = petTypesClient.getAllPetTypes();
        ResponseValidator.assertStatusCode(petTypesResponse.getStatusCode(), 200);

        PetType[] petTypes = petTypesResponse.as(PetType[].class);
        defaultPetType = petTypes[0];
    }

    @AfterEach
    void cleanupCreatedResources() {
        createdPetIds.forEach(petId -> petsClient.deletePet(petId));
        createdOwnerIds.forEach(ownerId -> ownersClient.deleteOwner(ownerId));
    }

    @Test
    @Story("List pets")
    @DisplayName("GET /pets should return all pets with valid structure")
    void shouldReturnAllPetsWithValidStructure() {
        // Given: the Petclinic API is available
        // When: requesting the pets collection
        Response response = petsClient.getAllPets();

        // Then: response contains pets matching the schema
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        List<Pet> pets = Arrays.asList(response.as(Pet[].class));
        assertThat("Pets list should not be empty", pets, is(not(empty())));

        ResponseValidator.assertFirstArrayItemMatchesSchema(
            response.getBody().asString(),
            "schemas/pet-schema.json"
        );

        Pet firstPet = pets.get(0);
        assertThat(firstPet.getId(), is(notNullValue()));
        assertThat(firstPet.getName(), is(notNullValue()));
        assertThat(firstPet.getType(), is(notNullValue()));
    }

    @Test
    @Story("Get pet by ID")
    @DisplayName("GET /pets/{id} should return pet details")
    void shouldReturnPetById() {
        // Given: an existing pet in the system
        Response listResponse = petsClient.getAllPets();
        Pet existingPet = listResponse.as(Pet[].class)[0];

        // When: requesting the pet by ID
        Response response = petsClient.getPetById(existingPet.getId());

        // Then: pet details are returned
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        Pet pet = response.as(Pet.class);
        ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/pet-schema.json");

        assertThat(pet.getId(), is(existingPet.getId()));
        assertThat(pet.getName(), is(existingPet.getName()));
    }

    @Test
    @Story("Create pet")
    @DisplayName("POST /owners/{ownerId}/pets should create a new pet for an owner")
    void shouldCreatePetForOwner() {
        // Given: a new owner and dynamic pet payload
        Owner owner = createOwner();
        PetFields petPayload = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());

        // When: adding a pet to the owner
        Response createResponse = petsClient.createPetForOwner(owner.getId(), petPayload);

        // Then: pet is created with expected attributes
        ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);

        Pet createdPet = createResponse.as(Pet.class);
        ResponseValidator.assertMatchesSchema(createResponse.getBody().asString(), "schemas/pet-schema.json");

        assertThat(createdPet.getId(), is(notNullValue()));
        assertThat(createdPet.getName(), is(petPayload.getName()));
        assertThat(createdPet.getOwnerId(), is(owner.getId()));
        assertThat(createdPet.getType().getId(), is(defaultPetType.getId()));

        createdPetIds.add(createdPet.getId());
    }

    @Test
    @Story("Update pet")
    @DisplayName("PUT /pets/{id} should update an existing pet")
    void shouldUpdatePetSuccessfully() {
        // Given: an owner with a newly created pet
        Owner owner = createOwner();
        Pet createdPet = createPetForOwner(owner.getId());
        String updatedName = createdPet.getName() + "Updated";

        Pet updatePayload = TestDataFactory.buildPetUpdatePayload(createdPet, updatedName);

        // When: updating the pet
        Response updateResponse = petsClient.updatePet(createdPet.getId(), updatePayload);

        // Then: pet is updated (API returns 204 No Content)
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = petsClient.getPetById(createdPet.getId());
        ResponseValidator.assertStatusCode(getResponse.getStatusCode(), 200);

        Pet updatedPet = getResponse.as(Pet.class);
        assertThat(updatedPet.getId(), is(createdPet.getId()));
        assertThat(updatedPet.getName(), is(updatedName));

        createdPetIds.add(createdPet.getId());
    }

    @Test
    @Story("Delete pet")
    @DisplayName("DELETE /pets/{id} should remove an existing pet")
    void shouldDeletePetSuccessfully() {
        // Given: an owner with a newly created pet
        Owner owner = createOwner();
        Pet createdPet = createPetForOwner(owner.getId());

        // When: deleting the pet
        Response deleteResponse = petsClient.deletePet(createdPet.getId());

        // Then: pet is removed and no longer retrievable (API returns 204 No Content)
        ResponseValidator.assertStatusCode(deleteResponse.getStatusCode(), 204);

        Response getResponse = petsClient.getPetById(createdPet.getId());
        assertThat(getResponse.getStatusCode(), is(404));
    }

    private Owner createOwner() {
        Response createOwnerResponse = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(createOwnerResponse.getStatusCode(), 201);

        Owner owner = createOwnerResponse.as(Owner.class);
        createdOwnerIds.add(owner.getId());
        return owner;
    }

    private Pet createPetForOwner(int ownerId) {
        PetFields petFields = TestDataFactory.buildPetFields(defaultPetType.getId(), defaultPetType.getName());
        Response createPetResponse = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(createPetResponse.getStatusCode(), 201);

        Pet pet = createPetResponse.as(Pet.class);
        createdPetIds.add(pet.getId());
        return pet;
    }
}
