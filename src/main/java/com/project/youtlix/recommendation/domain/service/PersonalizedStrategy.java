package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.RecommendationReason;
import com.project.youtlix.recommendation.domain.model.RecommendedItem;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recommendation strategy used when viewer has ratings, watchlist or watch activity.
 */
public class PersonalizedStrategy implements RecommendationStrategy {

    /** Creates personalized recommendations from candidate ids. */
    @Override
    public RecommendationList recommend(ViewerId viewerId, List<ContentId> candidates) {
        AtomicInteger rank = new AtomicInteger(0);
        List<RecommendedItem> items = candidates.stream()
                .map(id -> new RecommendedItem(id, 2.0d / (rank.incrementAndGet()), RecommendationReason.PERSONALIZED))
                .toList();
        return new RecommendationList(viewerId, Instant.now(), items);
    }
}
