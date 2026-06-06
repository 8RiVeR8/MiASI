package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.recommendation.domain.model.RecommendationList;
import com.project.youtlix.recommendation.domain.model.ViewerId;

/** Strategy contract for PU11 recommendation generation. */
public interface RecommendationStrategy {
    /** Recommends content for a viewer. */
    RecommendationList recommend(ViewerId viewerId);
}
