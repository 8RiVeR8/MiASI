package com.project.youtlix.recommendation.infrastructure.out.persistence;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.recommendation.domain.model.WatchlistItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Driven adapter for recommendation watchlists stored in Supabase Postgres.
 */
@Repository
public class SupabaseWatchlistRepository implements WatchlistRepository {

    private final SpringDataWatchlistJpaRepository watchlistRepository;
    private final SpringDataWatchlistItemJpaRepository itemRepository;

    /** Creates watchlist persistence adapter. */
    public SupabaseWatchlistRepository(
            SpringDataWatchlistJpaRepository watchlistRepository,
            SpringDataWatchlistItemJpaRepository itemRepository
    ) {
        this.watchlistRepository = watchlistRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public void save(Watchlist watchlist) {
        watchlistRepository.save(new WatchlistJpaEntity(watchlist.id().value(), watchlist.viewerId().value()));
        itemRepository.deleteAllByWatchlistId(watchlist.id().value());
        watchlist.items().forEach(item -> itemRepository.save(new WatchlistItemJpaEntity(
                UUID.randomUUID(),
                watchlist.id().value(),
                item.contentId().value(),
                item.addedOn()
        )));
    }

    @Override
    public Optional<Watchlist> ofViewer(ViewerId viewerId) {
        return watchlistRepository.findByViewerId(viewerId.value()).map(row -> {
            List<WatchlistItem> items = itemRepository.findAllByWatchlistId(row.id()).stream()
                    .map(item -> new WatchlistItem(new ContentId(item.contentId()), item.addedOn()))
                    .toList();
            return new Watchlist(new WatchlistId(row.id()), new ViewerId(row.viewerId()), items);
        });
    }
}
