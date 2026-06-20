package com.project.youtlix.testsupport.annotation;

import com.project.youtlix.testsupport.tags.TestTags;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tests that boot a limited or full Spring context and may use real adapters.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTags.INTEGRATION)
@SpringBootTest
@ActiveProfiles("integration")
public @interface IntegrationTest {
}
