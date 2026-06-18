package com.portfolio.petclinic.base;

import com.portfolio.petclinic.clients.OwnersClient;
import com.portfolio.petclinic.clients.PetTypesClient;
import com.portfolio.petclinic.clients.PetsClient;
import com.portfolio.petclinic.utils.ConfigLoader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    protected OwnersClient ownersClient;
    protected PetsClient petsClient;
    protected PetTypesClient petTypesClient;

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = ConfigLoader.getBaseUri();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();

        LOG.info("REST Assured configured with base URI: {}", ConfigLoader.getBaseUri());
    }

    @BeforeEach
    void initClients() {
        ownersClient = new OwnersClient();
        petsClient = new PetsClient();
        petTypesClient = new PetTypesClient();
    }
}
