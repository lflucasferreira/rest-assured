package com.portfolio.petclinic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties PROPERTIES = loadProperties();

    private ConfigLoader() {
    }

    private static Properties loadProperties() {
        String environment = System.getProperty("env", "dev");
        String configFile = "config/" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                throw new IllegalStateException("Configuration file not found: " + configFile);
            }
            properties.load(inputStream);
            LOG.info("Loaded configuration for environment: {}", environment);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load configuration: " + configFile, exception);
        }

        overrideFromSystemProperties(properties);
        return properties;
    }

    private static void overrideFromSystemProperties(Properties properties) {
        overrideProperty(properties, "api.base.uri");
        overrideProperty(properties, "api.secure.base.uri");
        overrideProperty(properties, "api.petclinic.root.uri");
    }

    private static void overrideProperty(Properties properties, String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isBlank()) {
            properties.setProperty(key, value);
        }
    }

    public static String getBaseUri() {
        return PROPERTIES.getProperty("api.base.uri");
    }

    public static String getSecureBaseUri() {
        return PROPERTIES.getProperty("api.secure.base.uri");
    }

    public static String getPetclinicRootUri() {
        String configured = PROPERTIES.getProperty("api.petclinic.root.uri");
        if (configured != null && !configured.isBlank()) {
            return configured;
        }
        return getBaseUri().replaceAll("/api$", "");
    }

    public static String getOpenApiDocsPath() {
        return "/v3/api-docs";
    }

    public static int getConnectionTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("api.connection.timeout.ms", "10000"));
    }

    public static int getSocketTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("api.socket.timeout.ms", "10000"));
    }

    public static int getWebhookTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("webhook.timeout.ms", "3000"));
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(PROPERTIES.getProperty("logging.enabled", "true"));
    }

    public static long getMaxResponseTimeMs() {
        return Long.parseLong(PROPERTIES.getProperty("api.response.time.max.ms", "5000"));
    }

    public static String getAuthUsername() {
        return PROPERTIES.getProperty("api.auth.username", "admin");
    }

    public static String getAuthPassword() {
        return PROPERTIES.getProperty("api.auth.password", "admin");
    }
}
