package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
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
        InMemoryWatchlistRepository watchlistRepository = new InMemoryWatchlistRepository();
        RecommendationApplicationService service = new RecommendationApplicationService(
                ratingRepository,
                watchlistRepository,
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

    @Test
    void contentRemovalRemovesContentFromEveryWatchlist() {
        InMemoryWatchlistRepository watchlistRepository = new InMemoryWatchlistRepository();
        RecommendationApplicationService service = new RecommendationApplicationService(
                new InMemoryRatingRepository(),
                watchlistRepository,
                new FakeContentCatalogPort(),
                viewerId -> List.of(),
                new RecordingPublisher()
        );
        ContentId removedContentId = ContentId.newId();
        ContentId remainingContentId = ContentId.newId();
        ViewerId firstViewer = new ViewerId(UUID.randomUUID());
        ViewerId secondViewer = new ViewerId(UUID.randomUUID());
        service.addToWatchlist(firstViewer, removedContentId);
        service.addToWatchlist(firstViewer, remainingContentId);
        service.addToWatchlist(secondViewer, removedContentId);

        service.removeFromWatchlists(removedContentId);

        assertThat(watchlistRepository.ofViewer(firstViewer)).get()
                .extracting(Watchlist::items)
                .asList()
                .singleElement()
                .extracting("contentId")
                .isEqualTo(remainingContentId);
        assertThat(watchlistRepository.ofViewer(secondViewer)).get()
                .extracting(Watchlist::items)
                .asList()
                .isEmpty();
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

        @Override
        public void removeFromWatchlists(ContentId contentId) {
            watchlists.values().forEach(watchlist -> watchlist.remove(contentId));
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
            return new ContentMetadata(Genre.ACTION, List.of(new Keyword("hero")), 2026, "Hero");
        }
    }

    static class RecordingPublisher implements DomainEventPublisher {
        final List<Object> events = new ArrayList<>();

        @Override
        public void publish(Object event) {
            events.add(event);
        }
    }
}
