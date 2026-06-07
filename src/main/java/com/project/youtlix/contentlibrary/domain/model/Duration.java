package com.project.youtlix.contentlibrary.domain.model;

/**
 * Duration of a movie or episode in seconds.
 *
 * @param seconds positive duration in seconds
 */
public record Duration(int seconds) {

    /**
     * Creates a validated duration.
     */
    public Duration {
        if (seconds <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
    }

    /**
     * Factory method for readability in tests and use cases.
     *
     * @param seconds positive duration in seconds
     * @return duration value object
     */
    public static Duration ofSeconds(int seconds) {
        return new Duration(seconds);
    }
}
