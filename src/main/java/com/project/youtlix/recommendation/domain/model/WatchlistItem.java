package com.project.youtlix.recommendation.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing one content item on a watchlist.
 *
 * @param contentId content added to watchlist
 * @param addedOn add time
 */
public record WatchlistItem(ContentId contentId, Instant addedOn) {

    /** Creates a validated watchlist item. */
    public WatchlistItem {
        Objects.requireNonNull(contentId, "contentId must not be null");
        Objects.requireNonNull(addedOn, "addedOn must not be null");
    }
}
