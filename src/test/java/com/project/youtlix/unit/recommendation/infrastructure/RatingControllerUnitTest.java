package com.project.youtlix.unit.recommendation.infrastructure;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.infrastructure.in.web.RatingController;
import com.project.youtlix.recommendation.infrastructure.in.web.RatingRequest;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.FixedIdentityProvider;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.stub.NoOpRecommendationUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RatingControllerUnitTest {

    @Test
    void rateDelegatesToUseCaseForViewer() {
        RecordingRecommendationUseCase useCase = new RecordingRecommendationUseCase();
        RatingController controller = new RatingController(useCase, new FixedIdentityProvider(ViewerTestAccount.viewerIdentity()));
        UUID contentId = UUID.randomUUID();

        controller.rate(ViewerTestAccount.BEARER, contentId, new RatingRequest(4));

        assertThat(useCase.viewerId.value()).isEqualTo(ViewerTestAccount.VIEWER_ID);
        assertThat(useCase.contentId.value()).isEqualTo(contentId);
        assertThat(useCase.stars.value()).isEqualTo(4);
    }

    static class RecordingRecommendationUseCase extends NoOpRecommendationUseCase {
        ViewerId viewerId;
        ContentId contentId;
        StarRating stars;

        @Override
        public void rate(ViewerId viewerId, ContentId contentId, StarRating stars) {
            this.viewerId = viewerId;
            this.contentId = contentId;
            this.stars = stars;
        }
    }
}
