package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.recommendation.domain.model.RecommendedItem;

import java.util.UUID;

/**
 * Response item returned by recommendation endpoint.
 */
public record RecommendationResponse(UUID contentId, double score, String reason) {

    /** Maps a domain recommended item to API response. */
    public static RecommendationResponse from(RecommendedItem item) {
        return new RecommendationResponse(item.contentId().value(), item.score(), item.reason().name());
    }
}
