package com.portfolio.petclinic.tests.negative;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.clients.OwnersV2Client;
import com.portfolio.petclinic.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Epic("Spring Petclinic API")
@Feature("Negative Scenarios")
class OwnersV2NegativeApiTest extends BaseTest {

    private OwnersV2Client ownersV2Client;

    @BeforeEach
    void initClient() {
        ownersV2Client = new OwnersV2Client(networkCapture);
    }

    @ParameterizedTest(name = "GET /v2/owners?page={0}&size={1} should return server error")
    @CsvSource({
            "-1, 2",
            "0, 0"
    })
    @Story("Invalid pagination")
    @DisplayName("Should reject invalid pagination parameters")
    void shouldRejectInvalidPaginationParameters(int page, int size) {
        Response response = ownersV2Client.getOwnersPage(page, size, null);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 500);
    }

    @Test
    @Story("Invalid specialty lookup")
    @DisplayName("GET /specialties/{id} should return 404 for unknown specialty")
    void shouldReturnNotFoundForUnknownSpecialty() {
        Response response = new com.portfolio.petclinic.clients.SpecialtiesClient(networkCapture)
                .getSpecialtyById(99999);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 404);
    }
}
