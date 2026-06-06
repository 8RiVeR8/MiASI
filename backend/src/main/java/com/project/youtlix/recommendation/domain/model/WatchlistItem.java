package com.project.youtlix.recommendation.domain.model;

import java.time.Instant;

/** Value object representing content saved by a viewer for later watching. */
public record WatchlistItem(ContentId contentId, Instant addedOn) {
    public WatchlistItem {
        if (contentId == null) throw new IllegalArgumentException("Content id is required");
        if (addedOn == null) throw new IllegalArgumentException("Added date is required");
    }
}
