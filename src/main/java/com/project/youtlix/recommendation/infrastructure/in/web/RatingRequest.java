package com.project.youtlix.recommendation.infrastructure.in.web;

import java.util.UUID;

/**
 * Request body for rating content.
 */
public record RatingRequest(UUID contentId, int stars) {
}
