package com.project.youtlix.recommendation.infrastructure.in.web;

import java.util.UUID;

/**
 * Request body for watchlist operations.
 */
public record WatchlistRequest(UUID contentId) {
}
