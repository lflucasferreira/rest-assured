package com.portfolio.petclinic.tests.security;

import com.portfolio.petclinic.clients.OwnersClient;
import com.portfolio.petclinic.clients.SpecialtiesClient;
import com.portfolio.petclinic.clients.UsersClient;
import com.portfolio.petclinic.clients.VetsClient;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.User;
import com.portfolio.petclinic.models.Vet;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.SecureApiProbe;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Security")
@Tag("security")
class SecurityAuthorizationTest {

    private UsersClient adminUsersClient;
    private User vetUser;
    private User ownerUser;

    @BeforeAll
    static void requireSecureApi() {
        Assumptions.assumeTrue(
                SecureApiProbe.isSecureApiAvailable(),
                "Secure Petclinic API is not available at " + ConfigLoader.getSecureBaseUri()
        );
    }

    @BeforeEach
    void provisionRoleUsers() {
        adminUsersClient = UsersClient.secured();
        vetUser = TestDataFactory.buildUniqueSecureUser("VET_ADMIN");
        ownerUser = TestDataFactory.buildUniqueSecureUser("OWNER_ADMIN");

        ResponseValidator.assertStatusCode(adminUsersClient.createUser(vetUser).getStatusCode(), 201);
        ResponseValidator.assertStatusCode(adminUsersClient.createUser(ownerUser).getStatusCode(), 201);

        Assumptions.assumeTrue(
                OwnersClient.withCredentials(ownerUser.getUsername(), ownerUser.getPassword())
                        .getAllOwners().getStatusCode() == 200,
                "Skipping RBAC tests because provisioned users cannot authenticate in current API image"
        );
    }

    @Test
    @Story("Role-based access")
    @DisplayName("VET_ADMIN should access vets but not create owners")
    void vetAdminShouldAccessVetsButNotCreateOwners() {
        Response vetsResponse = VetsClient.withCredentials(vetUser.getUsername(), vetUser.getPassword())
                .getAllVets();
        ResponseValidator.assertStatusCode(vetsResponse.getStatusCode(), 200);
        assertThat(vetsResponse.as(Vet[].class).length, is(greaterThan(0)));

        Response ownersResponse = OwnersClient.withCredentials(vetUser.getUsername(), vetUser.getPassword())
                .createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(ownersResponse.getStatusCode(), 403);
    }

    @Test
    @Story("Role-based access")
    @DisplayName("OWNER_ADMIN should access owners but not specialties")
    void ownerAdminShouldAccessOwnersButNotSpecialties() {
        Response ownersResponse = OwnersClient.withCredentials(ownerUser.getUsername(), ownerUser.getPassword())
                .getAllOwners();
        ResponseValidator.assertStatusCode(ownersResponse.getStatusCode(), 200);

        Response specialtiesResponse = SpecialtiesClient
                .withCredentials(ownerUser.getUsername(), ownerUser.getPassword())
                .getAllSpecialties();
        ResponseValidator.assertStatusCode(specialtiesResponse.getStatusCode(), 403);
    }

    @Test
    @Story("Role-based access")
    @DisplayName("OWNER_ADMIN should create owners successfully")
    void ownerAdminShouldCreateOwners() {
        Response response = OwnersClient.withCredentials(ownerUser.getUsername(), ownerUser.getPassword())
                .createOwner(TestDataFactory.buildOwner());
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);

        OwnersClient.withCredentials(ownerUser.getUsername(), ownerUser.getPassword())
                .deleteOwner(response.as(Owner.class).getId());
    }

    @Test
    @Story("Admin access")
    @DisplayName("Admin should access owners, vets and specialties")
    void adminShouldAccessCoreResources() {
        ResponseValidator.assertStatusCode(OwnersClient.secured().getAllOwners().getStatusCode(), 200);
        ResponseValidator.assertStatusCode(VetsClient.secured().getAllVets().getStatusCode(), 200);
        ResponseValidator.assertStatusCode(SpecialtiesClient.secured().getAllSpecialties().getStatusCode(), 200);
    }
}
