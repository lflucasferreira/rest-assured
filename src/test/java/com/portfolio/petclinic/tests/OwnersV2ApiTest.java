package com.portfolio.petclinic.tests;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.clients.OwnersV2Client;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Owners V2")
class OwnersV2ApiTest extends BaseTest {

    private OwnersV2Client ownersV2Client;

    @BeforeEach
    void initClient() {
        ownersV2Client = new OwnersV2Client(networkCapture);
    }

    @Test
    @Story("Pagination")
    @DisplayName("GET /v2/owners should return a paginated owners page")
    void shouldReturnPaginatedOwnersPage() {
        Response response = ownersV2Client.getOwnersPage(0, 2, null);

        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
        ResponseValidator.assertMatchesSchema(response.getBody().asString(), "schemas/owners-page-schema.json");

        assertThat(response.jsonPath().getList("content"), hasSize(lessThanOrEqualTo(2)));
        assertThat(response.jsonPath().getInt("totalElements"), is(greaterThan(0)));
        assertThat(response.jsonPath().getInt("totalPages"), is(greaterThan(0)));
    }

    @Test
    @Story("Pagination filter")
    @DisplayName("GET /v2/owners?lastName should filter paginated results")
    void shouldFilterPaginatedOwnersByLastName() {
        Response seedResponse = ownersClient.getAllOwners();
        String lastName = seedResponse.as(Owner[].class)[0].getLastName();

        Response response = ownersV2Client.getOwnersPage(0, 5, lastName);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        response.jsonPath().getList("content.lastName").forEach(value ->
                assertThat(value, is(lastName))
        );
    }

    @Test
    @Story("Pagination metadata")
    @DisplayName("GET /v2/owners should expose consistent page metadata")
    void shouldExposeConsistentPageMetadata() {
        Response response = ownersV2Client.getOwnersPage(0, 3, null);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);

        assertThat(response.jsonPath().getInt("page"), is(0));
        assertThat(response.jsonPath().getInt("size"), is(3));
        assertThat(response.jsonPath().get("totalElements"), is(notNullValue()));
        assertThat(response.jsonPath().get("totalPages"), is(notNullValue()));
    }
}
