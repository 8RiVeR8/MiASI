package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.domain.model.ContentType;
import com.project.youtlix.contentlibrary.domain.model.Genre;

import java.util.List;

/**
 * Request body used by the library web adapter for content creation and updates.
 */
public record ContentRequest(
        ContentType type,
        String title,
        String description,
        String thumbnailUrl,
        Genre genre,
        int releaseYear,
        List<String> keywords,
        Integer durationSeconds,
        String videoUri,
        List<String> languages
) {
}
