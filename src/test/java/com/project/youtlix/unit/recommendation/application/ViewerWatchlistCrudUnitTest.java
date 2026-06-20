package com.project.youtlix.unit.recommendation.application;

import com.project.youtlix.recommendation.application.service.RecommendationApplicationService;
import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.model.Watchlist;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import com.project.youtlix.testsupport.fixture.memory.InMemoryRatingRepository;
import com.project.youtlix.testsupport.fixture.memory.InMemoryWatchlistRepository;
import com.project.youtlix.testsupport.fixture.stub.RecommendationTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ViewerWatchlistCrudUnitTest {

    @Test
    void watchlistLifecycle_createReadDeleteVerifyRemoved_forViewer() {
        ViewerId viewer = ViewerTestAccount.recommendationViewerId();
        ContentId contentId = ContentId.newId();
        InMemoryWatchlistRepository watchlistRepository = new InMemoryWatchlistRepository();
        RecommendationApplicationService service = RecommendationTestSupport.service(new InMemoryRatingRepository(), watchlistRepository);

        service.addToWatchlist(viewer, contentId);

        Watchlist afterCreate = watchlistRepository.ofViewer(viewer).orElseThrow();
        assertThat(afterCreate.contains(contentId)).isTrue();
        assertThat(afterCreate.items()).hasSize(1);

        service.removeFromWatchlist(viewer, contentId);

        Watchlist afterDelete = watchlistRepository.ofViewer(viewer).orElseThrow();
        assertThat(afterDelete.contains(contentId)).isFalse();
        assertThat(afterDelete.isEmpty()).isTrue();
    }
}
