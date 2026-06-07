package com.project.youtlix.recommendation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier of a watchlist aggregate.
 *
 * @param value UUID value stored in recommendation schema
 */
public record WatchlistId(UUID value) {

    /** Creates a validated watchlist id. */
    public WatchlistId {
        Objects.requireNonNull(value, "watchlist id value must not be null");
    }

    /** Creates a new random watchlist id. */
    public static WatchlistId newId() {
        return new WatchlistId(UUID.randomUUID());
    }
}
