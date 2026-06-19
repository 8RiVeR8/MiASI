package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.Metadata;
import com.project.youtlix.contentlibrary.domain.model.Movie;

import java.util.UUID;

/**
 * Response returned by content library endpoints.
 */
public record ContentResponse(
        UUID id,
        String type,
        String title,
        String description,
        String thumbnailUrl,
        String genre,
        int releaseYear,
        boolean available
) {

    /**
     * Maps a domain aggregate to a response DTO.
     */
    public static ContentResponse from(Content content) {
        Metadata metadata = content.metadata();
        return new ContentResponse(
                content.id().value(),
                content instanceof Movie ? "MOVIE" : "SERIES",
                metadata.title(),
                metadata.description(),
                metadata.thumbnailUrl(),
                metadata.genre().name(),
                metadata.releaseYear(),
                content.available()
        );
    }
}
