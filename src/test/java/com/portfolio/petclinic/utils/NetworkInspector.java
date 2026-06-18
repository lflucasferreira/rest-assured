package com.portfolio.petclinic.utils;

import com.portfolio.petclinic.utils.filters.NetworkCaptureFilter;
import io.qameta.allure.Allure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

public final class NetworkInspector {

    private NetworkInspector() {
    }

    public static void attachLastExchangeToAllure(NetworkCaptureFilter capture) {
        NetworkCaptureFilter.NetworkExchange exchange = capture.lastExchange();
        Allure.addAttachment(
                "HTTP Request",
                "text/plain",
                exchange.method() + " " + exchange.uri() + System.lineSeparator()
                        + "Headers: " + exchange.requestHeaders() + System.lineSeparator()
                        + "Body: " + exchange.requestBody()
        );
        Allure.addAttachment(
                "HTTP Response",
                "text/plain",
                "Status: " + exchange.statusCode() + System.lineSeparator()
                        + "Content-Type: " + exchange.contentType() + System.lineSeparator()
                        + "Duration(ms): " + exchange.durationMs() + System.lineSeparator()
                        + "Body: " + exchange.responseBody()
        );
    }

    public static void assertExchangeCount(NetworkCaptureFilter capture, int expectedCount) {
        assertThat("Captured HTTP exchange count", capture.getExchanges().size(), is(expectedCount));
    }

    public static void assertLastRequestMethod(NetworkCaptureFilter capture, String expectedMethod) {
        assertThat(capture.lastExchange().method(), is(expectedMethod));
    }

    public static void assertLastRequestUriContains(NetworkCaptureFilter capture, String pathFragment) {
        assertThat(capture.lastExchange().uri(), containsString(pathFragment));
    }

    public static void assertLastResponseStatus(NetworkCaptureFilter capture, int expectedStatus) {
        assertThat(capture.lastExchange().statusCode(), is(expectedStatus));
    }

    public static void assertLastResponseTimeUnder(NetworkCaptureFilter capture, long maxMillis) {
        assertThat(
                "Response time should be within SLA",
                capture.lastExchange().durationMs(),
                lessThanOrEqualTo(maxMillis)
        );
    }

    public static void assertResponseSequenceContains(NetworkCaptureFilter capture, String... pathFragments) {
        assertThat("Expected at least " + pathFragments.length + " calls", capture.getExchanges().size(),
                greaterThan(pathFragments.length - 1));

        for (int index = 0; index < pathFragments.length; index++) {
            String uri = capture.getExchanges().get(index).uri();
            assertThat("Call #" + (index + 1) + " URI", uri, containsString(pathFragments[index]));
        }
    }

    public static void assertLastResponseBodyContains(NetworkCaptureFilter capture, String expectedFragment) {
        String body = capture.lastExchange().responseBody();
        assertThat(body, notNullValue());
        assertThat(body, containsString(expectedFragment));
    }
}
