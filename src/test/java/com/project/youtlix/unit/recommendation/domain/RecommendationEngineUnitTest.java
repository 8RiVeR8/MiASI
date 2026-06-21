package com.project.youtlix.unit.recommendation.domain;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RecommendationEngineUnitTest {

    private final RecommendationEngine engine = new RecommendationEngine();

    @Test
    void usesPersonalizedStrategyWhenSignalsExist() {
        ViewerId viewerId = new ViewerId(UUID.randomUUID());
        List<ContentId> candidates = List.of(ContentId.newId(), ContentId.newId());

        var recommendations = engine.generateFor(viewerId, true, candidates);

        assertThat(recommendations.items()).hasSize(2);
        assertThat(recommendations.items().getFirst().reason()).isEqualTo(RecommendationReason.PERSONALIZED);
    }

    @Test
    void usesGlobalPopularityWhenNoSignals() {
        ViewerId viewerId = new ViewerId(UUID.randomUUID());

        var recommendations = engine.generateFor(viewerId, false, List.of(ContentId.newId()));

        assertThat(recommendations.items()).hasSize(1);
        assertThat(recommendations.items().getFirst().reason()).isEqualTo(RecommendationReason.GLOBAL_POPULARITY);
    }
}
