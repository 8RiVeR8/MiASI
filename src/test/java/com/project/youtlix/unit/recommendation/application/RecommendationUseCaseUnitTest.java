package com.project.youtlix.unit.recommendation.application;

import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.service.RecommendationApplicationService;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;
import com.project.youtlix.testsupport.fixture.memory.InMemoryRatingRepository;
import com.project.youtlix.testsupport.fixture.memory.InMemoryWatchlistRepository;
import com.project.youtlix.testsupport.fixture.stub.EmptyContentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RecommendationUseCaseUnitTest {

    @Test
    void recommendationSwitchesToPersonalizedAfterRating() {
        InMemoryRatingRepository ratingRepository = new InMemoryRatingRepository();
        RecommendationApplicationService service = new RecommendationApplicationService(
                ratingRepository,
                new InMemoryWatchlistRepository(),
                stubCatalogPort(),
                viewerId -> List.of(),
                new RecordingDomainEventPublisher(),
                new EmptyContentRepository()
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
                stubCatalogPort(),
                viewerId -> List.of(),
                new RecordingDomainEventPublisher(),
                new EmptyContentRepository()
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
                .satisfies(items -> assertThat(items).hasSize(1));
        assertThat(watchlistRepository.ofViewer(secondViewer)).get()
                .extracting(Watchlist::items)
                .satisfies(items -> assertThat(items).isEmpty());
    }

    private ContentCatalogPort stubCatalogPort() {
        return new ContentCatalogPort() {
            @Override
            public List<ContentId> popularContent(int limit) {
                return List.of(ContentId.newId(), ContentId.newId());
            }

            @Override
            public ContentMetadata metadataOf(ContentId id) {
                return new ContentMetadata(Genre.ACTION, List.of(new Keyword("hero")), 2026, "Hero");
            }
        };
    }
}
