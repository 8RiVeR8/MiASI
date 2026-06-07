package com.project.youtlix.service;

import com.project.youtlix.entity.recommendation.Watchlist;
import com.project.youtlix.entity.recommendation.WatchlistItems;
import com.project.youtlix.repository.recommendation.WatchlistItemRepository;
import com.project.youtlix.repository.recommendation.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository itemRepository;

    public WatchlistService(
            WatchlistRepository watchlistRepository,
            WatchlistItemRepository itemRepository
    ) {
        this.watchlistRepository = watchlistRepository;
        this.itemRepository = itemRepository;
    }

    public List<WatchlistItems> getUserWatchlist(UUID viewerId) {

        Watchlist watchlist = watchlistRepository
                .findByViewerId(viewerId)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));

        return itemRepository.findAllByWatchlistId(watchlist.getId());
    }

    public void addToWatchlist(UUID viewerId, UUID contentId) {

        Watchlist watchlist = watchlistRepository
                .findByViewerId(viewerId)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));

        itemRepository.findByWatchlistIdAndContentId(watchlist.getId(), contentId)
                .ifPresent(i -> {
                    throw new RuntimeException("Already in watchlist");
                });

        WatchlistItems item = new WatchlistItems();
        item.setId(UUID.randomUUID());
        item.setWatchlistId(watchlist.getId());
        item.setContentId(contentId);

        itemRepository.save(item);
    }

    public void removeFromWatchlist(UUID viewerId, UUID contentId) {

        Watchlist watchlist = watchlistRepository
                .findByViewerId(viewerId)
                .orElseThrow(() -> new RuntimeException("Watchlist not found"));

        itemRepository.deleteByWatchlistIdAndContentId(watchlist.getId(), contentId);
    }
}
