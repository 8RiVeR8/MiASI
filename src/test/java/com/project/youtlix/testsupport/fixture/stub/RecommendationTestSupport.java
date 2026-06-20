package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.contentlibrary.application.port.in.ContentMetadata;
import com.project.youtlix.contentlibrary.domain.model.Genre;
import com.project.youtlix.contentlibrary.domain.model.Keyword;
import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.application.service.RecommendationApplicationService;
import com.project.youtlix.recommendation.application.port.out.RatingRepository;
import com.project.youtlix.recommendation.application.port.out.WatchlistRepository;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.testsupport.fixture.RecordingDomainEventPublisher;

import java.util.List;

public final class RecommendationTestSupport {

    private RecommendationTestSupport() {
    }

    public static RecommendationApplicationService service(
            RatingRepository ratingRepository,
            WatchlistRepository watchlistRepository
    ) {
        return new RecommendationApplicationService(
                ratingRepository,
                watchlistRepository,
                catalogPort(),
                viewerId -> List.of(),
                new RecordingDomainEventPublisher(),
                new EmptyContentRepository()
        );
    }

    public static ContentCatalogPort catalogPort() {
        return new ContentCatalogPort() {
            @Override
            public List<ContentId> popularContent(int limit) {
                return List.of();
            }

            @Override
            public ContentMetadata metadataOf(ContentId id) {
                return new ContentMetadata(Genre.DOCUMENTARY, List.of(new Keyword("unit-test")), 2026, "fixture");
            }
        };
    }
}
