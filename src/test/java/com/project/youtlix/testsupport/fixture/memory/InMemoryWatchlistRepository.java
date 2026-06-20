package com.project.youtlix.testsupport.fixture.memory;

import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory watchlist repository isolated from Supabase.
 */
public final class InMemoryWatchlistRepository implements WatchlistRepository {

    private final Map<ViewerId, Watchlist> watchlists = new LinkedHashMap<>();

    @Override
    public void save(Watchlist watchlist) {
        watchlists.put(watchlist.viewerId(), watchlist);
    }

    @Override
    public Optional<Watchlist> ofViewer(ViewerId viewerId) {
        return Optional.ofNullable(watchlists.get(viewerId));
    }

    @Override
    public void removeFromWatchlists(ContentId contentId) {
        watchlists.values().forEach(watchlist -> watchlist.remove(contentId));
    }
}
