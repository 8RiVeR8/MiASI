package com.project.youtlix.recommendation.application.port.out;

import java.util.List;

/**
 * Conformist representation of metadata consumed from the content library.
 *
 * @param genre genre name from library published language
 * @param keywords keyword values from library published language
 * @param releaseYear content release year
 */
public record ContentMetadata(String genre, List<String> keywords, int releaseYear) {

    /** Creates immutable metadata snapshot. */
    public ContentMetadata {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
    }
}
