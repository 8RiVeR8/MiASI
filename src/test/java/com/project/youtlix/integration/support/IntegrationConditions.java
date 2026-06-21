package com.project.youtlix.integration.support;

import java.nio.file.Path;

/**
 * Guards integration and e2e tests that require local {@code env.properties}.
 */
public final class IntegrationConditions {

    private IntegrationConditions() {
    }

    public static boolean envPropertiesPresent() {
        return Path.of("env.properties").toFile().exists();
    }
}
