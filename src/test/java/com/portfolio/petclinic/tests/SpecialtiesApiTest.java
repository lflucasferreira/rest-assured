package com.portfolio.petclinic.tests;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.clients.SpecialtiesClient;
import com.portfolio.petclinic.models.Specialty;
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
@Feature("Specialties")
class SpecialtiesApiTest extends BaseTest {

    private SpecialtiesClient specialtiesClient;
    private final List<Integer> createdSpecialtyIds = new ArrayList<>();

    @BeforeEach
    void initClient() {
        specialtiesClient = new SpecialtiesClient(networkCapture);
    }

    @AfterEach
    void cleanup() {
        createdSpecialtyIds.forEach(specialtiesClient::deleteSpecialty);
    }

    @Test
    @Story("List specialties")
    @DisplayName("GET /specialties should return all specialties")
    void shouldReturnAllSpecialties() {
        Response response = specialtiesClient.getAllSpecialties();
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        List<Specialty> specialties = Arrays.asList(response.as(Specialty[].class));
        assertThat(specialties, is(not(empty())));
        ResponseValidator.assertFirstArrayItemMatchesSchema(
                response.getBody().asString(),
                "schemas/specialty-schema.json"
        );
    }

    @Test
    @Story("Create specialty")
    @DisplayName("POST /specialties should create a new specialty")
    void shouldCreateSpecialty() {
        Specialty payload = TestDataFactory.buildUniqueSpecialty();
        Response response = specialtiesClient.createSpecialty(payload);

        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Specialty created = response.as(Specialty.class);
        assertThat(created.getId(), is(notNullValue()));
        assertThat(created.getName(), is(payload.getName()));
        createdSpecialtyIds.add(created.getId());
    }

    @Test
    @Story("Update specialty")
    @DisplayName("PUT /specialties/{id} should update an existing specialty")
    void shouldUpdateSpecialty() {
        Specialty created = createSpecialty();
        String updatedName = created.getName() + "-updated";
        Specialty updatePayload = new Specialty(created.getId(), updatedName);

        Response updateResponse = specialtiesClient.updateSpecialty(created.getId(), updatePayload);
        ResponseValidator.assertStatusCode(updateResponse.getStatusCode(), 204);

        Response getResponse = specialtiesClient.getSpecialtyById(created.getId());
        assertThat(getResponse.as(Specialty.class).getName(), is(updatedName));
    }

    @Test
    @Story("Delete specialty")
    @DisplayName("DELETE /specialties/{id} should remove an existing specialty")
    void shouldDeleteSpecialty() {
        Specialty created = createSpecialty();
        Response deleteResponse = specialtiesClient.deleteSpecialty(created.getId());
        ResponseValidator.assertStatusCode(deleteResponse.getStatusCode(), 204);
        assertThat(specialtiesClient.getSpecialtyById(created.getId()).getStatusCode(), is(404));
        createdSpecialtyIds.remove(created.getId());
    }

    private Specialty createSpecialty() {
        Response response = specialtiesClient.createSpecialty(TestDataFactory.buildUniqueSpecialty());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);
        Specialty specialty = response.as(Specialty.class);
        createdSpecialtyIds.add(specialty.getId());
        return specialty;
    }
}
