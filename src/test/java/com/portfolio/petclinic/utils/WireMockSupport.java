package com.portfolio.petclinic.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

public final class WireMockSupport implements AutoCloseable {

    private final WireMockServer server;

    public WireMockSupport() {
        server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        server.start();
        configureFor("localhost", server.port());
    }

    public int port() {
        return server.port();
    }

    public String baseUrl() {
        return "http://localhost:" + server.port();
    }

    public WireMockServer server() {
        return server;
    }

    @Override
    public void close() {
        server.stop();
    }
}
