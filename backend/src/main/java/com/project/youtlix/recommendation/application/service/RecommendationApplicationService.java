package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.*;
import com.project.youtlix.recommendation.domain.model.event.RecommendationsGenerated;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import org.springframework.stereotype.Service;
import java.time.Instant;

/** Application service implementing recommendation use cases PU11-PU13. */
@Service
public class RecommendationApplicationService implements RecommendationUseCase {
    private final RatingRepository ratings;
    private final WatchlistRepository watchlists;
    private final RatingService ratingService;
    private final RecommendationEngine engine;
    private final GlobalPopularityStrategy globalStrategy;
    private final PersonalizedStrategy personalizedStrategy;
    private final DomainEventPublisher eventPublisher;

    public RecommendationApplicationService(RatingRepository ratings, WatchlistRepository watchlists,
            RatingService ratingService, RecommendationEngine engine, GlobalPopularityStrategy globalStrategy,
            PersonalizedStrategy personalizedStrategy, DomainEventPublisher eventPublisher) {
        this.ratings = ratings; this.watchlists = watchlists; this.ratingService = ratingService;
        this.engine = engine; this.globalStrategy = globalStrategy; this.personalizedStrategy = personalizedStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Override public RecommendationList generateFor(ViewerId viewerId) {
        boolean personalized = !ratings.ofViewer(viewerId).isEmpty() || watchlists.ofViewer(viewerId).map(w -> !w.isEmpty()).orElse(false);
        RecommendationList list = engine.generateFor(viewerId, personalized ? personalizedStrategy : globalStrategy);
        eventPublisher.publish(new RecommendationsGenerated(viewerId, list.items().size(), Instant.now()));
        return list;
    }

    @Override public void rate(ViewerId viewerId, ContentId contentId, StarRating stars) {
        Rating rating = ratingService.rate(ratings.ofViewerAndContent(viewerId, contentId).orElse(null), viewerId, contentId, stars);
        ratings.save(rating);
        eventPublisher.publishAll(rating.occurredEvents());
    }

    @Override public void addToWatchlist(ViewerId viewerId, ContentId contentId) {
        Watchlist watchlist = watchlists.ofViewer(viewerId).orElseGet(() -> Watchlist.emptyFor(viewerId));
        watchlist.add(contentId);
        watchlists.save(watchlist);
        eventPublisher.publishAll(watchlist.occurredEvents());
    }

    @Override public void removeFromWatchlist(ViewerId viewerId, ContentId contentId) {
        Watchlist watchlist = watchlists.ofViewer(viewerId).orElseGet(() -> Watchlist.emptyFor(viewerId));
        watchlist.remove(contentId);
        watchlists.save(watchlist);
    }
}
