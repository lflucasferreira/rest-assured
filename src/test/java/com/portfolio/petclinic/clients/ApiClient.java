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
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.getBaseUri())
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

        this.requestSpec = builder.build();
    }
}
