package com.portfolio.petclinic.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.module.jsv.JsonSchemaValidator;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public final class ResponseValidator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private ResponseValidator() {
    }

    public static void assertStatusCode(int actualStatusCode, int expectedStatusCode) {
        assertThat("Unexpected HTTP status code", actualStatusCode, is(expectedStatusCode));
    }

    public static void assertMatchesSchema(String responseBody, String schemaClasspath) {
        try (InputStream schemaStream = ResponseValidator.class.getClassLoader().getResourceAsStream(schemaClasspath)) {
            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema not found: " + schemaClasspath);
            }

            JsonSchemaValidator
                    .matchesJsonSchema(schemaStream)
                    .matches(responseBody);
        } catch (Exception exception) {
            throw new AssertionError("Response does not match schema: " + schemaClasspath, exception);
        }
    }

    public static void assertFirstArrayItemMatchesSchema(String jsonArrayBody, String itemSchemaClasspath) {
        try {
            JsonNode arrayNode = OBJECT_MAPPER.readTree(jsonArrayBody);
            assertThat("Response should be a JSON array", arrayNode.isArray(), is(true));
            assertThat("JSON array should not be empty", arrayNode.size(), greaterThan(0));

            assertMatchesSchema(arrayNode.get(0).toString(), itemSchemaClasspath);
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Exception exception) {
            throw new AssertionError("Failed to validate first array item against schema", exception);
        }
    }

    public static <T> T deserialize(String responseBody, Class<T> targetType) {
        try {
            return OBJECT_MAPPER.readValue(responseBody, targetType);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to deserialize response body", exception);
        }
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }
}
