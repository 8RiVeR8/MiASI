package com.project.youtlix.recommendation.domain.model;

/** Value object representing a single recommended item. */
public record RecommendedItem(ContentId contentId, double score, RecommendationReason reason) {
    public RecommendedItem {
        if (contentId == null) throw new IllegalArgumentException("Content id is required");
        if (reason == null) throw new IllegalArgumentException("Recommendation reason is required");
    }
}
