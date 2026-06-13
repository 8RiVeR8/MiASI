package com.project.youtlix.recommendation.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * Result of recommendation generation for one viewer.
 *
 * @param viewerId viewer receiving recommendations
 * @param generatedAt generation time
 * @param items recommended items
 */
public record RecommendationList(ViewerId viewerId, Instant generatedAt, List<RecommendedItem> items) {

    /** Creates immutable recommendation result. */
    public RecommendationList {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
