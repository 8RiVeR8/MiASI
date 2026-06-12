package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.recommendation.domain.model.WatchlistId;
import com.project.youtlix.recommendation.domain.model.event.RecommendationsGenerated;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    @Autowired
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
        List<Rating> ratings = ratingRepository.ofViewer(viewerId);
        Optional<Watchlist> watchlist = watchlistRepository.ofViewer(viewerId);
        List<WatchActivity> watchActivities = watchActivityPort.watchedBy(viewerId);

        Map<Genre, Integer> genreScore = new HashMap<>();
        Set<ContentId> alreadyKnown = new HashSet<>();

        ratings.forEach(rating -> {
            alreadyKnown.add(rating.contentId());
            if (rating.stars().isPositive()) {
                scoreGenre(genreScore, rating.contentId(), rating.stars().value());
            }
        });
        watchlist.ifPresent(existing -> existing.items().forEach(item -> {
            alreadyKnown.add(item.contentId());
            scoreGenre(genreScore, item.contentId(), 2);
        }));
        watchActivities.stream()
                .filter(WatchActivity::completed)
                .forEach(activity -> {
                    ContentId contentId = new ContentId(activity.contentId().value());
                    alreadyKnown.add(contentId);
                    scoreGenre(genreScore, contentId, 3);
                });

        boolean hasPersonalizedSignals = !genreScore.isEmpty();
        List<ContentId> candidates = hasPersonalizedSignals
                ? personalizedCandidates(genreScore, alreadyKnown)
                : contentCatalogPort.popularContent(DEFAULT_RECOMMENDATION_LIMIT);

        RecommendationList recommendations = recommendationEngine.generateFor(
                viewerId,
                hasPersonalizedSignals,
                candidates
        );
        eventPublisher.publish(new RecommendationsGenerated(
                viewerId,
                recommendations.items().size(),
                recommendations.generatedAt()
        ));
        return recommendations;
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

    private void scoreGenre(Map<Genre, Integer> genreScore, ContentId contentId, int score) {
        Genre genre = contentCatalogPort.metadataOf(contentId).genre();
        genreScore.merge(genre, score, Integer::sum);
    }

    private List<ContentId> personalizedCandidates(Map<Genre, Integer> genreScore, Set<ContentId> alreadyKnown) {
        Genre favoriteGenre = genreScore.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow();

        List<ContentId> matching = contentCatalogPort.popularContent(DEFAULT_RECOMMENDATION_LIMIT * 3).stream()
                .filter(contentId -> !alreadyKnown.contains(contentId))
                .filter(contentId -> favoriteGenre.equals(contentCatalogPort.metadataOf(contentId).genre()))
                .limit(DEFAULT_RECOMMENDATION_LIMIT)
                .toList();

        return matching.isEmpty()
                ? contentCatalogPort.popularContent(DEFAULT_RECOMMENDATION_LIMIT)
                : matching;
    }
}
