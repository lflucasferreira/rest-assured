package com.portfolio.petclinic.clients;

import com.portfolio.petclinic.utils.ConfigLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class ApiClient {

    protected final RequestSpecification requestSpec;

    protected ApiClient(Filter... additionalFilters) {
        this(buildRequestSpec(ConfigLoader.getBaseUri(), false, additionalFilters));
    }

    protected ApiClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public static RequestSpecification buildSecureRequestSpec(Filter... additionalFilters) {
        return buildRequestSpec(ConfigLoader.getSecureBaseUri(), true, additionalFilters);
    }

    public static RequestSpecification buildAuthenticatedRequestSpec(
            String baseUri,
            String username,
            String password,
            Filter... additionalFilters
    ) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setRelaxedHTTPSValidation();

        for (Filter filter : additionalFilters) {
            builder.addFilter(filter);
        }

        if (ConfigLoader.isLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .addFilter(new AllureRestAssured());
        }

        return builder.build().auth().preemptive().basic(username, password);
    }

    private static RequestSpecification buildRequestSpec(String baseUri, boolean withAuth, Filter... additionalFilters) {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setRelaxedHTTPSValidation();

        for (Filter filter : additionalFilters) {
            builder.addFilter(filter);
        }

        if (ConfigLoader.isLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter())
                    .addFilter(new ResponseLoggingFilter())
                    .addFilter(new AllureRestAssured());
        }

        RequestSpecification specification = builder.build();
        if (withAuth) {
            specification = specification.auth().preemptive().basic(
                    ConfigLoader.getAuthUsername(),
                    ConfigLoader.getAuthPassword());
        }
        return specification;
    }
}
