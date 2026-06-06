package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for watchlists until SQL persistence is implemented. */
@Repository
public class InMemoryWatchlistRepository implements WatchlistRepository {
    private final Map<ViewerId, Watchlist> watchlists = new ConcurrentHashMap<>();
    @Override public void save(Watchlist watchlist) { watchlists.put(watchlist.viewerId(), watchlist); }
    @Override public Optional<Watchlist> ofViewer(ViewerId viewerId) { return Optional.ofNullable(watchlists.get(viewerId)); }
}
