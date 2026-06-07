package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;

import java.util.List;

/**
 * Domain service selecting the recommendation strategy.
 */
public class RecommendationEngine {

    private final RecommendationStrategy personalizedStrategy;
    private final RecommendationStrategy globalPopularityStrategy;

    /** Creates recommendation engine with default strategies. */
    public RecommendationEngine() {
        this(new PersonalizedStrategy(), new GlobalPopularityStrategy());
    }

    /** Creates recommendation engine with explicit strategies. */
    public RecommendationEngine(
            RecommendationStrategy personalizedStrategy,
            RecommendationStrategy globalPopularityStrategy
    ) {
        this.personalizedStrategy = personalizedStrategy;
        this.globalPopularityStrategy = globalPopularityStrategy;
    }

    /**
     * Generates recommendations using personalized strategy when signals exist.
     */
    public RecommendationList generateFor(ViewerId viewerId, boolean hasPersonalizedSignals, List<ContentId> candidates) {
        RecommendationStrategy strategy = hasPersonalizedSignals ? personalizedStrategy : globalPopularityStrategy;
        return strategy.recommend(viewerId, candidates);
    }
}
