package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Driving web adapter for PU13 watchlist management.
 */
@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    private final RecommendationUseCase useCase;

    /** Creates watchlist web adapter. */
    public WatchlistController(RecommendationUseCase useCase) {
        this.useCase = useCase;
    }

    /** Adds content to watchlist. */
    @PostMapping("/{viewerId}")
    public void add(@PathVariable UUID viewerId, @RequestBody WatchlistRequest request) {
        useCase.addToWatchlist(new ViewerId(viewerId), new ContentId(request.contentId()));
    }

    /** Removes content from watchlist. */
    @DeleteMapping("/{viewerId}/{contentId}")
    public void remove(@PathVariable UUID viewerId, @PathVariable UUID contentId) {
        useCase.removeFromWatchlist(new ViewerId(viewerId), new ContentId(contentId));
    }
}
