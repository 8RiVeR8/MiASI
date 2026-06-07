package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.model.DomainEvent;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.ContentMetadata;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationUseCaseTest {

    @Test
    void recommendationSwitchesToPersonalizedAfterRating() {
        InMemoryRatingRepository ratingRepository = new InMemoryRatingRepository();
        RecommendationApplicationService service = new RecommendationApplicationService(
                ratingRepository,
                new InMemoryWatchlistRepository(),
                new FakeContentCatalogPort(),
                viewerId -> List.of(),
                new RecordingPublisher()
        );
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        ContentId contentId = ContentId.newId();

        RecommendationList beforeRating = service.generateFor(viewerId);
        service.rate(viewerId, contentId, new StarRating(5));
        RecommendationList afterRating = service.generateFor(viewerId);

        assertThat(beforeRating.items()).allMatch(item -> item.reason() == RecommendationReason.GLOBAL_POPULARITY);
        assertThat(afterRating.items()).allMatch(item -> item.reason() == RecommendationReason.PERSONALIZED);
        assertThat(ratingRepository.ofViewer(viewerId)).hasSize(1);
    }

    static class InMemoryRatingRepository implements RatingRepository {
        private final Map<String, Rating> ratings = new LinkedHashMap<>();

        @Override
        public void save(Rating rating) {
            ratings.put(key(rating.viewerId(), rating.contentId()), rating);
        }

        @Override
        public Optional<Rating> ofViewerAndContent(ViewerId viewerId, ContentId contentId) {
            return Optional.ofNullable(ratings.get(key(viewerId, contentId)));
        }

        @Override
        public List<Rating> ofViewer(ViewerId viewerId) {
            return ratings.values().stream().filter(rating -> rating.viewerId().equals(viewerId)).toList();
        }

        private String key(ViewerId viewerId, ContentId contentId) {
            return viewerId.value() + ":" + contentId.value();
        }
    }

    static class InMemoryWatchlistRepository implements WatchlistRepository {
        private final Map<ViewerId, Watchlist> watchlists = new LinkedHashMap<>();

        @Override
        public void save(Watchlist watchlist) {
            watchlists.put(watchlist.viewerId(), watchlist);
        }

        @Override
        public Optional<Watchlist> ofViewer(ViewerId viewerId) {
            return Optional.ofNullable(watchlists.get(viewerId));
        }
    }

    static class FakeContentCatalogPort implements ContentCatalogPort {
        private final List<ContentId> candidates = List.of(ContentId.newId(), ContentId.newId());

        @Override
        public List<ContentId> popularContent(int limit) {
            return candidates.stream().limit(limit).toList();
        }

        @Override
        public ContentMetadata metadataOf(ContentId id) {
            return new ContentMetadata("ACTION", List.of("hero"), 2026);
        }
    }

    static class RecordingPublisher implements DomainEventPublisher {
        final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            events.add(event);
        }
    }
}
