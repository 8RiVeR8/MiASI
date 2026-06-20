package com.project.youtlix.unit.recommendation.infrastructure;

import com.project.youtlix.recommendation.application.port.in.RecommendationUseCase;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.infrastructure.in.web.WatchlistController;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.FixedIdentityProvider;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class WatchlistControllerUnitTest {

    @Test
    void addAndRemoveDelegateToUseCaseForViewer() {
        RecordingRecommendationUseCase useCase = new RecordingRecommendationUseCase();
        WatchlistController controller = new WatchlistController(useCase, new FixedIdentityProvider(ViewerTestAccount.viewerIdentity()));
        UUID contentId = UUID.randomUUID();

        controller.add(ViewerTestAccount.BEARER, contentId);
        controller.remove(ViewerTestAccount.BEARER, contentId);

        assertThat(useCase.added).isTrue();
        assertThat(useCase.removed).isTrue();
        assertThat(useCase.viewerId.value()).isEqualTo(ViewerTestAccount.VIEWER_ID);
        assertThat(useCase.contentId.value()).isEqualTo(contentId);
    }

    static class RecordingRecommendationUseCase implements RecommendationUseCase {
        ViewerId viewerId;
        ContentId contentId;
        boolean added;
        boolean removed;

        @Override
        public com.project.youtlix.recommendation.domain.model.RecommendationList generateFor(ViewerId viewerId) {
            return null;
        }

        @Override
        public java.util.List<com.project.youtlix.contentlibrary.infrastructure.in.web.ContentResponse> toContentResponses(
                com.project.youtlix.recommendation.domain.model.RecommendationList recommendations
        ) {
            return java.util.List.of();
        }

        @Override
        public void rate(ViewerId viewerId, ContentId contentId, StarRating stars) {
        }

        @Override
        public void addToWatchlist(ViewerId viewerId, ContentId contentId) {
            this.viewerId = viewerId;
            this.contentId = contentId;
            this.added = true;
        }

        @Override
        public void removeFromWatchlist(ViewerId viewerId, ContentId contentId) {
            this.viewerId = viewerId;
            this.contentId = contentId;
            this.removed = true;
        }

        @Override
        public void removeFromWatchlists(ContentId contentId) {
        }
    }
}
