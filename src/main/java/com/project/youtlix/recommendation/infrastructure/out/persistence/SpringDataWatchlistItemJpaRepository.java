package com.project.youtlix.recommendation.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for recommendation.watchlist_items rows.
 */
public interface SpringDataWatchlistItemJpaRepository extends JpaRepository<WatchlistItemJpaEntity, UUID> {

    /** Finds items by watchlist id. */
    List<WatchlistItemJpaEntity> findAllByWatchlistId(UUID watchlistId);

    /** Deletes items by watchlist id. */
    void deleteAllByWatchlistId(UUID watchlistId);
}
