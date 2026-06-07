package com.project.youtlix.recommendation.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for recommendation.watchlists rows.
 */
public interface SpringDataWatchlistJpaRepository extends JpaRepository<WatchlistJpaEntity, UUID> {

    /** Finds watchlist by viewer. */
    Optional<WatchlistJpaEntity> findByViewerId(UUID viewerId);
}
