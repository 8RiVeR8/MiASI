package com.project.youtlix.recommendation.domain.model;

/**
 * One item in generated recommendation list.
 *
 * @param contentId recommended content id
 * @param score computed recommendation score
 * @param reason recommendation reason
 */
public record RecommendedItem(ContentId contentId, double score, RecommendationReason reason) {
}
