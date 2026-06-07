package com.project.youtlix.controller.recommendation;

import com.project.youtlix.dto.AddToWatchlistRequest;
import com.project.youtlix.entity.recommendation.WatchlistItems;
import com.project.youtlix.service.WatchlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final WatchlistService service;

    public WatchlistController(WatchlistService service) {
        this.service = service;
    }

    @GetMapping("/{viewerId}")
    public List<WatchlistItems> get(@PathVariable UUID viewerId) {
        return service.getUserWatchlist(viewerId);
    }

    @PostMapping("/{viewerId}")
    public void add(@PathVariable UUID viewerId, @RequestBody AddToWatchlistRequest request) {
        service.addToWatchlist(viewerId, request.getContentId());
    }

    @DeleteMapping("/{viewerId}/{contentId}")
    public void delete(@PathVariable UUID viewerId, @PathVariable UUID contentId) {
        service.removeFromWatchlist(viewerId, contentId);
    }
}
