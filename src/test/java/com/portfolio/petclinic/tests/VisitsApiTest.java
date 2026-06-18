package com.portfolio.petclinic.tests;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.models.Visit;
import com.portfolio.petclinic.models.VisitFields;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Visits")
class VisitsApiTest extends BaseTest {

    private final List<Integer> createdOwnerIds = new ArrayList<>();
    private final List<Integer> createdPetIds = new ArrayList<>();
    private final List<Integer> createdVisitIds = new ArrayList<>();
    private PetType defaultPetType;

    @BeforeEach
    void loadPetType() {
        Response response = petTypesClient.getAllPetTypes();
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        defaultPetType = response.as(PetType[].class)[0];
    }

    @AfterEach
    void cleanup() {
        createdVisitIds.forEach(visitId -> visitsClient.deleteVisit(visitId));
        createdPetIds.forEach(petId -> petsClient.deletePet(petId));
        createdOwnerIds.forEach(ownerId -> ownersClient.deleteOwner(ownerId));
    }

    @Test
    @Story("List visits")
    @DisplayName("GET /visits should return visits including newly created records")
    void shouldReturnAllVisitsIncludingCreatedRecord() {
        // Given: at least one visit exists in the system
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());
        Visit createdVisit = createVisit(owner.getId(), pet.getId());

        // When
        Response response = visitsClient.getAllVisits();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        List<Visit> visits = Arrays.asList(response.as(Visit[].class));
        assertThat(visits.stream().anyMatch(visit -> visit.getId().equals(createdVisit.getId())), is(true));
    }

    @Test
    @Story("Get visit by ID")
    @DisplayName("GET /visits/{id} should return visit details")
    void shouldReturnVisitById() {
        // Given: a created visit
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());
        Visit createdVisit = createVisit(owner.getId(), pet.getId());

        // When
        Response response = visitsClient.getVisitById(createdVisit.getId());

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.as(Visit.class).getId(), is(createdVisit.getId()));
    }

    @Test
    @Story("Create visit")
    @DisplayName("POST /owners/{ownerId}/pets/{petId}/visits should create a visit")
    void shouldCreateVisitForPet() {
        // Given
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());
        VisitFields visitFields = TestDataFactory.buildVisitFields();

        // When
        Response response = visitsClient.addVisit(owner.getId(), pet.getId(), visitFields);

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Visit createdVisit = response.as(Visit.class);
        assertThat(createdVisit.getId(), is(notNullValue()));
        assertThat(createdVisit.getPetId(), is(pet.getId()));
        assertThat(createdVisit.getDescription(), is(visitFields.getDescription()));

        createdVisitIds.add(createdVisit.getId());
    }

    @Test
    @Story("Update visit")
    @DisplayName("PUT /visits/{id} should update an existing visit")
    void shouldUpdateVisitSuccessfully() {
        // Given
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());
        Visit createdVisit = createVisit(owner.getId(), pet.getId());
        String updatedDescription = createdVisit.getDescription() + "-updated";
        Visit updatePayload = TestDataFactory.buildVisitUpdatePayload(createdVisit, updatedDescription);

        // When
        Response updateResponse = visitsClient.updateVisit(createdVisit.getId(), updatePayload);

        // Then
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = visitsClient.getVisitById(createdVisit.getId());
        ResponseValidator.assertStatusCode(getResponse.getStatusCode(), 200);
        assertThat(getResponse.as(Visit.class).getDescription(), is(updatedDescription));
    }

    @Test
    @Story("Delete visit")
    @DisplayName("DELETE /visits/{id} should remove an existing visit")
    void shouldDeleteVisitSuccessfully() {
        // Given
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId());
        Visit createdVisit = createVisit(owner.getId(), pet.getId());

        // When
        Response deleteResponse = visitsClient.deleteVisit(createdVisit.getId());

        // Then
        ResponseValidator.assertStatusCode(deleteResponse.getStatusCode(), 204);
        assertThat(visitsClient.getVisitById(createdVisit.getId()).getStatusCode(), is(404));
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

    private Visit createVisit(int ownerId, int petId) {
        Response response = visitsClient.addVisit(ownerId, petId, TestDataFactory.buildVisitFields());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Visit visit = response.as(Visit.class);
        createdVisitIds.add(visit.getId());
        return visit;
    }
}
