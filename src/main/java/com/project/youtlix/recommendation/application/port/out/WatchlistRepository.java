package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;

import java.util.Optional;

/**
 * Repository port for watchlist aggregates.
 */
public interface WatchlistRepository {

    /** Persists a watchlist. */
    void save(Watchlist watchlist);

    /** Loads watchlist by viewer. */
    Optional<Watchlist> ofViewer(ViewerId viewerId);

    /** Removes content from all persisted watchlists. */
    void removeFromWatchlists(ContentId contentId);
}
