package com.project.youtlix.integration.support;

import com.project.youtlix.testsupport.annotation.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base contract for integration tests. Extend when shared setup is needed.
 */
@IntegrationTest
abstract class IntegrationTestSupport {

    @Autowired
    private ApplicationContext applicationContext;

    protected ApplicationContext context() {
        return applicationContext;
    }

    protected void assertContextStarted() {
        assertThat(applicationContext).isNotNull();
    }
}
