package com.project.youtlix.repository.recommendation;

import com.project.youtlix.entity.recommendation.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WatchlistRepository extends JpaRepository<Watchlist, UUID> {

    Optional<Watchlist> findByViewerId(UUID viewerId);
}
