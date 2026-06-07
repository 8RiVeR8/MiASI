package com.project.youtlix.contentlibrary.domain.model;

/**
 * Search keyword assigned to content metadata.
 *
 * @param value keyword text
 */
public record Keyword(String value) {

    /**
     * Creates a normalized keyword.
     */
    public Keyword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("keyword must not be blank");
        }
        value = value.trim().toLowerCase();
    }
}
