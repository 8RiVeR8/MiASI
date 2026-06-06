package com.project.youtlix.contentlibrary.domain.model;

/** Value object representing a page requested during browsing. */
public record Page(int number, int size) {
    public Page {
        if (number < 0) throw new IllegalArgumentException("Page number cannot be negative");
        if (size <= 0) throw new IllegalArgumentException("Page size must be positive");
    }
}
