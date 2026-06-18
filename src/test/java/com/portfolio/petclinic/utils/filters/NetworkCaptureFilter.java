package com.portfolio.petclinic.utils.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NetworkCaptureFilter implements Filter {

    private final ThreadLocal<List<NetworkExchange>> exchanges = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {

        long startedAt = System.currentTimeMillis();
        Response response = context.next(requestSpec, responseSpec);
        long durationMs = System.currentTimeMillis() - startedAt;

        exchanges.get().add(new NetworkExchange(
                requestSpec.getMethod(),
                requestSpec.getURI(),
                requestSpec.getHeaders().asList().toString(),
                requestSpec.getBody(),
                response.getStatusCode(),
                response.getContentType(),
                response.asString(),
                durationMs
        ));

        return response;
    }

    public void reset() {
        exchanges.get().clear();
    }

    public List<NetworkExchange> getExchanges() {
        return Collections.unmodifiableList(exchanges.get());
    }

    public NetworkExchange lastExchange() {
        List<NetworkExchange> captured = exchanges.get();
        if (captured.isEmpty()) {
            throw new IllegalStateException("No HTTP exchange captured yet");
        }
        return captured.get(captured.size() - 1);
    }

    public record NetworkExchange(
            String method,
            String uri,
            String requestHeaders,
            String requestBody,
            int statusCode,
            String contentType,
            String responseBody,
            long durationMs
    ) {
    }
}
