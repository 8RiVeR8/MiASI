package com.project.youtlix.recommendation.infrastructure.in.web;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for user rating of content.
 */
public record UserRatingResponse(
    UUID contentId,
    int stars,
    Instant ratedAt
) {
}

