package com.project.youtlix.recommendation.domain.model;

import java.util.UUID;

/** Value object identifying a watchlist aggregate. */
public record WatchlistId(UUID value) {
    public WatchlistId { if (value == null) throw new IllegalArgumentException("Watchlist id is required"); }
    /** Creates a new watchlist identifier. */
    public static WatchlistId newId() { return new WatchlistId(UUID.randomUUID()); }
}
