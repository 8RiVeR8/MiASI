package com.project.youtlix.recommendation.application.service;

import com.project.youtlix.recommendation.application.port.out.ContentCatalogPort;
import com.project.youtlix.recommendation.domain.model.*;
import com.project.youtlix.recommendation.domain.service.RecommendationStrategy;
import org.springframework.stereotype.Component;
import java.time.Instant;

/** Global popularity implementation of the PU11 strategy. */
@Component
public class GlobalPopularityStrategy implements RecommendationStrategy {
    private final ContentCatalogPort catalog;
    public GlobalPopularityStrategy(ContentCatalogPort catalog) { this.catalog = catalog; }
    @Override public RecommendationList recommend(ViewerId viewerId) {
        return new RecommendationList(viewerId, Instant.now(), catalog.popularContent(10).stream()
                .map(id -> new RecommendedItem(id, 1.0, RecommendationReason.GLOBAL_POPULARITY)).toList());
    }
}
