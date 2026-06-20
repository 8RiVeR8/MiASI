package com.project.youtlix.unit.recommendation.application;

import com.project.youtlix.recommendation.application.service.RecommendationApplicationService;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.Rating;
import com.project.youtlix.recommendation.domain.model.StarRating;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.memory.InMemoryRatingRepository;
import com.project.youtlix.testsupport.fixture.memory.InMemoryWatchlistRepository;
import com.project.youtlix.testsupport.fixture.stub.RecommendationTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ViewerRatingCrudUnitTest {

    @Test
    void ratingLifecycle_createReadModifyRead_forViewer() {
        ViewerId viewer = ViewerTestAccount.recommendationViewerId();
        ContentId contentId = ContentId.newId();
        InMemoryRatingRepository ratingRepository = new InMemoryRatingRepository();
        RecommendationApplicationService service = RecommendationTestSupport.service(ratingRepository, new InMemoryWatchlistRepository());

        service.rate(viewer, contentId, new StarRating(3));

        Rating afterCreate = ratingRepository.ofViewerAndContent(viewer, contentId).orElseThrow();
        assertThat(afterCreate.stars().value()).isEqualTo(3);
        assertThat(ratingRepository.ofViewer(viewer)).hasSize(1);

        service.rate(viewer, contentId, new StarRating(5));

        Rating afterUpdate = ratingRepository.ofViewerAndContent(viewer, contentId).orElseThrow();
        assertThat(afterUpdate.stars().value()).isEqualTo(5);
        assertThat(ratingRepository.ofViewer(viewer)).hasSize(1);
    }
}
