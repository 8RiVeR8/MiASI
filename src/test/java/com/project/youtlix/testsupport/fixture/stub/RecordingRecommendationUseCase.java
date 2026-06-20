package com.project.youtlix.testsupport.fixture.stub;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RecordingRecommendationUseCase extends NoOpRecommendationUseCase {

    public UUID requestedViewerId;

    @Override
    public RecommendationList generateFor(ViewerId viewerId) {
        requestedViewerId = viewerId.value();
        return new RecommendationList(
                viewerId,
                Instant.now(),
                List.of(new RecommendedItem(ContentId.newId(), 0.8, RecommendationReason.GLOBAL_POPULARITY))
        );
    }
}
