package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.ViewerId;

/** Domain service selecting and running a recommendation strategy for PU11. */
public class RecommendationEngine {
    /** Generates recommendations using the selected strategy. */
    public RecommendationList generateFor(ViewerId viewerId, RecommendationStrategy strategy) { return strategy.recommend(viewerId); }
}
