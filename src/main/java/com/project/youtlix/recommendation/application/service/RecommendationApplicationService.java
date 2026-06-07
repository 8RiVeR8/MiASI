package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for recommendation generation, ratings and watchlist operations.
 */
@Service
public class RecommendationApplicationService implements RecommendationUseCase {

    private static final int DEFAULT_RECOMMENDATION_LIMIT = 20;

    private final RatingRepository ratingRepository;
    private final WatchlistRepository watchlistRepository;
    private final ContentCatalogPort contentCatalogPort;
    private final WatchActivityPort watchActivityPort;
    private final DomainEventPublisher eventPublisher;
    private final RatingService ratingService;
    private final RecommendationEngine recommendationEngine;

    /** Creates recommendation application service with default domain services. */
    public RecommendationApplicationService(
            RatingRepository ratingRepository,
            WatchlistRepository watchlistRepository,
            ContentCatalogPort contentCatalogPort,
            WatchActivityPort watchActivityPort,
            DomainEventPublisher eventPublisher
    ) {
        this(
                ratingRepository,
                watchlistRepository,
                contentCatalogPort,
                watchActivityPort,
                eventPublisher,
                new RatingService(),
                new RecommendationEngine()
        );
    }

    /** Constructor useful for tests with explicit domain services. */
    public RecommendationApplicationService(
            RatingRepository ratingRepository,
            WatchlistRepository watchlistRepository,
            ContentCatalogPort contentCatalogPort,
            WatchActivityPort watchActivityPort,
            DomainEventPublisher eventPublisher,
            RatingService ratingService,
            RecommendationEngine recommendationEngine
    ) {
        this.ratingRepository = ratingRepository;
        this.watchlistRepository = watchlistRepository;
        this.contentCatalogPort = contentCatalogPort;
        this.watchActivityPort = watchActivityPort;
        this.eventPublisher = eventPublisher;
        this.ratingService = ratingService;
        this.recommendationEngine = recommendationEngine;
    }

    @Override
    public RecommendationList generateFor(ViewerId viewerId) {
        boolean hasPersonalizedSignals = hasPersonalizedSignals(viewerId);
        List<ContentId> candidates = contentCatalogPort.popularContent(DEFAULT_RECOMMENDATION_LIMIT);
        return recommendationEngine.generateFor(viewerId, hasPersonalizedSignals, candidates);
    }

    @Override
    public void rate(ViewerId viewerId, ContentId contentId, StarRating stars) {
        Rating rating = ratingService.rate(
                ratingRepository.ofViewerAndContent(viewerId, contentId),
                viewerId,
                contentId,
                stars
        );
        ratingRepository.save(rating);
        eventPublisher.publishAll(rating.occurredEvents());
    }

    @Override
    public void addToWatchlist(ViewerId viewerId, ContentId contentId) {
        Watchlist watchlist = watchlistRepository.ofViewer(viewerId)
                .orElseGet(() -> new Watchlist(WatchlistId.newId(), viewerId));
        watchlist.add(contentId);
        watchlistRepository.save(watchlist);
        eventPublisher.publishAll(watchlist.occurredEvents());
    }

    @Override
    public void removeFromWatchlist(ViewerId viewerId, ContentId contentId) {
        Watchlist watchlist = watchlistRepository.ofViewer(viewerId)
                .orElseThrow(() -> new IllegalArgumentException("watchlist not found for viewer: " + viewerId.value()));
        watchlist.remove(contentId);
        watchlistRepository.save(watchlist);
    }

    private boolean hasPersonalizedSignals(ViewerId viewerId) {
        return !ratingRepository.ofViewer(viewerId).isEmpty()
                || watchlistRepository.ofViewer(viewerId).map(watchlist -> !watchlist.isEmpty()).orElse(false)
                || !watchActivityPort.watchedBy(viewerId).isEmpty();
    }
}
