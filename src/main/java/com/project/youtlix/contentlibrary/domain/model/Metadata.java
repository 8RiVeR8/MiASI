package com.project.youtlix.contentlibrary.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Descriptive content metadata used for browsing, filtering and recommendations.
 *
 * @param title content title
 * @param description content description
 * @param thumbnailUrl thumbnail URL
 * @param genre content genre
 * @param releaseYear release year
 * @param keywords search keywords
 */
public record Metadata(
        String title,
        String description,
        String thumbnailUrl,
        Genre genre,
        int releaseYear,
        List<Keyword> keywords
) {

    /**
     * Creates validated metadata.
     */
    public Metadata {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Objects.requireNonNull(genre, "genre must not be null");
        if (releaseYear < 1888) {
            throw new IllegalArgumentException("release year must be at least 1888");
        }
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
    }
}
