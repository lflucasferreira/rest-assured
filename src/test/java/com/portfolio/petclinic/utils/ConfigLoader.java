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
        String baseUriOverride = System.getProperty("api.base.uri");
        if (baseUriOverride != null && !baseUriOverride.isBlank()) {
            properties.setProperty("api.base.uri", baseUriOverride);
        }
    }

    public static String getBaseUri() {
        return PROPERTIES.getProperty("api.base.uri");
    }

    public static int getConnectionTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("api.connection.timeout.ms", "10000"));
    }

    public static int getSocketTimeoutMs() {
        return Integer.parseInt(PROPERTIES.getProperty("api.socket.timeout.ms", "10000"));
    }

    public static boolean isLoggingEnabled() {
        return Boolean.parseBoolean(PROPERTIES.getProperty("logging.enabled", "true"));
    }
}
