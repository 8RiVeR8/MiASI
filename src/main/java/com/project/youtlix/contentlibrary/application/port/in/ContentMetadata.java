package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;

import java.util.List;

/**
 * Published language with catalog metadata consumed by recommendations.
 *
 * @param genre content genre
 * @param keywords content keywords
 * @param releaseYear release year
 * @param title content title
 */
public record ContentMetadata(Genre genre, List<Keyword> keywords, int releaseYear, String title) {

    /**
     * Creates immutable published metadata.
     */
    public ContentMetadata {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
    }
}
