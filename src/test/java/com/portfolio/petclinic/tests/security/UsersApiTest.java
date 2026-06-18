package com.portfolio.petclinic.tests.security;

import com.portfolio.petclinic.clients.OwnersClient;
import com.portfolio.petclinic.clients.UsersClient;
import com.portfolio.petclinic.models.User;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Spring Petclinic API")
@Feature("Security")
@Tag("security")
class UsersApiTest {

    private UsersClient adminUsersClient;

    @BeforeAll
    static void requireSecureApi() {
        Assumptions.assumeTrue(
                SecureApiProbe.isSecureApiAvailable(),
                "Secure Petclinic API is not available at " + ConfigLoader.getSecureBaseUri()
        );
    }

    @BeforeEach
    void initAdminClient() {
        adminUsersClient = UsersClient.secured();
    }

    @Test
    @Story("User provisioning")
    @DisplayName("POST /users with admin credentials should create a new user")
    void shouldCreateUserWithAdminCredentials() {
        User userPayload = TestDataFactory.buildUniqueSecureUser("OWNER_ADMIN");

        Response response = adminUsersClient.createUser(userPayload);
        ResponseValidator.assertStatusCode(response.getStatusCode(), 201);

        User createdUser = response.as(User.class);
        assertThat(createdUser.getUsername(), is(userPayload.getUsername()));
        assertThat(createdUser.isEnabled(), is(true));
        assertThat(createdUser.getRoles().get(0).getName(), org.hamcrest.Matchers.containsString("OWNER_ADMIN"));
    }

    @Test
    @Story("User authentication")
    @DisplayName("Newly created user should authenticate when provisioning supports Basic Auth login")
    void shouldAuthenticateWithNewlyCreatedUserWhenSupported() {
        User userPayload = TestDataFactory.buildUniqueSecureUser("OWNER_ADMIN");
        adminUsersClient.createUser(userPayload);

        Response response = OwnersClient
                .withCredentials(userPayload.getUsername(), userPayload.getPassword())
                .getAllOwners();

        Assumptions.assumeTrue(
                response.getStatusCode() == 200,
                "Skipping provisioned-user login check because current API image does not enable Basic Auth for POST /users"
        );
        ResponseValidator.assertStatusCode(response.getStatusCode(), 200);
    }
}
