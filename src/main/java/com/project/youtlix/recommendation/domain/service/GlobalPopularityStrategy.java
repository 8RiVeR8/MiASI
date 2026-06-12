package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.recommendation.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;
import com.project.youtlix.recommendation.domain.model.ViewerId;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recommendation strategy based on globally popular content.
 */
public class GlobalPopularityStrategy implements RecommendationStrategy {

    /** Creates recommendations from popular candidates. */
    @Override
    public RecommendationList recommend(ViewerId viewerId, List<ContentId> candidates) {
        AtomicInteger rank = new AtomicInteger(0);
        List<RecommendedItem> items = candidates.stream()
                .map(id -> new RecommendedItem(id, 1.0d / (rank.incrementAndGet()), RecommendationReason.GLOBAL_POPULARITY))
                .toList();
        return new RecommendationList(viewerId, Instant.now(), items);
    }
}
