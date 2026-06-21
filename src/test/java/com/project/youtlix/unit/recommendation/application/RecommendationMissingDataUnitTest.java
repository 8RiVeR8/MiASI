package com.project.youtlix.unit.recommendation.application;

import com.project.youtlix.recommendation.application.service.RecommendationApplicationService;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.memory.InMemoryRatingRepository;
import com.project.youtlix.testsupport.fixture.memory.InMemoryWatchlistRepository;
import com.project.youtlix.testsupport.fixture.stub.RecommendationTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RecommendationMissingDataUnitTest {

    @Test
    void getUserRatingForContentReturnsEmptyWhenNotRated() {
        RecommendationApplicationService service = RecommendationTestSupport.service(
                new InMemoryRatingRepository(),
                new InMemoryWatchlistRepository()
        );

        assertThat(service.getUserRatingForContent(
                ViewerTestAccount.recommendationViewerId(),
                ContentId.newId()
        )).isEmpty();
    }

    @Test
    void isInWatchlistReturnsFalseForUnknownContent() {
        RecommendationApplicationService service = RecommendationTestSupport.service(
                new InMemoryRatingRepository(),
                new InMemoryWatchlistRepository()
        );

        assertThat(service.isInWatchlist(
                ViewerTestAccount.recommendationViewerId(),
                ContentId.newId()
        )).isFalse();
    }
}
