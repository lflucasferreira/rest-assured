package com.portfolio.petclinic.base;

import com.portfolio.petclinic.clients.DiagnosticsClient;
import com.portfolio.petclinic.clients.OwnersClient;
import com.portfolio.petclinic.clients.PetTypesClient;
import com.portfolio.petclinic.clients.PetsClient;
import com.portfolio.petclinic.clients.VisitsClient;
import com.portfolio.petclinic.utils.ConfigLoader;
import com.portfolio.petclinic.utils.filters.NetworkCaptureFilter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTest {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    protected NetworkCaptureFilter networkCapture;
    protected OwnersClient ownersClient;
    protected PetsClient petsClient;
    protected PetTypesClient petTypesClient;
    protected VisitsClient visitsClient;
    protected DiagnosticsClient diagnosticsClient;

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = ConfigLoader.getBaseUri();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.useRelaxedHTTPSValidation();

        LOG.info("REST Assured configured with base URI: {}", ConfigLoader.getBaseUri());
    }

    @BeforeEach
    void initClients() {
        networkCapture = new NetworkCaptureFilter();
        ownersClient = new OwnersClient(networkCapture);
        petsClient = new PetsClient(networkCapture);
        petTypesClient = new PetTypesClient(networkCapture);
        visitsClient = new VisitsClient(networkCapture);
        diagnosticsClient = new DiagnosticsClient(networkCapture);
    }
}
