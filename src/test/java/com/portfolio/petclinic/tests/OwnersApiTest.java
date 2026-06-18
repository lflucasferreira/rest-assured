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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Owners")
class OwnersApiTest extends BaseTest {

    @Test
    @Story("List owners")
    @DisplayName("GET /owners should return all owners with valid structure")
    void shouldReturnAllOwnersWithValidStructure() {
        // Given: the Petclinic API is available
        // When: requesting the owners collection
        Response response = ownersClient.getAllOwners();

        // Then: response contains a non-empty list matching the owner schema
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        List<Owner> owners = Arrays.asList(response.as(Owner[].class));
        assertThat("Owners list should not be empty", owners, is(not(empty())));

        ResponseValidator.assertFirstArrayItemMatchesSchema(
            response.getBody().asString(),
            "schemas/owner-schema.json"
        );
        owners.forEach(owner -> {
            assertThat(owner.getId(), is(notNullValue()));
            assertThat(owner.getFirstName(), is(notNullValue()));
            assertThat(owner.getLastName(), is(notNullValue()));
        });
    }

    @Test
    @Story("Create owner")
    @DisplayName("POST /owners should create a new owner")
    void shouldCreateOwnerSuccessfully() {
        // Given: dynamically generated owner data
        Owner ownerPayload = TestDataFactory.buildOwner();

        // When: creating a new owner
        Response createResponse = ownersClient.createOwner(ownerPayload);

        // Then: owner is created with expected fields
        ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);

        Owner createdOwner = createResponse.as(Owner.class);
        ResponseValidator.assertMatchesSchema(createResponse.getBody().asString(), "schemas/owner-schema.json");

        assertThat(createdOwner.getId(), is(notNullValue()));
        assertThat(createdOwner.getFirstName(), is(ownerPayload.getFirstName()));
        assertThat(createdOwner.getLastName(), is(ownerPayload.getLastName()));
        assertThat(createdOwner.getTelephone(), is(ownerPayload.getTelephone()));

        // Cleanup
        ownersClient.deleteOwner(createdOwner.getId());
    }

    @Test
    @Story("Get owner by ID")
    @DisplayName("GET /owners/{id} should return owner details")
    void shouldReturnOwnerById() {
        // Given: an existing owner from the collection
        Response listResponse = ownersClient.getAllOwners();
        ResponseValidator.assertStatusCode(listResponse.getStatusCode(), 200);

        Owner existingOwner = listResponse.as(Owner[].class)[0];
        int ownerId = existingOwner.getId();

        // When: requesting the owner by ID
        Response response = ownersClient.getOwnerById(ownerId);

        // Then: owner details are returned
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        Owner owner = response.as(Owner.class);
        assertThat(owner.getId(), is(ownerId));
        assertThat(owner.getFirstName(), is(existingOwner.getFirstName()));
        assertThat(owner.getLastName(), is(existingOwner.getLastName()));
    }

    @Test
    @Story("Filter owners")
    @DisplayName("GET /owners?lastName should filter owners by last name")
    void shouldFilterOwnersByLastName() {
        // Given: owners exist in the system
        Response listResponse = ownersClient.getAllOwners();
        Owner seedOwner = listResponse.as(Owner[].class)[0];
        String lastName = seedOwner.getLastName();

        // When: filtering by last name
        Response filteredResponse = ownersClient.getAllOwnersByLastName(lastName);

        // Then: filtered results contain only matching owners
        ResponseValidator.assertStatusCode(filteredResponse.getStatusCode(), 200);

        List<Owner> filteredOwners = Arrays.asList(filteredResponse.as(Owner[].class));
        assertThat(filteredOwners, hasSize(greaterThan(0)));
        filteredOwners.forEach(owner ->
            assertThat(owner.getLastName(), is(lastName))
        );
    }

    @Test
    @Story("Update owner")
    @DisplayName("PUT /owners/{id} should update an existing owner")
    void shouldUpdateOwnerSuccessfully() {
        // Given: a newly created owner
        Owner ownerPayload = TestDataFactory.buildOwner();
        Response createResponse = ownersClient.createOwner(ownerPayload);
        ResponseValidator.assertStatusCode(createResponse.getStatusCode(), 201);
        Owner createdOwner = createResponse.as(Owner.class);

        String updatedLastName = createdOwner.getLastName() + "Updated";
        String updatedCity = "UpdatedCity";
        Owner updatePayload = TestDataFactory.buildOwnerUpdatePayload(createdOwner, updatedLastName, updatedCity);

        // When: updating the owner
        Response updateResponse = ownersClient.updateOwner(createdOwner.getId(), updatePayload);

        // Then: owner is updated (API returns 204 No Content)
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = ownersClient.getOwnerById(createdOwner.getId());
        ResponseValidator.assertStatusCode(getResponse.getStatusCode(), 200);

        Owner updatedOwner = getResponse.as(Owner.class);
        assertThat(updatedOwner.getLastName(), is(updatedLastName));
        assertThat(updatedOwner.getCity(), is(updatedCity));

        ownersClient.deleteOwner(createdOwner.getId());
    }
}
