package com.project.youtlix.recommendation.domain.model;

import java.time.Instant;
import java.util.List;

/** Value object returned by PU11 recommendation generation. */
public record RecommendationList(ViewerId viewerId, Instant generatedAt, List<RecommendedItem> items) {
    public RecommendationList {
        if (viewerId == null) throw new IllegalArgumentException("Viewer id is required");
        generatedAt = generatedAt == null ? Instant.now() : generatedAt;
        items = items == null ? List.of() : List.copyOf(items);
    }
}
