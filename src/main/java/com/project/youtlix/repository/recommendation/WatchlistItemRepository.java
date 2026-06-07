package com.project.youtlix.repository.recommendation;

import com.project.youtlix.entity.recommendation.WatchlistItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItems, UUID> {

    List<WatchlistItems> findAllByWatchlistId(UUID watchlistId);

    Optional<WatchlistItems> findByWatchlistIdAndContentId(UUID watchlistId, UUID contentId);

    void deleteByWatchlistIdAndContentId(UUID watchlistId, UUID contentId);
}
