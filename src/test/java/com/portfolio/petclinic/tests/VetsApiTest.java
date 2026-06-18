package com.portfolio.petclinic.tests;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Vet;
import com.portfolio.petclinic.models.Vet;
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
@Feature("Vets")
class VetsApiTest extends BaseTest {

    private com.portfolio.petclinic.clients.VetsClient vetsClient;
    private final List<Integer> createdVetIds = new ArrayList<>();

    @BeforeEach
    void initVetsClient() {
        vetsClient = new com.portfolio.petclinic.clients.VetsClient(networkCapture);
    }

    @AfterEach
    void cleanup() {
        createdVetIds.forEach(vetsClient::deleteVet);
    }

    @Test
    @Story("List vets")
    @DisplayName("GET /vets should return all veterinarians with valid structure")
    void shouldReturnAllVetsWithValidStructure() {
        // Given/When
        Response response = vetsClient.getAllVets();

        // Then
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        List<Vet> vets = Arrays.asList(response.as(Vet[].class));
        assertThat("Vets list should not be empty", vets, is(not(empty())));

        ResponseValidator.assertFirstArrayItemMatchesSchema(
                response.getBody().asString(),
                "schemas/vet-schema.json"
        );

        vets.forEach(vet -> {
            assertThat(vet.getId(), is(notNullValue()));
            assertThat(vet.getFirstName(), is(notNullValue()));
            assertThat(vet.getLastName(), is(notNullValue()));
        });
    }

    @Test
    @Story("Get vet by ID")
    @DisplayName("GET /vets/{id} should return vet details")
    void shouldReturnVetById() {
        Response listResponse = vetsClient.getAllVets();
        Vet seedVet = listResponse.as(Vet[].class)[0];

        Response response = vetsClient.getVetById(seedVet.getId());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        assertThat(response.as(Vet.class).getFirstName(), is(seedVet.getFirstName()));
    }

    @Test
    @Story("Create vet")
    @DisplayName("POST /vets should create a new veterinarian")
    void shouldCreateVet() {
        Vet payload = TestDataFactory.buildUniqueVet();
        Response response = vetsClient.createVet(payload);

        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Vet created = response.as(Vet.class);
        assertThat(created.getId(), is(notNullValue()));
        createdVetIds.add(created.getId());
    }

    @Test
    @Story("Update vet")
    @DisplayName("PUT /vets/{id} should update an existing veterinarian")
    void shouldUpdateVet() {
        Vet created = createVet();
        Vet updatePayload = TestDataFactory.buildVet(created.getFirstName(), created.getLastName() + "Updated");
        updatePayload.setId(created.getId());

        Response updateResponse = vetsClient.updateVet(created.getId(), updatePayload);
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = vetsClient.getVetById(created.getId());
        assertThat(getResponse.as(Vet.class).getLastName(), is(updatePayload.getLastName()));
    }

    @Test
    @Story("Delete vet")
    @DisplayName("DELETE /vets/{id} should remove an existing veterinarian")
    void shouldDeleteVet() {
        Vet created = createVet();
        Response deleteResponse = vetsClient.deleteVet(created.getId());
        ResponseValidator.assertStatusCode(deleteResponse.getStatusCode(), 204);
        assertThat(vetsClient.getVetById(created.getId()).getStatusCode(), is(404));
        createdVetIds.remove(created.getId());
    }

    private Vet createVet() {
        Response response = vetsClient.createVet(TestDataFactory.buildUniqueVet());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Vet vet = response.as(Vet.class);
        createdVetIds.add(vet.getId());
        return vet;
    }
}
