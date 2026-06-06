package com.project.youtlix.contentlibrary.domain.model;

import java.util.List;

/** Value object describing a movie or series in the content library. */
public record Metadata(String title, String description, String thumbnailUrl, Genre genre, int releaseYear, List<Keyword> keywords) {
    public Metadata {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title is required");
        if (genre == null) throw new IllegalArgumentException("Genre is required");
        if (releaseYear <= 0) throw new IllegalArgumentException("Release year must be positive");
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
    }
}
