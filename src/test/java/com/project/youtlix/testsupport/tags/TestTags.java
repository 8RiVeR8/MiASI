package com.project.youtlix.testsupport.tags;

/**
 * JUnit 5 tag names used to split the test pyramid in Maven profiles.
 */
public final class TestTags {

    public static final String UNIT = "unit";
    public static final String INTEGRATION = "integration";
    public static final String E2E = "e2e";
    public static final String ARCHITECTURE = "architecture";

    private TestTags() {
    }
}
