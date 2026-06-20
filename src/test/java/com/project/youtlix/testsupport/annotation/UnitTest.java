package com.project.youtlix.testsupport.annotation;

import com.project.youtlix.testsupport.tags.TestTags;
import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fast tests with no Spring context and no external I/O.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTags.UNIT)
public @interface UnitTest {
}
