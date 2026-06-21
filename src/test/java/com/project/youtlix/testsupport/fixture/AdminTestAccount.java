package com.project.youtlix.testsupport.fixture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Optional admin credentials for integration/e2e tests against Supabase.
 * Configure in gitignored {@code env.properties}:
 * {@code TEST_ADMIN_EMAIL} and {@code TEST_ADMIN_PASSWORD}.
 */
public final class AdminTestAccount {

    private static final Properties PROPERTIES = loadEnvProperties();

    private AdminTestAccount() {
    }

    public static Optional<String> email() {
        return optionalProperty("TEST_ADMIN_EMAIL");
    }

    public static Optional<String> password() {
        return optionalProperty("TEST_ADMIN_PASSWORD");
    }

    public static boolean isConfigured() {
        return email().isPresent() && password().isPresent();
    }

    public static void assumeConfigured() {
        if (!isConfigured()) {
            throw new org.opentest4j.TestAbortedException(
                    "Skipping: set TEST_ADMIN_EMAIL and TEST_ADMIN_PASSWORD in env.properties"
            );
        }
    }

    private static Optional<String> optionalProperty(String key) {
        String fromEnv = System.getenv(key);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return Optional.of(fromEnv.trim());
        }
        String fromFile = PROPERTIES.getProperty(key);
        if (fromFile != null && !fromFile.isBlank()) {
            return Optional.of(fromFile.trim());
        }
        return Optional.empty();
    }

    private static Properties loadEnvProperties() {
        Properties properties = new Properties();
        Path path = Path.of("env.properties");
        if (!Files.isRegularFile(path)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        } catch (IOException ignored) {
            return properties;
        }
        return properties;
    }
}
