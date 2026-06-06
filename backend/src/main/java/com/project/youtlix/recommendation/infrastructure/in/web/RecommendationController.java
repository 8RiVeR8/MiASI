package com.project.youtlix.recommendation.infrastructure.in.web;

import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/** Driving web adapter exposing recommendation endpoints from PU11-PU13. */
@RestController
@RequestMapping
public class RecommendationController {
    private final RecommendationUseCase recommendation;
    public RecommendationController(RecommendationUseCase recommendation) { this.recommendation = recommendation; }

    @GetMapping("/recommendations")
    public RecommendationList recommendations(@RequestParam UUID viewerId) { return recommendation.generateFor(new ViewerId(viewerId)); }

    @PostMapping("/rate/{contentId}")
    public ResponseEntity<Void> rate(@PathVariable UUID contentId, @RequestBody RateRequest request) {
        recommendation.rate(new ViewerId(request.viewerId()), new ContentId(contentId), new StarRating(request.stars()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/watchlist/{contentId}")
    public ResponseEntity<Void> addToWatchlist(@PathVariable UUID contentId, @RequestBody WatchlistRequest request) {
        recommendation.addToWatchlist(new ViewerId(request.viewerId()), new ContentId(contentId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/watchlist/{contentId}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable UUID contentId, @RequestBody WatchlistRequest request) {
        recommendation.removeFromWatchlist(new ViewerId(request.viewerId()), new ContentId(contentId));
        return ResponseEntity.noContent().build();
    }

    public record RateRequest(UUID viewerId, int stars) {}
    public record WatchlistRequest(UUID viewerId) {}
}
