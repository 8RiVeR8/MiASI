package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.domain.model.Genre;

import java.util.List;

/**
 * Request body for metadata-only content updates.
 */
public record ContentMetadataRequest(
        String title,
        String description,
        String thumbnailUrl,
        Genre genre,
        int releaseYear,
        List<String> keywords
) {
}
