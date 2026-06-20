package com.project.youtlix.testsupport.annotation;

import com.project.youtlix.testsupport.tags.TestTags;
import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Structural and dependency rules enforced with ArchUnit or custom checks.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTags.ARCHITECTURE)
public @interface ArchitectureTest {
}
