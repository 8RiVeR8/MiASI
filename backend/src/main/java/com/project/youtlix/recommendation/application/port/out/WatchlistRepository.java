package com.project.youtlix.recommendation.application.port.out;

import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import java.util.Optional;

/** Output port for watchlist persistence. */
public interface WatchlistRepository {
    void save(Watchlist watchlist);
    Optional<Watchlist> ofViewer(ViewerId viewerId);
}
