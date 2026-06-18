package com.portfolio.petclinic.utils;

public final class ErrorResponseValidator {

    private ErrorResponseValidator() {
    }

    public static void assertErrorStatusAndOptionalProblemDetail(
            String responseBody,
            int actualStatus,
            int expectedStatus,
            String schemaClasspath) {

        ResponseValidator.assertStatusCode(actualStatus, expectedStatus);

        if (responseBody == null || responseBody.isBlank()) {
            return;
        }

        ResponseValidator.assertMatchesSchema(responseBody, schemaClasspath);
    }
}
