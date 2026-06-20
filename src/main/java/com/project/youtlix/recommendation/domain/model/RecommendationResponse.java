package com.project.youtlix.recommendation.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * DTO for recommendations returned in application layer.
 * Does not depend on infrastructure layer.
 */
public record RecommendationResponse(
        UUID id,
        String type,
        String title,
        String description,
        String thumbnailUrl,
        String genre,
        int releaseYear,
        boolean available,
        Integer durationSeconds,
        String videoUri,
        List<String> languages,
        List<SeasonResponse> seasons
) {

    public record SeasonResponse(UUID id, int number, String title, List<EpisodeResponse> episodes) {
    }

    public record EpisodeResponse(
            UUID id,
            int number,
            String title,
            int durationSeconds,
            String videoUri,
            List<String> languages
    ) {
    }
}

