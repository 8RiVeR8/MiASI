package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.domain.model.Content;

import java.util.UUID;

/**
 * Response returned by content library endpoints.
 */
public record ContentResponse(UUID id, String title, String genre, int releaseYear, boolean available) {

    /**
     * Maps a domain aggregate to a response DTO.
     */
    public static ContentResponse from(Content content) {
        return new ContentResponse(
                content.id().value(),
                content.metadata().title(),
                content.metadata().genre().name(),
                content.metadata().releaseYear(),
                content.available()
        );
    }
}
