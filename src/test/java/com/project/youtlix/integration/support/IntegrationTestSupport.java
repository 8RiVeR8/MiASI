package com.project.youtlix.integration.support;

import org.junit.jupiter.api.BeforeEach;
import org.opentest4j.TestAbortedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for Spring integration tests using profile {@code integration}.
 */
public abstract class IntegrationTestSupport {

    @Autowired
    private ApplicationContext applicationContext;

    protected String integrationMarker() {
        return "it-" + UUID.randomUUID();
    }

    @BeforeEach
    void requireLocalDatabaseConfiguration() {
        if (!IntegrationConditions.envPropertiesPresent()) {
            throw new TestAbortedException("Skipping: create env.properties with DB_DATABASE (and optional TEST_ADMIN_*)");
        }
        assertContextStarted();
    }

    protected ApplicationContext context() {
        return applicationContext;
    }

    protected void assertContextStarted() {
        assertThat(applicationContext).isNotNull();
    }
}
