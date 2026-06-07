package com.project.youtlix.contentlibrary.domain.model;

/**
 * Page request used by browsing use cases.
 *
 * @param number zero-based page number
 * @param size page size
 */
public record Page(int number, int size) {

    /**
     * Creates a validated page request.
     */
    public Page {
        if (number < 0) {
            throw new IllegalArgumentException("page number must not be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("page size must be positive");
        }
    }
}
