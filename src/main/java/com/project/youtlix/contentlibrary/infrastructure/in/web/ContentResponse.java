package com.project.youtlix.contentlibrary.infrastructure.in.web;

import com.project.youtlix.contentlibrary.domain.model.*;

import java.util.List;
import java.util.UUID;

/**
 * Response returned by content library endpoints.
 */
public record ContentResponse(
        UUID id,
        String type,
        String title,
        String description,
        ContentType contentType,
        String thumbnailUrl,
        String genre,
        int releaseYear,
        boolean available,
        Integer durationSeconds,
        String videoUri,
        List<String> languages,
        List<SeasonResponse> seasons
) {

    /**
     * Maps a domain aggregate to a response DTO.
     */
    public static ContentResponse from(Content content) {
        Metadata metadata = content.metadata();
        if (content instanceof Movie movie) {
            return new ContentResponse(
                    content.id().value(),
                    "MOVIE",
                    metadata.title(),
                    metadata.description(),
                    metadata.contentType(),
                    metadata.thumbnailUrl(),
                    metadata.genre().name(),
                    metadata.releaseYear(),
                    content.available(),
                    movie.duration().seconds(),
                    movie.videoFile().uri(),
                    movie.videoFile().languages(),
                    List.of()
            );
        }
        Series series = (Series) content;
        return new ContentResponse(
                content.id().value(),
                "SERIES",
                metadata.title(),
                metadata.description(),
                metadata.contentType(),
                metadata.thumbnailUrl(),
                metadata.genre().name(),
                metadata.releaseYear(),
                content.available(),
                null,
                null,
                List.of(),
                series.seasons().stream().map(SeasonResponse::from).toList()
        );
    }

    public record SeasonResponse(UUID id, int number, String title, List<EpisodeResponse> episodes) {
        static SeasonResponse from(Season season) {
            return new SeasonResponse(
                    season.id().value(),
                    season.number(),
                    season.title(),
                    season.episodes().stream().map(EpisodeResponse::from).toList()
            );
        }
    }

    public record EpisodeResponse(
            UUID id,
            int number,
            String title,
            int durationSeconds,
            String videoUri,
            List<String> languages
    ) {
        static EpisodeResponse from(Episode episode) {
            return new EpisodeResponse(
                    episode.id().value(),
                    episode.number(),
                    episode.title(),
                    episode.duration().seconds(),
                    episode.videoFile().uri(),
                    episode.videoFile().languages()
            );
        }
    }
}
