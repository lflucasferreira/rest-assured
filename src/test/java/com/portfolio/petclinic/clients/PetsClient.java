package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetsClient extends ApiClient {

    private static final String PETS_PATH = "/pets";

    public Response getAllPets() {
        return given()
            .spec(requestSpec)
            .when()
            .get(PETS_PATH);
    }

    public Response getPetById(int petId) {
        return given()
            .spec(requestSpec)
            .pathParam("petId", petId)
            .when()
            .get(PETS_PATH + "/{petId}");
    }

    public Response updatePet(int petId, Pet pet) {
        return given()
            .spec(requestSpec)
            .pathParam("petId", petId)
            .body(pet)
            .when()
            .put(PETS_PATH + "/{petId}");
    }

    public Response deletePet(int petId) {
        return given()
            .spec(requestSpec)
            .pathParam("petId", petId)
            .when()
            .delete(PETS_PATH + "/{petId}");
    }

    public Response createPetForOwner(int ownerId, PetFields petFields) {
        return given()
            .spec(requestSpec)
            .pathParam("ownerId", ownerId)
            .body(petFields)
            .when()
            .post("/owners/{ownerId}/pets");
    }
}
