package com.project.youtlix.contentlibrary.domain.model;

/** Value object representing video duration in seconds. */
public record Duration(int seconds) {
    public Duration { if (seconds <= 0) throw new IllegalArgumentException("Duration must be positive"); }
}
