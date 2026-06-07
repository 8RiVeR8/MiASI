package com.project.youtlix.recommendation.domain.service;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.recommendation.domain.model.RecommendationList;

import java.util.List;

/**
 * Strategy contract for recommendation algorithms.
 */
public interface RecommendationStrategy {

    /** Recommends content for a viewer based on candidate content ids. */
    RecommendationList recommend(ViewerId viewerId, List<ContentId> candidates);
}
