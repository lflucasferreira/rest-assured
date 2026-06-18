package com.portfolio.petclinic.tests.flows;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.OwnerCreatedEvent;
import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.models.VisitFields;
import com.portfolio.petclinic.utils.NetworkInspector;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("End-to-End Flows")
class PetLifecycleFlowTest extends BaseTest {

    @Test
    @Story("Pet lifecycle")
    @DisplayName("Should complete owner-pet-visit lifecycle and validate call chain via network capture")
    void shouldCompleteOwnerPetVisitLifecycle() {
        // Given: dynamic owner and pet type data
        PetType petType = fetchFirstPetType();
        networkCapture.reset();

        // When: executing full business flow
        Owner owner = createOwner();
        Pet pet = createPet(owner.getId(), petType);
        addVisit(owner.getId(), pet.getId());
        String updatedPetName = pet.getName() + "-VIP";
        updatePetName(pet, updatedPetName);
        Owner ownerWithPets = fetchOwnerWithPets(owner.getId());

        // Then: domain state is consistent
        assertThat(ownerWithPets.getPets(), hasSize(1));
        assertThat(ownerWithPets.getPets().get(0).getName(), is(updatedPetName));
        assertThat(ownerWithPets.getPets().get(0).getVisits(), hasSize(greaterThanOrEqualTo(1)));

        // And: network trace corroborates expected API sequence
        NetworkInspector.attachLastExchangeToAllure(networkCapture);
        NetworkInspector.assertResponseSequenceContains(
                networkCapture,
                "/owners",
                "/owners/",
                "/owners/",
                "/pets/",
                "/owners/"
        );

        cleanup(owner.getId(), pet.getId());
    }

    @Test
    @Story("Cross-service orchestration")
    @DisplayName("Should orchestrate Petclinic API calls and produce auditable owner event payload")
    void shouldProduceAuditableOwnerCreatedEventPayload() {
        // Given/When: owner is created
        Owner owner = createOwner();

        // Then: event payload can be used by downstream consumers
        OwnerCreatedEvent event = new OwnerCreatedEvent(owner.getId(), owner.getFirstName(), owner.getLastName());
        assertThat(event.asPayload().get("event"), is("OWNER_CREATED"));
        assertThat(event.asPayload().get("ownerId"), is(owner.getId()));

        ownersClient.deleteOwner(owner.getId());
    }

    private PetType fetchFirstPetType() {
        Response response = petTypesClient.getAllPetTypes();
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        return response.as(PetType[].class)[0];
    }

    private Owner createOwner() {
        Response response = ownersClient.createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        return response.as(Owner.class);
    }

    private Pet createPet(int ownerId, PetType petType) {
        PetFields petFields = TestDataFactory.buildPetFields(petType.getId(), petType.getName());
        Response response = petsClient.createPetForOwner(ownerId, petFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        return response.as(Pet.class);
    }

    private void addVisit(int ownerId, int petId) {
        VisitFields visitFields = TestDataFactory.buildVisitFields();
        Response response = visitsClient.addVisit(ownerId, petId, visitFields);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
    }

    private void updatePetName(Pet pet, String updatedName) {
        Pet updatePayload = TestDataFactory.buildPetUpdatePayload(pet, updatedName);
        Response response = petsClient.updatePet(pet.getId(), updatePayload);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 204);
        pet.setName(updatedName);
    }

    private Owner fetchOwnerWithPets(int ownerId) {
        Response response = ownersClient.getOwnerById(ownerId);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        Owner owner = response.as(Owner.class);
        assertThat(owner.getPets(), is(notNullValue()));
        return owner;
    }

    private void cleanup(int ownerId, int petId) {
        petsClient.deletePet(petId);
        ownersClient.deleteOwner(ownerId);
    }
}
