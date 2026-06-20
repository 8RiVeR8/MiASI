package com.project.youtlix.unit.recommendation.infrastructure;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.event.ContentRemoved;
import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.infrastructure.in.event.ContentRemovedEventHandler;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ContentRemovedEventHandlerUnitTest {

    @Test
    void handlesContentRemovedByClearingWatchlists() {
        RecordingRecommendationUseCase useCase = new RecordingRecommendationUseCase();
        ContentRemovedEventHandler handler = new ContentRemovedEventHandler(useCase);
        UUID contentId = UUID.randomUUID();
        ContentRemoved event = new ContentRemoved(new ContentId(contentId), Instant.now());

        assertThat(handler.supports(event)).isTrue();
        handler.handle(event);

        assertThat(useCase.removedContentId.value()).isEqualTo(contentId);
    }

    static class RecordingRecommendationUseCase implements RecommendationUseCase {
        com.project.youtlix.recommendation.domain.model.ContentId removedContentId;

        @Override
        public com.project.youtlix.recommendation.domain.model.RecommendationList generateFor(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId
        ) {
            return null;
        }

        @Override
        public java.util.List<com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse> toContentResponses(
                com.project.youtlix.recommendation.domain.model.RecommendationList recommendations
        ) {
            return java.util.List.of();
        }

        @Override
        public void rate(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId,
                com.project.youtlix.recommendation.domain.model.StarRating stars
        ) {
        }

        @Override
        public void addToWatchlist(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId
        ) {
        }

        @Override
        public void removeFromWatchlist(
                com.project.youtlix.recommendation.domain.model.ViewerId viewerId,
                com.project.youtlix.recommendation.domain.model.ContentId contentId
        ) {
        }

        @Override
        public void removeFromWatchlists(com.project.youtlix.recommendation.domain.model.ContentId contentId) {
            this.removedContentId = contentId;
        }
    }
}
