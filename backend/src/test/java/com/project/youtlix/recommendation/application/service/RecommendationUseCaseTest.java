package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.common.infrastructure.event.InMemoryDomainEventBus;
import com.project.youtlix.recommendation.application.port.out.*;
import com.project.youtlix.recommendation.domain.model.*;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import com.project.youtlix.recommendation.infrastructure.out.persistence.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Application tests for PU11-PU13 recommendation flows. */
class RecommendationUseCaseTest {
    @Test
    void recommendationSwitchesToPersonalizedAfterRating() {
        ContentId contentId = new ContentId(UUID.randomUUID());
        ContentCatalogPort catalog = new ContentCatalogPort() {
            @Override public List<ContentId> popularContent(int limit) { return List.of(contentId); }
            @Override public ContentMetadata metadataOf(ContentId id) { return new ContentMetadata("DRAMA", List.of("drama"), 2026); }
        };
        WatchActivityPort watchActivity = viewerId -> List.of();
        RecommendationApplicationService service = new RecommendationApplicationService(
                new InMemoryRatingRepository(), new InMemoryWatchlistRepository(), new RatingService(), new RecommendationEngine(),
                new GlobalPopularityStrategy(catalog), new PersonalizedStrategy(catalog, watchActivity), new InMemoryDomainEventBus());
        ViewerId viewerId = new ViewerId(UUID.randomUUID());

        assertEquals(RecommendationReason.GLOBAL_POPULARITY, service.generateFor(viewerId).items().getFirst().reason());
        service.rate(viewerId, contentId, new StarRating(5));
        assertEquals(RecommendationReason.PERSONALIZED, service.generateFor(viewerId).items().getFirst().reason());
    }
}
